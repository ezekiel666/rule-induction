package org.sag.rule.induction

import com.typesafe.config.{ConfigFactory, Config}

/**
 * @author Cezary Pawlowski
 */
class Settings(config: Config) {
  config.checkValid(ConfigFactory.parseResources("application.conf"), "rule-induction")

  val inputFile = config.getString("rule-induction.input-file")
  val loop = config.getBoolean("rule-induction.loop")

  val streamingInterval = config.getInt("rule-induction.streaming.interval")
  val streamingRandomShift = config.getInt("rule-induction.streaming.random-shift")

  val timeWindow = config.getInt("rule-induction.time-window")
  val computationDelay = config.getDouble("rule-induction.computation-delay")

  val minSupportMode = config.getString("rule-induction.min-support.mode")
  val absoluteMinSupport = config.getInt("rule-induction.min-support.absolute")
  val relativeMinSupport = config.getDouble("rule-induction.min-support.relative")

  val minConfidence = config.getDouble("rule-induction.min-confidence")

  def print(): Unit = {
    println(s"rule-induction settings:")

    println(s"inputFile=$inputFile")
    println(s"loop=$loop")

    println(s"streaming.interval=$streamingInterval")
    println(s"streaming.random-shift=$streamingRandomShift")

    println(s"time-window=$timeWindow")
    println(s"computation-delay=$computationDelay")

    println(s"min-support.mode=$minSupportMode")
    println(s"min-support.absolute=$absoluteMinSupport")
    println(s"min-support.relative=$relativeMinSupport")

    println(s"min-confidence=$minConfidence")
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
