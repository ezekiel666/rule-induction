package org.sag.rule.induction

import akka.event.slf4j.Logger
import org.scalatest.{FlatSpec, Matchers}

/**
 * @author Cezary Pawlowski
 */
class SettingsTest extends FlatSpec with Matchers {
  val log = Logger("SettingsTest")

  "settings" should "extract actor system name" in {
    Context.getSettings.getActorSystemName shouldBe "ClusterSystem"
  }
}
