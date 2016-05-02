package org.sag.rule.induction.algorithm

/**
 * @author Cezary Pawlowski
 */
class Rule(base: IS, predecessor: IS, successor: IS, itemsetsCount: Int) {
  private def px(): Double = {
    predecessor.support / itemsetsCount.toDouble
  }

  private def py(): Double = {
    successor.support / itemsetsCount.toDouble
  }

  private def pxy(): Double = {
    base.support / itemsetsCount.toDouble
  }

  def conf(): Double = {
    pxy() / px()
  }

  def show(): Unit = {
    print(predecessor.itemset.ids.mkString(" "))
    print(" -> ")
    print(successor.itemset.ids.mkString(" "))
    println(s" (${base.support}, ${predecessor.support}, ${successor.support}, ${conf()})")
  }
}
