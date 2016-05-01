package org.sag.rule.induction

import org.sag.rule.induction.actors.Worker

/**
 * @author Cezary Pawlowski
 */
object WorkerNode extends AbstractWorkerNode[Worker] {
  def main(args: Array[String]): Unit = {
    val port = if (args.isEmpty) 0 else args(0).toInt
    val system = start(port)
    sys addShutdownHook {
      system.shutdown()
    }
  }
}
