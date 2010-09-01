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
  def apply(capacity: Int)(implicit tfn: String): Group = {
    log.debug("Creating gates with capacity: " + capacity)
    val g1 = Gate(capacity)(tfn)
    val g2 = Gate(capacity)(tfn)
    Group(new Ref(capacity, g1, g2))
  }
}

case class Group(ref: Ref[(Int, Gate, Gate)]) extends Logging {
  implicit val txFactory = TransactionFactory(blockingAllowed = true, timeout = 10 seconds)
  def joinGroup: (Gate, Gate) = {
    atomic {
      val n_left = ref.get._1
      if (n_left <= 0) {
        log.debug("Group's capacity is zero retrying transaction...")
        //Thread.sleep(100)
        retry
      }
      else {
        val n_l_rem: Int = n_left - 1
        ref.set(n_l_rem, ref.get._2, ref.get._3)
        log.debug("Set remaining capacity of group to " + n_l_rem)
      }
    }
    (ref.get._2, ref.get._3)
  }

  def awaitGroup()(implicit tfn: String): (Gate, Gate) = {
    atomic {
      val n_left = ref.get._1
      if (n_left > 0) retry
      else {
        val g1 = ref.get._2
        val g2 = ref.get._2
        ref.set(n_left - 1, ref.get._2, ref.get._3)
      }
    }
    (ref.get._2, ref.get._3)
  }
}