package org.ulysses.santa

import se.scalablesolutions.akka.util.Logging
import org.ulysses.santa.SantaRunner._
import se.scalablesolutions.akka.stm.TransactionFactory

/**
 * Created by IntelliJ IDEA.
 * User: arjan
 * Date: Jan 17, 2010
 * Time: 8:34:23 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class Helper(g:Group, i:Int) extends Logging {
  
  def doTask(op: => Unit) = {
    log.debug("helper " + i + " is joining group")
    val (in, out) = g.joinGroup
    passGate(in)
    log.debug("helper " + i + " has passed in gate")
    op
    passGate(out)
    log.debug("helper " + i + " has passed out gate")
  }

  def passGate(g:Gate) = {
     log.debug("helper " + i + " is trying to pass gate " + g + " with remaining id " + System.identityHashCode(g.remaining))   
     g.passGate
  }
}

case class Elf1(g:Group, i:Int) extends Helper(g, i) with Logging {
  var metInStudy = 0
  def tryMeet = super.doTask(meetInStudy)
  def meetInStudy:Unit = {
    log.info("Elf " + i + " is meeting in study")
    metInStudy += 1
  }
}

case class Reindeer1(g:Group, i:Int) extends Helper(g, i) with Logging {
  def tryDeliver = super.doTask(deliverToys)
  
  def deliverToys = {
    log.info("Reindeer " + i + " is delivering toys")
  }
}

class Elf(g:Group, i:Int) extends Runnable {
  def run:Unit = {
    val elf = new Elf1(g, i)
    repeatDelayed(1000)(elf.tryMeet)
  }
}

class Reindeer(g:Group, i:Int) extends Runnable {
  def run:Unit = {
    val r = new Reindeer1(g, i)
    repeatDelayed(1000)(r.tryDeliver)
  }
}