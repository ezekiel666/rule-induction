package org.sag.rule.induction

import akka.event.slf4j.Logger
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, FlatSpec}
import scala.concurrent.duration._

/**
 * @author Cezary Pawlowski
 */
class MasterTest extends FlatSpec with Matchers with BeforeAndAfterAll {
  val log = Logger("MasterTest")
  val nodes = ClusterStartup.run(3)
  log.info("all nodes are up")

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
