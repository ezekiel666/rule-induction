package org.sag.rule.induction.algorithm

import java.util.Date

import akka.actor.ActorSelection
import org.sag.rule.induction.common.Util

import scala.collection.immutable
import scala.collection.mutable

/**
 * @author Cezary Pawlowski
 */
class FICollection(itemsets: List[Itemset], start: Date, stop: Date, workers: Set[ActorSelection]) {
  private val minSupp = Util.calculateMinSupport(itemsets.size)
  private val fi = new mutable.ArrayBuffer[immutable.Map[Itemset, Int]]
  private val ic = mutable.Map[Itemset, Int]() // contains itemsets count, when different than default

  generate()

  private def generate(): Unit = {
    generate0()
    generate1()
    while(generateNext()) {}
  }

  private def generate0(): Unit = {
    val itemset = Itemset(List.empty[Long])
    val support = itemsets.size
    val f = immutable.Map[Itemset, Int]((itemset, support))
    fi += f
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
    val n = it1.size - 1
    0 until n forall(i => it1.ids(i) == it2.ids(i))
  }

  private def join(it1: Itemset, it2: Itemset): Itemset = {
    val n = it1.size - 1
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
    println(s"frequent itemsets:")
    println(s"X (sup, ic)")
    fi foreach { f =>
      f foreach { case (itemset, support) =>
        print(itemset.ids.mkString(" "))
        println(s" ($support, ${getItemsetsCount(itemset)})")
      }
    }
  }

  def getFrequentItemsets = fi

  def empty = fi(0).head

  def getItemsetsCount(itemset: Itemset): Int = {
    ic.get(itemset) match {
      case Some(ic) => ic
      case None => itemsets.size
    }
  }

  def getSupport(itemset: Itemset): Int = {
    fi(itemset.size).get(itemset).get
  }
}
