package org.sag.rule.induction.algorithm

import scala.collection.immutable
import scala.collection.mutable

/**
 * @author Cezary Pawlowski
 */
class FICollection(itemsets: List[Itemset], minSupp: Int) {
  val fi = new mutable.ArrayBuffer[immutable.Map[Itemset, Int]]
  generate()

  private def generate(): Unit = {
    generate1()
    while(generateNext()) {}
  }

  private def generate1(): Unit = {
    val f = itemsets.flatMap(_.ids).groupBy(identity) map {
      case (k, v) => (Itemset(List(k)), v.size)
    } filter {
      case (k, v) => v >= minSupp
    }

    fi += f
  }

  private def generateNext(): Boolean = {
    val c = join()
    val f = prune(c)

    if(f.isEmpty) {
      false
    } else {
      fi += f
      true
    }
  }

  private def join(): mutable.Map[Itemset, Int] = {
    val c = mutable.Map[Itemset, Int]()
    val keys = fi.last.keys.toArray

    0 until keys.size foreach { i =>
      (i + 1) until keys.size foreach { k =>
        val it1 = keys(i)
        val it2 = keys(k)

        if(check(it1, it2)) {
          val it = join(it1, it2)
          c.put(it, 0)
        }
      }
    }

    c
  }

  private def check(it1: Itemset, it2: Itemset): Boolean = {
    val n = it1.ids.size - 1
    0 until n forall(i => it1.ids(i) == it2.ids(i))
  }

  private def join(it1: Itemset, it2: Itemset): Itemset = {
    val n = it1.ids.size - 1
    val ids = it1.ids.take(n) ::: List(it1.ids.last, it2.ids.last).sorted
    Itemset(ids)
  }

  private def prune(c: mutable.Map[Itemset, Int]): immutable.Map[Itemset, Int] = {
    for(itemset <- itemsets) {
      for((k, v) <- c) {
        if(itemset.contains(k)) {
          c(k) = v + 1
        }
      }
    }

    c filter {
      case (k, v) => v >= minSupp
    } toMap
  }

  def show(): Unit = {
    println(s"frequent itemsets [minSupp=$minSupp]:")
    for(f <- fi) {
      for((k, v) <- f) {
        print(k.ids.mkString(" "))
        println(s" ($v)")
      }
    }
  }

  def getItemsetsCount(): Int = {
    itemsets.size
  }

  def getSupport(itemset: Itemset): Int = {
    fi(itemset.ids.size).get(itemset).get
  }
}
