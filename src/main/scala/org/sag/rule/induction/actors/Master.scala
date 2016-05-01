package org.sag.rule.induction.actors

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.routing.FromConfig
import org.sag.rule.induction.common.CmdConsole
import CmdConsole.{InvalidCmd, ExitCmd, RunCmd}
import scala.concurrent.ExecutionContext

/**
 * @author Cezary Pawlowski
 */
class Master extends Actor with ActorLogging {
  private val cluster = Cluster(context.system)
  private val workerRouter = context.actorOf(FromConfig.props(), name = "workerRouter")

  private implicit val executor = ExecutionContext.global

  override def preStart(): Unit = {
    control()
  }

  override def receive: Receive = {
    case other =>
      log.warning(s"unknown message $other from $sender")
  }

  def control(): Unit = {
    while(true) {
      val cmd = CmdConsole.getCmd()
      cmd match {
        case RunCmd() =>
          start()
          return
        case ExitCmd() =>
          stop()
          return
        case InvalidCmd() =>
          println("invalid command (only run/exit is allowed)")
      }
    }
  }

  def start(): Unit = {

  }

  def stop(): Unit = {
    context.stop(self)
    context.system.shutdown()
  }
}