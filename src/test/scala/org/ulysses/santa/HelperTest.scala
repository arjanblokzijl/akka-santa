package org.ulysses.santa

import se.scalablesolutions.akka.util.Logging
import org.specs.Specification
import java.util.concurrent.CountDownLatch
import org.ulysses.santa.TestUtils._

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
      val elf = Elf1(Group(1), 1)
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