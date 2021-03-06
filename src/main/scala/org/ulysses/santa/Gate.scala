package org.ulysses.santa

import se.scalablesolutions.akka
import akka.util.Logging
import akka.stm.Ref;
import akka.stm.local._
import akka.stm.Transaction.Propagation
import akka.util.duration._

/**
 * Created by IntelliJ IDEA.
 * User: arjan
 * Date: Jan 16, 2010
 * Time: 10:43:13 AM
 * To change this template use File | Settings | File Templates.
 */

case class Gate(capacity: Int, remaining: Ref[Int]) extends Logging {
  implicit val txFactory = TransactionFactory(blockingAllowed = true, trackReads = true, timeout = java.lang.Long.MAX_VALUE nanos, propagation=Propagation.RequiresNew)
  
  def passGate: Unit = {
    atomic {
      val rem = remaining.get
      val refHash = System.identityHashCode(remaining)
      log.debug("passGate found n_left " + rem + " for remaining ref hash " + refHash)
      if (rem <= 0) {
        log.debug("Gate has no capacity left, retrying for ref id "  + refHash)
        retry
      }
      log.debug("decreasing remaining capacity to " + (rem - 1) + " for remaining ref id " + refHash)
      remaining alter(_ - 1)
      log.debug("finished passing gate " + refHash)
    }
  }

  def getRemaining: Int = {
    atomic {
      remaining.get
    }
  }

  def operateGate: Unit = {
    atomic {
      remaining.set(capacity)
      log.debug("Finished setting gate capacity to value " + capacity + " for ref id " + System.identityHashCode(remaining))
    }
    //this check seems to cause some kind of a deadlock on the gate... still need to find out why
    checkFull
  }

  private def checkFull: Unit = {
    atomic {
      log.debug("Checking whether gate is full, found remaining: " + remaining + " for ref id " + System.identityHashCode(remaining))
      if (remaining.get > 0) {
        log.debug("Gate has remaining capacity left, retrying...")
        retry
      }
      log.debug("Gate is full, exiting wait ")
    }
  }

  private def resetGate: Unit = {

  }
}

object Gate {
  def apply(capacity: Int): Gate = {
    val ref = new Ref[Int](0)
    Gate(capacity, ref)
  }
}
