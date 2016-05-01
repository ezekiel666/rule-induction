package org.sag.rule.induction

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import org.sag.rule.induction.actors.Worker

import scala.reflect.ClassTag

/**
 * @author Cezary Pawlowski
 */
abstract class AbstractWorkerNode[T <: Worker : ClassTag] {
  def start(port: Int): ActorSystem = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [worker]"))
      .withFallback(Context.getConfig)

    val system = ActorSystem(Context.getSettings.getActorSystemName, config)

    system.actorOf(Props[T], name = "worker")

    system
  }
}
