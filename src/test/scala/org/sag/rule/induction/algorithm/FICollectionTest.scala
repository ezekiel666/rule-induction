package org.sag.rule.induction.algorithm

import java.util.Date

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import org.sag.rule.induction.common.Messages.{GetSupport, SetSupport}
import org.scalatest.{FlatSpec, Matchers}
import org.slf4j.LoggerFactory

/**
 * @author Cezary Pawlowski, Maciej Korpalski
 */
class FICollectionTest extends FlatSpec with Matchers {
  val log = LoggerFactory.getLogger(classOf[FICollectionTest])

  "fi collection" should "work" in {
    val itemsets = List(Itemset(List(1, 2)), Itemset(List(1, 2, 3)), Itemset(List(1, 2, 3)), Itemset(List(1, 3)), Itemset(List(1, 3)))
    val fiCollection = new FICollection(itemsets, new Date(), new Date(), Set())
    fiCollection.show()
  }

  "fi collection" should "support polling" in {
    val system = ActorSystem()
    system.actorOf(Props[TestWorker], name = "worker")
    val worker = system.actorSelection("/user/worker")

    val itemsets = List(Itemset(List(1)))
    val fiCollection = new FICollection(itemsets, new Date(), new Date(), Set(worker))
    fiCollection.show()
  }
}

// test dependencies

/**
 * @author Cezary Pawlowski, Maciej Korpalski
 */
class TestWorker extends Actor with ActorLogging {
  override def receive: Receive = {
    case GetSupport(itemset, start, stop) =>
      log.info(s"GetSupport($itemset, $start, $stop) from $sender")
      sender ! SetSupport(itemset, start, stop, 1, 1)

    case other =>
      log.warning(s"unknown message $other from $sender")
  }
}
