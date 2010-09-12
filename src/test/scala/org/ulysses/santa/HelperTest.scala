package org.ulysses.santa


import org.specs.Specification
import java.util.concurrent.CountDownLatch
import org.ulysses.santa.TestUtils._
import se.scalablesolutions.akka
import akka.util.Logging
import akka.stm.Ref;
import akka.stm.local._
import akka.util.duration._

/**
 * Created by IntelliJ IDEA.
 * User: arjan
 * Date: Jan 30, 2010
 * Time: 7:50:51 AM
 * To change this template use File | Settings | File Templates.
 */

class HelperTest extends Specification with Logging {
  implicit protected val tfn: String = this.getClass.getName

  "an Elf" should {
    "meet in study when it can pass the gate" in {
      val g1 = Group(1)
      val elf = Elf1(g1, 1)
      atomic {
        val in  = g1.ref.get._2
        val out  = g1.ref.get._3
        in.remaining.set(1)
        out.remaining.set(1)
      }

      elf.doTask({elf.meetInStudy})
      elf.metInStudy must be equalTo (1)
    }
  }

  "an Elf" should {
    "block it cannot pass the gate" in {
      val elf = Elf1(Group(0), 1)

      val waitLatch = new CountDownLatch(1)
      val t1 = new TestThread(waitLatch, elf.tryMeet)
      t1.start
      
      Thread.sleep(200)

      //One thread must be blocking, since it called join on a group which is full
      numOfLiveThreads(t1) must be equalTo(1)

      interruptAliveThreads(t1)
    }
  }
}