package org.ulysses.santa

import se.scalablesolutions.akka
import akka.stm.Ref
import akka.stm.local._
import akka.stm.TransactionFactory
import akka.util.Logging;
import akka.util.duration._

/**
 * Created by IntelliJ IDEA.
 * User: arjan
 * Date: Jan 17, 2010
 * Time: 8:34:36 AM
 * To change this template use File | Settings | File Templates.
 */
object Group {
  def apply(capacity: Int): Group = {
    log.debug("Creating gates with capacity: " + capacity)
    val g1 = Gate(capacity)
    val g2 = Gate(capacity)
    Group(capacity:Int, new Ref(capacity, g1, g2))
  }
}

case class Group(capacity:Int, ref: Ref[(Int, Gate, Gate)]) extends Logging {
  implicit val txFactory = TransactionFactory(blockingAllowed = true, trackReads = true, timeout = java.lang.Long.MAX_VALUE nanos)

  def joinGroup: (Gate, Gate) = {
    atomic {
      val n_left = ref.get._1
      if (n_left <= 0) {
        log.debug("Group's capacity is zero retrying transaction...")
        //Thread.sleep(100)
        retry
      }
      else {
        val rem: Int = n_left - 1
        ref.set(rem, ref.get._2, ref.get._3)
        log.debug("Set remaining capacity of group to " + rem)
      }
      (ref.get._2, ref.get._3)
    }
  }

  def awaitGroup: (Gate, Gate) = {
    atomic {
      val (n_left: Int, g1: Gate, g2: Gate) = (ref.get._1, ref.get._2, ref.get._3)
      if (n_left > 0) retry
      else {
        ref.set(capacity, Gate(capacity), Gate(capacity))
      }
      (g1, g2)
    }
  }
}