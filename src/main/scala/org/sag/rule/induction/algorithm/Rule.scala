package org.sag.rule.induction.algorithm

/**
 * @author Cezary Pawlowski, Maciej Korpalski
 */
class Rule(base: ItemsetTuple, predecessor: ItemsetTuple, successor: ItemsetTuple) {
  private def px(): Double = {
    predecessor.support / predecessor.itemsetsCount.toDouble
  }

  private def py(): Double = {
    successor.support / successor.itemsetsCount.toDouble
  }

  private def pxy(): Double = {
    base.support / base.itemsetsCount.toDouble
  }

  def conf(): Double = {
    pxy() / px()
  }

  def show(): Unit = {
    print(predecessor.itemset.ids.mkString(" "))
    print(" -> ")
    print(successor.itemset.ids.mkString(" "))
    println(s" (${base.support}, ${predecessor.support}, ${successor.support}, ${base.itemsetsCount}, ${predecessor.itemsetsCount}, ${successor.itemsetsCount}, ${conf()})")
  }
}
