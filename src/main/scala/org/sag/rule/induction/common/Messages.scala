package org.sag.rule.induction.common

/**
 * @author Cezary Pawlowski
 */
object Messages {
  trait Message {}

  case class Data(itemset: List[Long]) extends Message

  case class GetSupport(item: Long) extends Message
  case class SetSupport(item: Long, support: Long) extends Message
}
