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
  implicit val txFactory = TransactionFactory(blockingAllowed = true, trackReads = true, traceLevel=TraceLevel.Fine, timeout = java.lang.Long.MAX_VALUE nanos)
  
  def passGate: Unit = {
    log.debug("passing gate with ref id: " + System.identityHashCode(remaining))
    atomic {
//      log.debug("passGate found n_left " + n_left)
      if (remaining.get <= 0) {
        log.debug("Gate has no capacity left, retrying")
        retry
      }
      log.debug("decreasing remaining capacity")
      remaining alter(_ - 1)
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
    checkFull
    log.info("Finished operating gate")
  }

  private def checkFull: Unit = {
    atomic {
      if (remaining.get > 0) {
        log.info("Gate has remaining " + remaining + " on ref id " + System.identityHashCode(remaining))
        retry
      }
      log.debug("Gate is full, exiting wait ")
    }
  }

  private def resetGate: Unit = {
    atomic {
      remaining.set(capacity)
    }
    log.debug("Finished setting gate capacity to value " + capacity + " for ref id " + System.identityHashCode(remaining))
  }
}

object Gate {
  def apply(capacity: Int): Gate = {
    val ref = new Ref[Int](0)
    Gate(capacity, ref)
  }
}
