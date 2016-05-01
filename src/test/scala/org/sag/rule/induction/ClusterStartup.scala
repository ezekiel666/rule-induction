package org.sag.rule.induction

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.testkit.{TestKit}
import org.scalatest.{Matchers}
import scala.concurrent.duration._

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
