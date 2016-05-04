package org.sag.rule.induction.common

import org.sag.rule.induction.Context

/**
 * @author Cezary Pawlowski
 */
object Util {
  def getInterval(): Int = {
    val s = Context.getSettings
    (s.computationDelay * s.timeWindow).toInt
  }

  def calculateMinSupport(itemsetsCount: Int): Int = {
    val s = Context.getSettings

    s.minSupportMode match {
      case "absolute" =>
        s.absoluteMinSupport
      case "relative" =>
        (s.relativeMinSupport * itemsetsCount).toInt
    }
  }

  def getSupportPollLimit(minSupp: Int): Int = {
    val s = Context.getSettings
    ((1 - s.supportPollMargin) * minSupp).toInt
  }
}
