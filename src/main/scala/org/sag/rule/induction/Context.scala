package org.sag.rule.induction

import com.typesafe.config.{ConfigFactory, Config}

/**
 * @author Cezary Pawlowski, Maciej Korpalski
 */
class Context(val config: Config) {
  val settings = new Settings(config)

  def this() {
    this(ConfigFactory.load())
  }
}

/**
 * @author Cezary Pawlowski, Maciej Korpalski
 */
object Context {
  private var context = new Context()
  def set(context: Context): Unit = synchronized {
    this.context = context
  }
  def get: Context = synchronized { context }
  def getConfig: Config = synchronized { context.config }
  def getSettings: Settings = synchronized { context.settings }
}
