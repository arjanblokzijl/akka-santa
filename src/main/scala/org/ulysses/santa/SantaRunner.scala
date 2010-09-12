package org.ulysses.santa

import scala.util.Random
import org.slf4j.LoggerFactory
import org.slf4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: arjan
 * Date: Jan 17, 2010
 * Time: 8:36:13 AM
 * To change this template use File | Settings | File Templates.
 */

object SantaRunner {
  val log = LoggerFactory getLogger SantaRunner.getClass.getName
  def main(args:Array[String]) = {
    System.setProperty("org.multiverse.tracing.enabled", "true")
    val elfGroup = Group(3)
    val reindeerGroup = Group(9)
    val santa = new Thread(new Santa(elfGroup, reindeerGroup))
    santa start

    log.debug("starting elfs")
    1 to 10 foreach {i => new Thread(new Elf(elfGroup, i)) start}
    log.debug("starting reindeers")
    1 to 9 foreach {i => new Thread(new Reindeer(reindeerGroup, i)) start}
  }

  def repeat[T](n:Int)(op: T):Unit = {
    if (n > 0) {op; repeat(n-1)(op)}
  }

  def repeatDelayed[T](n:Int)(op: T):Unit = {
    if (n > 0) {op; Thread.sleep(Random.nextInt(1000)); repeatDelayed(n-1)(op)}
  }

}