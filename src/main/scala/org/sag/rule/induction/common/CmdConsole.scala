package org.sag.rule.induction.common

import scala.io.StdIn

/**
 * @author Cezary Pawlowski
 */
object CmdConsole {
  def getCmd(): Cmd = {
    print("> ")
    val cmd = StdIn.readLine()
    if(cmd == null) { InvalidCmd() }

    cmd match {
      case "run" =>
        RunCmd()
      case "exit" =>
        ExitCmd()
      case _ =>
        InvalidCmd()
    }
  }

  abstract class Cmd
  case class RunCmd() extends Cmd
  case class ExitCmd() extends Cmd
  case class InvalidCmd() extends Cmd
}
