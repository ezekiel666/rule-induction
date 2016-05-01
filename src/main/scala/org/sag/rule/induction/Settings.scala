package org.sag.rule.induction

import com.typesafe.config.{ConfigFactory, Config}

/**
 * @author Cezary Pawlowski
 */
class Settings(config: Config) {
  config.checkValid(ConfigFactory.defaultReference(), "rule-induction")

  val minSupportMode = config.getString("rule-induction.min-support.mode")
  val absoluteMinSupport = config.getInt("rule-induction.min-support.absolute")
  val relativeMinSupport = config.getDouble("rule-induction.min-support.relative")

  def print(): Unit = {
    println(s"rule-induction settings:")

    println(s"min-support.mode=$minSupportMode")
    println(s"min-support.absolute=$absoluteMinSupport")
    println(s"min-support.relative=$relativeMinSupport")
  }

  def getActorSystemName: String = {
    val seed = config.getStringList("akka.cluster.seed-nodes").get(0)
    val regex = "(.*)://(.*)@(.*)".r
    seed match {
      case regex(_, name, _) =>
        name
    }
  }
}
