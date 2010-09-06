package org.ulysses.santa

import scala.util.Random
/**
 * Created by IntelliJ IDEA.
 * User: arjan
 * Date: Jan 17, 2010
 * Time: 8:36:13 AM
 * To change this template use File | Settings | File Templates.
 */

object SantaRunner {
  def main(args:Array[String]) = {
      val elfGroup = Group(3)
      1 to 10 foreach {i => new Thread(new Elf(elfGroup, i)) start}
      val reindeerGroup = Group(9)
      1 to 9 foreach {i => new Thread(new Reindeer(reindeerGroup, i)) start}
      val santa = new Thread(new Santa(elfGroup, reindeerGroup))
      santa start
//; sequence_ [ elf elf_group n | n <- [1..10] ]
//; rein_group <- newGroup 9
//; sequence_ [ reindeer rein_group n | n <- [1..9] ]
//; forever (santa elf_group rein_group) }

  }

  def elf(g:Group, id:Int) = {
    
  }

  def repeat[T](n:Int)(op: T):Unit = {
    if (n > 0) {op; repeat(n-1)(op)}
  }

  def repeatDelayed[T](n:Int)(op: T):Unit = {
    if (n > 0) {op; Thread.sleep(Random.nextInt(500)); repeatDelayed(n-1)(op)}
  }

}