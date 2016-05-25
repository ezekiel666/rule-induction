package org.sag.rule.induction

import org.sag.rule.induction.actors.Master

/**
 * @author Cezary Pawlowski, Maciej Korpalski
 */
object MasterNode extends AbstractMasterNode[Master] {
  def main(args: Array[String]): Unit = {
    val port = if (args.isEmpty) 0 else args(0).toInt
    val system = start(port)
    system registerOnTermination {
      // ...
    }
  }
}
