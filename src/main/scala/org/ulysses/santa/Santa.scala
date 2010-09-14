package org.ulysses.santa

import se.scalablesolutions.akka
import akka.stm.local._
import akka.util.Logging;
import akka.stm.TransactionFactory
import akka.util.duration._
import org.ulysses.santa.SantaRunner._


/**
 * Created by IntelliJ IDEA.
 * User: arjan
 * Date: Jan 3, 2010
 * Time: 11:53:08 AM
 * To change this template use File | Settings | File Templates.
 */

class Santa1(elves: Group, reindeers:Group) extends Logging {
  implicit val txFactory = TransactionFactory(blockingAllowed = true, trackReads = true, timeout = java.lang.Long.MAX_VALUE nanos)

  def choose:Unit = {
    atomic {
      var gates: (String, (Gate, Gate)) = null
      either {
        gates = chooseGroup("delivering toys", reindeers)
      } orElse {
        gates = chooseGroup("meeting in my study", elves)
      }
//      val gates: (String, (Gate, Gate)) = chooseGroup("meeting in my study", elves)
      log.info("Ho ho ho, " + gates._1)
      val in = gates._2._1
      log.debug("Start operating in gate " + in + " with remaining ref id: " + System.identityHashCode(in.remaining))
      in.operateGate
      log.debug("Finished operating in gate " + in)
      val out = gates._2._2
      log.debug("Start operating out gate " + out)
      out.operateGate
      log.info("Finished operating out gate ")
    }
  }

  def chooseGroup(task: String, g: Group) : (String, (Gate, Gate)) = {
    log.debug("Choosing group: " + task)
    val gates:(Gate, Gate) = g.awaitGroup
    (task, gates)
  }
}

class Santa(elves: Group, reindeers:Group) extends Runnable {
  def run:Unit = {
    val s = new Santa1(elves, reindeers)
    repeat(1000)(s.choose)
  }
}
