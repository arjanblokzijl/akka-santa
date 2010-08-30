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
class GroupTest extends Specification with Logging {
  implicit protected val tfn: String = this.getClass.getName

  "a Group" should {
    "block when joining if group is full" in {
      val g = Group(0)
      val doneSignal = new CountDownLatch(1);
      val t1 = new TestThread(doneSignal, {g.joinGroup})
      t1.start

      Thread.sleep(500)

      doneSignal.getCount must be equalTo(1)
      t1.isAlive must be equalTo(true)
      t1.interrupt
    }
  }


    "a Group" should {
    "block when full" in {
      val doneSignal = new CountDownLatch(2);

      val g = Group(2)

      val t1 = new TestThread(doneSignal, {g.joinGroup})
      val t2 = new TestThread(doneSignal, {g.joinGroup})
      val t3 = new TestThread(doneSignal, {g.joinGroup})
      t1.start
      t2.start
      t3.start

      Thread.sleep(500)
//      doneSignal.await

      //One thread must be blocking, since it called join on a group which is full
      numOfLiveThreads(t1, t2, t3) must be equalTo(1)
      
      interruptAliveThreads(t1, t2, t3)
    }

    def join(g: Group): Unit = {
      log.debug("joining Group")
      g.joinGroup
      log.debug("Finished joining Group")
    }
  }
}
class JoinGroup(g:Group) extends Runnable with Logging {
    def run:Unit = {
     try {
       log.debug("joining group")
       g.joinGroup
       log.debug("finished joining group")
     } catch {
        case ex:InterruptedException => {log.warning("thread was interrupted")}
        case ex:Exception => {log.warning("thread stopped with exception: " + ex.getMessage)}
     } // return;
  }
}