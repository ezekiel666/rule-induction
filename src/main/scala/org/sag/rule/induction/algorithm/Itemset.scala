package org.sag.rule.induction.algorithm

/**
 * @author Cezary Pawlowski
 */
case class Itemset(ids: List[Long]) {
  def contains(itemset: Itemset): Boolean = {
    itemset.ids.forall(id => ids.contains(id))
  }

  def size: Int = ids.size
}
