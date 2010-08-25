package org.ulysses.santa

import se.scalablesolutions.akka
import akka.util.Logging
import akka.stm.Ref;
import akka.stm.local._
import akka.util.duration._

/**
 * Created by IntelliJ IDEA.
 * User: arjan
 * Date: Jan 16, 2010
 * Time: 10:43:13 AM
 * To change this template use File | Settings | File Templates.
 */

case class Gate(capacity: Int, remaining: Ref[Int]) extends Logging {
  implicit val txFactory = TransactionFactory(blockingAllowed = true, timeout = 10 seconds)
  
  def passGate: Unit = {
    log.debug("passing gate")
    atomic {
      val n_left = remaining.get
      log.debug("passGate found n_left " + n_left)
      if (n_left <= 0) retry 
      else remaining.set(n_left - 1)
    }
    log.debug("finished passing gate")
  }

  def getRemaining: Int = {
    var res = 0
    atomic {
      res = remaining.get
    }
    res
  }

  def operateGate: Unit = {
    resetGate
    waitForFull
    log.debug("Finished operating gate")
  }

  private def waitForFull: Unit = {
    atomic {
      val n_left: Int = remaining.get
      log.debug("waitForFull found n_left: " + n_left)
      if (n_left > 0) {
        Thread.sleep(10)
        retry
      } else {
        log.debug("Gate is full, exiting wait ")
      }
    }
  }

  private def resetGate: Unit = {
    log.debug("Set capacity to value " + capacity)
    atomic {  
      remaining.set(capacity)
    }
  }

  private def isFull(implicit tfn: String): Boolean = {
    atomic {
      val n_left: Int = remaining.get
      log.debug("Found n_left: " + n_left)
      if (n_left > 0) {
        log.debug("Re-trying transaction")
        false
      } else {
        true
      }
    }
  }
}

object Gate {
  def apply(capacity: Int)(implicit tfn: String): Gate = {
    val ref = new Ref[Int](capacity)
    Gate(capacity, ref)
  }
}
