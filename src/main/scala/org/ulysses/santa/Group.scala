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
    val g1 = Gate(capacity)(tfn)
    val g2 = Gate(capacity)(tfn)
    Group(new Ref(capacity, g1, g2))
  }
}

case class Group(ref: Ref[(Int, Gate, Gate)]) extends Logging {
  implicit val txFactory = TransactionFactory(blockingAllowed = true, trackReads = true, timeout = 600 seconds)
  def joinGroup(): (Gate, Gate) = {
    atomic {
      val n_left = ref.get._1
      log.debug("found n_left: " + n_left)
      if (n_left <= 0) retry
      else {
        val n_l_rem: Int = n_left - 1
        ref.swap(n_l_rem, ref.get._2, ref.get._3)
        log.deubg("finished swapping n_left with: " + n_l_rem)
      }
    }
    (ref.get._2, ref.get._3)
  }

  def awaitGroup(): (Gate, Gate) = {
    atomic {
      val n_left = ref.get._1
      if (n_left > 0) retry
      else {
        val g1 = ref.get._2
        val g2 = ref.get._2
        ref.swap(n_left - 1, ref.get._2, ref.get._3)
      }
    }
    (ref.get._2, ref.get._3)
  }
}