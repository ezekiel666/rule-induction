package org.sag.rule.induction.actors

import java.util.Date

import akka.actor.{Actor, ActorLogging}
import org.joda.time.DateTime
import org.sag.rule.induction.Context
import org.sag.rule.induction.algorithm.Itemset
import org.sag.rule.induction.common.Messages._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

import scala.collection.mutable


/**
 * @author Cezary Pawlowski
 */
class Worker extends Actor with ActorLogging {
  val sequence = mutable.Map[Date, Itemset]()

  val dt = getInterval()
  context.system.scheduler.schedule(dt millis, dt millis) {
    self ! RulesGenerationTrigger()
  }

  override def receive: Receive = {
    case Data(ids) =>
      log.info(s"Data($ids) from $sender")
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
        apriori(itemsets, start, stop)
      }

    case other =>
      log.warning(s"unknown message $other from $sender")
  }

  def getInterval(): Int = {
    val s = Context.getSettings
    (s.computationDelay * s.timeWindow).toInt
  }

  def calculateMinSupport(itemsetsCount: Long): Int = {
    val s = Context.getSettings

    s.minSupportMode match {
      case "absolute" =>
        s.absoluteMinSupport
      case "relative" =>
        (s.relativeMinSupport * itemsetsCount).toInt
    }
  }

  def apriori(itemsets: List[Itemset], start: Date, stop: Date): Unit = {
    //import akka.cluster.Cluster
    //val cluster = Cluster(context.system)
    //val workers = cluster.state.members.filter(m => m.hasRole("worker"))
    //context.actorSelection("akka.tcp://ClusterSystem@" + n + "/user/acceptor")
    // TODO
    log.info("apriori")
  }
}
