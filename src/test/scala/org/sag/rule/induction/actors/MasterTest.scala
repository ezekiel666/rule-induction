package org.sag.rule.induction.actors

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.testkit.{ImplicitSender, TestKit}
import org.sag.rule.induction._
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

/**
 * @author Cezary Pawlowski
 */
class MasterTest extends FlatSpec with Matchers with BeforeAndAfterAll {
  val log = LoggerFactory.getLogger(classOf[MasterTest])
  val nodes = ClusterStartup.run(3)

  override protected def beforeAll() = {
    Thread.sleep(2000)
  }

  override protected def afterAll {
    nodes foreach { n =>
      TestKit.shutdownActorSystem(n)
    }
  }

  "master" should "finish processing" in {
    new TestKit(nodes(0)) with ImplicitSender {
      def run {
        val master = system.actorSelection("/user/master")
        master ! StartProcessing()
        expectMsg(10 seconds, ProcessingFinished())
      }
    }.run
  }
}

// test dependencies

/**
 * @author Cezary Pawlowski
 */
case class StartProcessing()

/**
 * @author Cezary Pawlowski
 */
case class ProcessingFinished()

/**
 * @author Cezary Pawlowski
 */
class TestMaster extends Master {
  override def preStart(): Unit = ()

  override def receive: Receive = {
    case StartProcessing() =>
      log.info(s"StartProcessing() from $sender")
      start()
    case other =>
      super.receive(other)
  }

  override def processingFinished(): Unit = {
    context.system.actorSelection("/system/testActor*") ! ProcessingFinished()
  }
}

/**
 * @author Cezary Pawlowski
 */
object TestMasterNode extends AbstractMasterNode[TestMaster] {}

/**
 * @author Cezary Pawlowski
 */
object ClusterStartup {
  def run(workersCount : Int) : Array[ActorSystem] = {
    val seedUrl = Context.getConfig.getStringList("akka.cluster.seed-nodes").get(0)
    val seedPort = seedUrl.substring(seedUrl.lastIndexOf(':') + 1).toInt
    val seed = TestMasterNode.start(seedPort) // master

    Thread.sleep(1000)

    new TestKit(seed) with Matchers {
      def impl : Array[ActorSystem] = {
        Cluster(system).subscribe(testActor, classOf[MemberUp])
        expectMsgClass(classOf[CurrentClusterState])

        Cluster(system) join Cluster(system).selfAddress

        val nodes = { for(i <- 0 until workersCount) yield WorkerNode.start(0) }.toArray
        val ports = { for(n <- nodes) yield Cluster(n).selfAddress.port }.toSet
        receiveN(workersCount + 1, (workersCount + 1) * 5 seconds).collect { case MemberUp(m) => m.address.port }.toSet should be(ports ++ Some(Some(seedPort)))

        Cluster(system).unsubscribe(testActor)

        Array(system) ++ nodes
      }
    }.impl
  }
}