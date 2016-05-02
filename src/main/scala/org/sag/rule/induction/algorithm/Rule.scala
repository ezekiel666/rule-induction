package org.sag.rule.induction.algorithm

/**
 * @author Cezary Pawlowski
 */
class Rule(base: (Itemset, Int), predecessor: (Itemset, Int), successor: (Itemset, Int), itemsetsCount: Int) {
  private def px(): Double = {
    predecessor._2 / itemsetsCount.toDouble
  }

  private def py(): Double = {
    successor._2 / itemsetsCount.toDouble
  }

  private def pxy(): Double = {
    base._2 / itemsetsCount.toDouble
  }

  def conf(): Double = {
    pxy() / px()
  }
}
