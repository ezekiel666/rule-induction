package org.sag.rule.induction.actors

import akka.actor.{Actor, ActorLogging}
import org.sag.rule.induction.Context
import org.sag.rule.induction.common.Messages.{SetSupport, Data}

/**
 * @author Cezary Pawlowski
 */
class Worker extends Actor with ActorLogging {
  override def receive: Receive = {
    case Data(itemset) =>
      log.info(s"Data($itemset) from $sender")
      // TODO

    case SetSupport(item, support) =>
      log.info(s"SetSupport($item, $support) from $sender")
      // TODO

    case other =>
      log.warning(s"unknown message $other from $sender")
  }

  def calculateMinSupport(itemsetsCount: Long): Int = {
    val s = Context.getSettings

    s.minSupportMode match {
      case "absolute" =>
        s.absoluteMinSupport
      case "relative" =>
        (s.relativeMinSupport * itemsetsCount).toInt
    }
  }
}
