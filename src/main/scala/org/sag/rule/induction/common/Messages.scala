package org.sag.rule.induction.common

import java.util.Date

import org.sag.rule.induction.algorithm.Itemset

/**
 * @author Cezary Pawlowski, Maciej Korpalski
 */
object Messages {
  trait Message {}

  case class Data(ids: List[Long]) extends Message

  case class GetSupport(itemset: Itemset, start: Date, stop: Date) extends Message
  case class SetSupport(itemset: Itemset, start: Date, stop: Date, support: Int, itemsetsCount: Int) extends Message

  case class RulesGenerationTrigger()
}
