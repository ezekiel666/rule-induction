package org.sag.rule.induction

import java.util.Date

import akka.event.slf4j.Logger
import org.sag.rule.induction.algorithm.{RuleInduction, FICollection, Itemset}
import org.scalatest.{FlatSpec, Matchers}

/**
 * @author Cezary Pawlowski
 */
class RuleInductionTest extends FlatSpec with Matchers {
  val log = Logger("RuleInductionTest")

  "rule induction" should "work" in {
    val itemsets = List(Itemset(List(1, 2)), Itemset(List(1, 2, 3)), Itemset(List(1, 2, 3)), Itemset(List(1, 3)), Itemset(List(1, 3)))
    val minSupp = 2
    val fiCollection = new FICollection(itemsets, new Date(), new Date(), Set())
    fiCollection.show()

    val minConf = 0.5
    val rules = new RuleInduction(fiCollection)
    rules.show()
  }
}
