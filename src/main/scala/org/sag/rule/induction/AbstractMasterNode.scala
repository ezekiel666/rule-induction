package org.sag.rule.induction

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import com.typesafe.config.ConfigFactory
import org.sag.rule.induction.actors.Master

import scala.reflect.ClassTag

/**
 * @author Cezary Pawlowski
 */
abstract class AbstractMasterNode[T <: Master : ClassTag] {
  def start(port: Int): ActorSystem = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [master]"))
      .withFallback(Context.getConfig)

    val system = ActorSystem(Context.getSettings.getActorSystemName, config)

    system.log.info("master node will start when at least one worker node will join the cluster")

    Cluster(system) registerOnMemberUp {
      system.actorOf(Props[T], name = "master")
    }

    system
  }
}
