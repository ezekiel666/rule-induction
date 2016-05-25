package org.sag.rule.induction.algorithm

import java.util.Date

import akka.actor.ActorSelection
import akka.util.Timeout
import org.sag.rule.induction.common.Messages.{SetSupport, GetSupport}
import org.sag.rule.induction.common.Util
import org.slf4j.LoggerFactory

import scala.collection.immutable
import scala.collection.mutable
import scala.concurrent.{Await, Future}

import scala.concurrent.duration._
import akka.pattern.{AskTimeoutException, ask}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author Cezary Pawlowski, Maciej Korpalski
 */
class FICollection(itemsets: List[Itemset], start: Date, stop: Date, workers: Set[ActorSelection]) {
  private val log = LoggerFactory.getLogger(classOf[FICollection])
  private val minSupp = Util.calculateMinSupport(itemsets.size)
  private val fi = new mutable.ArrayBuffer[immutable.Map[Itemset, ItemsetStats]]

  generate()

  private def generate(): Unit = {
    generate0()
    generate1()
    while(generateNext()) {}
  }

  private def generate0(): Unit = {
    val itemset = Itemset(List.empty[Long])
    val s = itemsets.size
    val f = immutable.Map[Itemset, ItemsetStats]((itemset, ItemsetStats(s, s)))
    fi += f
  }

  private def poll(p: mutable.Map[Itemset, ItemsetStats]): Unit = {
    implicit val timeout = Timeout(5 seconds)

    p.par foreach { case (itemset, stats) =>
      val futures = workers map { actor =>
        try {
          (actor ? GetSupport(itemset, start, stop)) map {
            case SetSupport(itemset, start, stop, support, itemsetsCount) =>
              log.info(s"SetSupport($itemset, $start, $stop, $support, $itemsetsCount) from $actor")
              (support, itemsetsCount)
          }
        } catch {
          case _: AskTimeoutException =>
            log.info(s"AskTimeoutException($itemset, $start, $stop) from $actor")
            Future.successful((0, 0))
        }
      }

      val future = Future.reduce(futures) {
        case ((s1,i1), (s2,i2)) =>
          (s1 + s2, i1 + i2)
      }

      val result = Await.result(future, Duration.Inf)

      result match {
        case (support, itemsetsCount) =>
          stats.support += support
          stats.itemsetsCount += itemsetsCount
      }
    }
  }

  private def filter(c: mutable.Map[Itemset, ItemsetStats]): immutable.Map[Itemset, ItemsetStats] = {
    val f = c filter {
      case (itemset, stats) => stats.support >= minSupp
    }

    val p = c filter {
      case (itemset, stats) => stats.support >= Util.getSupportPollLimit(minSupp) && stats.support < minSupp
    }

    poll(p)

    val fp = p filter {
      case (itemset, stats) => stats.support >= Util.calculateMinSupport(stats.itemsetsCount)
    }

    (f ++ fp).toMap
  }

  private def generate1(): Unit = {
    val c = mutable.Map[Itemset, ItemsetStats]()

    c ++= itemsets.flatMap(_.ids).groupBy(identity) map {
      case (k, v) => (Itemset(List(k)), ItemsetStats(v.size, itemsets.size))
    }

    fi += filter(c)
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

  private def join(): mutable.Map[Itemset, ItemsetStats] = {
    val c = mutable.Map[Itemset, ItemsetStats]()
    val prev = fi.last.keys.toArray

    0 until prev.size foreach { i =>
      (i + 1) until prev.size foreach { k =>
        val it1 = prev(i)
        val it2 = prev(k)

        if(check(it1, it2)) {
          val it = join(it1, it2)
          c.put(it, ItemsetStats(0, itemsets.size))
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

  private def prune(c: mutable.Map[Itemset, ItemsetStats]): immutable.Map[Itemset, ItemsetStats] = {
    for(i <- itemsets) {
      for((itemset, stats) <- c) {
        if(i.contains(itemset)) {
          stats.support += 1
        }
      }
    }

    filter(c)
  }

  def show(): Unit = {
    println(s"frequent itemsets [start=$start, stop=$stop]:")
    println(s"X (sup, ic) [minSupp]")
    fi foreach { f =>
      f foreach { case (itemset, stats) =>
        print(itemset.ids.mkString(" "))
        println(s" (${stats.support}, ${stats.itemsetsCount}) [${Util.calculateMinSupport(stats.itemsetsCount)}]")
      }
    }
  }

  def getFrequentItemsets = fi

  def empty = fi(0).head

  def getStats(itemset: Itemset): ItemsetStats = {
    fi(itemset.size).get(itemset).get
  }
}
