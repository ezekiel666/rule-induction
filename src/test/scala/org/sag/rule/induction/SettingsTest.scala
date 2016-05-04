package org.sag.rule.induction

import org.scalatest.{FlatSpec, Matchers}
import org.slf4j.LoggerFactory

/**
 * @author Cezary Pawlowski
 */
class SettingsTest extends FlatSpec with Matchers {
  val log = LoggerFactory.getLogger(classOf[SettingsTest])

  "settings" should "extract actor system name" in {
    Context.getSettings.getActorSystemName shouldBe "ClusterSystem"
  }
}
