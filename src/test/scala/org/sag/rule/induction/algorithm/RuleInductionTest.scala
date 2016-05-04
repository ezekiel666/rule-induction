package org.sag.rule.induction.algorithm

import java.util.Date

import org.scalatest.{FlatSpec, Matchers}
import org.slf4j.LoggerFactory

/**
 * @author Cezary Pawlowski
 */
class RuleInductionTest extends FlatSpec with Matchers {
  val log = LoggerFactory.getLogger(classOf[RuleInductionTest])

  "rule induction" should "work" in {
    val itemsets = List(Itemset(List(1, 2)), Itemset(List(1, 2, 3)), Itemset(List(1, 2, 3)), Itemset(List(1, 3)), Itemset(List(1, 3)))
    val fiCollection = new FICollection(itemsets, new Date(), new Date(), Set())
    val rules = new RuleInduction(fiCollection)
    rules.show()
  }
}
