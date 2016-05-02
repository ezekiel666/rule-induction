package org.sag.rule.induction

import akka.event.slf4j.Logger
import akka.testkit.{ImplicitSender, TestKit}
import org.sag.rule.induction.algorithm.{Itemset, FICollection}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.duration._

/**
 * @author Cezary Pawlowski
 */
class FICollectionTest extends FlatSpec with Matchers {
  val log = Logger("FICollectionTest")

  "fi collection" should "work" in {
    val itemsets = List(Itemset(List(1, 2)), Itemset(List(1, 2, 3)), Itemset(List(1, 2, 3)), Itemset(List(1, 3)), Itemset(List(1, 3)))
    val minSupp = 2
    val fiCollection = new FICollection(itemsets, minSupp)
    fiCollection.show()
  }
}
