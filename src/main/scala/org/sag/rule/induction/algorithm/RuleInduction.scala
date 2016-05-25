package org.sag.rule.induction.algorithm

import org.sag.rule.induction.Context

import scala.collection.{mutable}

/**
 * @author Cezary Pawlowski, Maciej Korpalski
 */
class RuleInduction(fiCollection: FICollection) {
  private val minConf = Context.getSettings.minConfidence
  private val rules = new mutable.ArrayBuffer[Rule]

  generate()

  private def generate(): Unit = {
    fiCollection.getFrequentItemsets.reverse foreach { f =>
      f foreach {
        case (itemset, stats) =>
          val tuple = ItemsetTuple(itemset, stats.support, stats.itemsetsCount)
          generate(tuple)
      }
    }
  }

  private def generate(itemsetTuple: ItemsetTuple): Unit = {
    0 to itemsetTuple.itemset.size foreach { i =>
      getSubsets(itemsetTuple.itemset, i) foreach { successor =>
        val predecessor = substract(itemsetTuple.itemset, successor.itemset)
        val rule = new Rule(
          itemsetTuple,
          predecessor,
          successor
        )

        if(rule.conf() >= minConf) {
          rules += rule
        }
      }
    }
  }

  private def getSubsets(itemset: Itemset, n: Int): List[ItemsetTuple] = {
    val v = new mutable.ArrayBuffer[ItemsetTuple]

    if(n == 0) {
      fiCollection.empty match { case (itemset, stats) =>
        v += ItemsetTuple(itemset, stats.support, stats.itemsetsCount)
      }
      return v.toList
    }

    getSubsetsImpl(v, itemset.ids, List(), n)
    v.toList
  }

  private def getSubsetsImpl(v: mutable.ArrayBuffer[ItemsetTuple], i: List[Long], t: List[Long], n: Int): Unit = {
    if(n == 0) {
      val itemset = Itemset(t)
      val stats = fiCollection.getStats(itemset)
      v += ItemsetTuple(itemset, stats.support, stats.itemsetsCount)
      return
    }

    0 until i.size foreach { k =>
      val tt = t ::: List(i(k))
      getSubsetsImpl(v, i.drop(k + 1), tt, n - 1)
    }
  }

  private def substract(lhs: Itemset, rhs: Itemset): ItemsetTuple = {
    val ids = lhs.ids filter {
      id => !rhs.ids.contains(id)
    }

    val itemset = Itemset(ids)
    val stats = fiCollection.getStats(itemset)
    ItemsetTuple(itemset, stats.support, stats.itemsetsCount)
  }

  def show(): Unit = {
    println(s"rules [minConf=$minConf]:")
    println("X -> Y (sup_XY, sup_X, sup_Y, ic_XY, ic_X, ic_Y, conf)")
    rules.foreach(_.show())
  }
}
