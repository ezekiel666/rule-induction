package org.sag.rule.induction.algorithm

/**
 * @author Cezary Pawlowski, Maciej Korpalski
 */
case class Itemset(ids: List[Long]) {
  def contains(itemset: Itemset): Boolean = {
    itemset.ids.forall(id => ids.contains(id))
  }

  def size = ids.size
}
