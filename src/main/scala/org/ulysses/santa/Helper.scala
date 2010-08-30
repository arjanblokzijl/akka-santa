package org.ulysses.santa

import se.scalablesolutions.akka.util.Logging

/**
 * Created by IntelliJ IDEA.
 * User: arjan
 * Date: Jan 17, 2010
 * Time: 8:34:23 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class Helper(g:Group, i:Int) extends Logging with Runnable {

  def doTask(op: => Unit) = {
    log.debug("elf " + i + " is joining group")
    val io = g.joinGroup
    passGate(io._1)
    op
    passGate(io._2)
  }

  def passGate(g:Gate) = {
     log.debug("helper " + i + " is passing gate " + g)
  }
}

case class Elf1(g:Group, i:Int) extends Helper(g, i) with Logging {
  def run = doTask({meetInStudy})
  def meetInStudy = {
    log.info("Elf " + i + " is meeting in study")
  }
}

case class Reindeer1(g:Group, i:Int) extends Helper(g, i) with Logging {
  def run = doTask({deliverToys})
  
  def deliverToys = {
    log.info("Reindeer " + i + " is delivering toys")
  }
}