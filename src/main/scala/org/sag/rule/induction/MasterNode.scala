package org.sag.rule.induction

import akka.actor.{Props, ActorSystem}
import akka.cluster.Cluster
import com.typesafe.config.ConfigFactory
import org.sag.rule.induction.actors.Master

/**
 * @author Cezary Pawlowski
 */
object MasterNode {
  def start(port: Int): ActorSystem = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [master]"))
      .withFallback(Context.getConfig)

    val system = ActorSystem(Context.getSettings.getActorSystemName, config)

    system.log.info("master node will start when at least one worker node will join the cluster")

    Cluster(system) registerOnMemberUp {
      system.actorOf(Props[Master], name = "master")
    }

    system
  }

  def main(args: Array[String]): Unit = {
    val port = if (args.isEmpty) 0 else args(0).toInt
    val system = start(port)
    system registerOnTermination {
      // ...
    }
  }
}
