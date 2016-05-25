package org.sag.rule.induction.actors

import akka.actor.{Actor, ActorLogging}
import akka.routing.FromConfig
import org.sag.rule.induction.Context
import org.sag.rule.induction.common.CmdConsole
import CmdConsole.{InvalidCmd, ExitCmd, RunCmd}
import org.sag.rule.induction.common.Messages.Data
import scala.concurrent.ExecutionContext
import scala.io.Source
import scala.util.Random

/**
 * @author Cezary Pawlowski, Maciej Korpalski
 */
class Master extends Actor with ActorLogging {
  private val workerRouter = context.actorOf(FromConfig.props(), name = "workerRouter")

  private implicit val executor = ExecutionContext.global

  private val random = new Random()

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
    if(Context.getSettings.loop) {
      while(true) {
        streamData()
      }
    } else {
      streamData()
      processingFinished()
    }
  }

  def stop(): Unit = {
    context.stop(self)
    context.system.shutdown()
  }

  def processingFinished(): Unit = {
    println("processing finished")
    control()
  }

  def streamData(): Unit = {
    val s = Context.getSettings
    val shift = s.streamingRandomShift

    for(line <- Source.fromFile(s.inputFile).getLines()) {
      val interval = Context.getSettings.streamingInterval + random.nextInt(2 * shift) - shift
      Thread.sleep(interval)
      workerRouter ! Data(line.split("\\s+").map(_.toLong).toList)
    }
  }
}