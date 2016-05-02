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
        case (itemset, support) => generate(IS(itemset, support))
      }
    }
  }

  private def generate(is: IS): Unit = {
    0 to is.itemset.size foreach { i =>
      getSubsets(is.itemset, i) foreach { successor =>
        val predecessor = substract(is.itemset, successor.itemset)
        val rule = new Rule(
          is,
          predecessor,
          successor,
          fiCollection.getItemsetsCount()
        )

        if(rule.conf() >= minConf) {
          rules += rule
        }
      }
    }
  }

  private def getSubsets(itemset: Itemset, n: Int): List[IS] = {
    val v = new mutable.ArrayBuffer[IS]

    if(n == 0) {
      val empty = fiCollection.fi(0).head
      empty match { case (itemset, support) =>
        v += IS(itemset, support)
      }
      return v.toList
    }

    getSubsetsImpl(v, itemset.ids, List(), n)
    v.toList
  }

  private def getSubsetsImpl(v: mutable.ArrayBuffer[IS], i: List[Long], t: List[Long], n: Int): Unit = {
    if(n == 0) {
      val itemset = Itemset(t)
      val support = fiCollection.getSupport(itemset)
      v += IS(itemset, support)
      return
    }

    0 until i.size foreach { k =>
      val tt = t ::: List(i(k))
      getSubsetsImpl(v, i.drop(k + 1), tt, n - 1)
    }
  }

  private def substract(lhs: Itemset, rhs: Itemset): IS = {
    val ids = lhs.ids filter {
      id => !rhs.ids.contains(id)
    }

    val it = Itemset(ids)
    val supp = fiCollection.getSupport(it)

    IS(it, supp)
  }

  def show(): Unit = {
    println(s"rules [minSupp=$minSupp, minConf=$minConf]:")
    println("X -> Y (sup_XY, sup_X, sup_Y, conf)")
    rules.foreach(_.show())
  }
}
