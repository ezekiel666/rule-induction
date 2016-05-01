package org.sag.rule.induction

import org.sag.rule.induction.actors.Master

/**
 * @author Cezary Pawlowski
 */
class TestMaster extends Master {
  override def preStart(): Unit = ()

  override def receive: Receive = {
    case StartProcessing() =>
      log.info(s"StartProcessing() from $sender")
      start()
    case other =>
      super.receive(other)
  }

  override def processingFinished(): Unit = {
    context.system.actorSelection("/system/testActor*") ! ProcessingFinished()
  }
}