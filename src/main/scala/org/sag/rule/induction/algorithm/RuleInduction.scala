package org.sag.rule.induction.algorithm

import scala.collection.{mutable}

/**
 * @author Cezary Pawlowski
 */
class RuleInduction(fiCollection: FICollection, minSupp: Int, minConf: Double) {
  private val rules = new mutable.ArrayBuffer[Rule]
  generate()

  private def generate(): Unit = {
    fiCollection.fi.reverse foreach { f =>
      f foreach {
        case tuple => generate(tuple)
      }
    }
  }

  private def generate(tuple: (Itemset, Int)): Unit = {
    tuple match {
      case (itemset, supp) =>
        0 to itemset.ids.size foreach { i =>
          getSubsets(itemset, i) foreach { case (successor, sSupp) =>
            val (predecessor, pSupp) = substract(itemset, successor)
            val rule = new Rule(
              (itemset, supp),
              (predecessor, pSupp),
              (successor, sSupp),
              fiCollection.getItemsetsCount()
            )

            if(rule.conf() >= minConf) {
              rules += rule
            }
          }
        }
    }
  }

  private def getSubsets(itemset: Itemset, n : Int): List[(Itemset, Int)] = {
    val v = new mutable.ArrayBuffer[(Itemset, Int)]

    if(n == 0) {

    }
    // TODO

    getSubsetsImpl(v, itemset, Itemset(List()), n)
    v.toList
  }

  private def getSubsetsImpl(v: mutable.ArrayBuffer[(Itemset, Int)], i: Itemset, t: Itemset, n : Int): Unit = {
    if(n == 0) {

    }

    // TODO
  }

  private def substract(lhs: Itemset, rhs: Itemset): (Itemset, Int) = {
    val ids = lhs.ids filter {
      id => !rhs.ids.contains(id)
    }

    val it = Itemset(ids)
    val supp = fiCollection.getSupport(it)

    (it, supp)
  }

  def show(): Unit = {
    println(s"rules [minSupp=$minSupp, minConf=$minConf]:")
    // TODO
  }
}
