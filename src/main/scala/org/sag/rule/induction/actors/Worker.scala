package org.sag.rule.induction.actors

import akka.actor.{Actor, ActorLogging}
import org.sag.rule.induction.Context

/**
 * @author Cezary Pawlowski
 */
class Worker extends Actor with ActorLogging {
  override def receive: Receive = {
    case other =>
      log.warning(s"unknown message $other from $sender")
  }

  def calculateMinSupport(rowsCount: Long): Int = {
    val s = Context.getSettings

    s.minSupportMode match {
      case "absolute" =>
        s.absoluteMinSupport
      case "relative" =>
        (s.relativeMinSupport * rowsCount).toInt
    }
  }
}
