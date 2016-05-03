package org.sag.rule.induction.actors

import java.util.Date

import akka.actor.{ExtendedActorSystem, Actor, ActorLogging}
import org.joda.time.DateTime
import org.sag.rule.induction.Context
import org.sag.rule.induction.algorithm.{RuleInduction, FICollection, Itemset}
import org.sag.rule.induction.common.Messages._
import org.sag.rule.induction.common.Util

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

import scala.collection.mutable

import akka.cluster.Cluster

/**
 * @author Cezary Pawlowski
 */
class Worker extends Actor with ActorLogging {
  val sequence = mutable.Map[Date, Itemset]()

  override def receive: Receive = {
    case Data(ids) =>
      log.info(s"Data($ids) from $sender")
      if(sequence.isEmpty) { schedule() }
      sequence.put(new Date(), Itemset(ids))

    case GetSupport(itemset, start, stop) =>
      log.info(s"GetSupport($itemset, $start, $stop) from $sender")
      val itemsets = sequence.filterKeys(d => d.after(start) && d.before(stop)).map(_._2).toList
      val support = itemsets.filter(_.contains(itemset)).size
      sender ! SetSupport(itemset, start, stop, support, itemsets.size)

    case SetSupport(itemset, start, stop, support, itemsetsCount) =>
      log.info(s"SetSupport($itemset, $start, $stop, $support, $itemsetsCount) from $sender")
      // TODO

    case RulesGenerationTrigger() =>
      log.info(s"RulesGenerationTrigger() from $sender")
      val s = Context.getSettings
      val stop = new Date()
      val start = new DateTime(stop).minusMillis(s.timeWindow).toDate
      val itemsets = sequence.filterKeys(d => d.after(start) && d.before(stop)).map(_._2).toList
      Future {
        generateRules(itemsets, start, stop)
      }

    case other =>
      log.warning(s"unknown message $other from $sender")
  }

  def schedule(): Unit = {
    val dt = Util.getInterval()
    context.system.scheduler.schedule(dt millis, dt millis) {
      self ! RulesGenerationTrigger()
    }
  }

  def generateRules(itemsets: List[Itemset], start: Date, stop: Date): Unit = {
    log.info(s"apriori [itemsets=$itemsets, start=$start, stop=$stop]")

    val selfAddress = context.system.asInstanceOf[ExtendedActorSystem].provider.getDefaultAddress
    val workers = Cluster(context.system).state.members.filter(m => m.hasRole("worker") && m.address != selfAddress) map { w =>
      context.actorSelection(w.address + "/user/worker")
    }

    //log.info(s"selfAddress=${selfAddress}")
    //log.info(s"workers=${workers}")

    val fiCollection = new FICollection(itemsets, start, stop, workers)
    val rules = new RuleInduction(fiCollection)

    println(s"[itemsets=$itemsets]")
    println(s"[start=$start, stop=$stop]")
    fiCollection.show()
    rules.show()
  }
}
