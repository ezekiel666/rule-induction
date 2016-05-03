package org.sag.rule.induction

import java.util.Date

import akka.event.slf4j.Logger
import org.sag.rule.induction.algorithm.{Itemset, FICollection}
import org.scalatest.{FlatSpec, Matchers}

/**
 * @author Cezary Pawlowski
 */
class FICollectionTest extends FlatSpec with Matchers {
  val log = Logger("FICollectionTest")

  "fi collection" should "work" in {
    val itemsets = List(Itemset(List(1, 2)), Itemset(List(1, 2, 3)), Itemset(List(1, 2, 3)), Itemset(List(1, 3)), Itemset(List(1, 3)))
    val fiCollection = new FICollection(itemsets, new Date(), new Date(), Set())
    fiCollection.show()
  }
}
