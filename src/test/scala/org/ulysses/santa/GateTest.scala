package org.ulysses.santa

import se.scalablesolutions.akka.util.Logging
import org.specs.Specification
import java.util.concurrent.CountDownLatch
import se.scalablesolutions.akka
import akka.util.Logging
import akka.stm.Ref;
import akka.stm.local._
import org.ulysses.santa.TestUtils._

/**
 * Created by IntelliJ IDEA.
 * User: arjan
 * Date: Jan 30, 2010
 * Time: 7:50:51 AM
 * To change this template use File | Settings | File Templates.
 */

class GateTest extends Specification with Logging {
  implicit protected val tfn: String = this.getClass.getName

//  "a Gate" should {
//    "have remaining decremented if someone has passed" in {
//      val g = Gate(1)
//      atomic {
//        g.remaining.set(1)
//      }
//      g.passGate
//      g.getRemaining must be equalTo (0)
//    }
//  }
//
//  "a Gate" should {
//    "Operate when everyone joined" in {
//      val doneSignal = new CountDownLatch(2);
//      val operateSignal = new CountDownLatch(1);
//
//      val g = Gate(2)
//      val opt = new TestThread(operateSignal, {g.operateGate})
//      opt.start
//      Thread.sleep(10)
//
//      val t1 = new TestThread(doneSignal, {g.passGate})
//      val t2 = new TestThread(doneSignal, {g.passGate})
//      t1.start
//      t2.start
//
//      log.debug("countDown for workers started")
//      operateSignal.await
//      doneSignal.await
//      log.debug("doneSignal has finished")
//      g.getRemaining must be equalTo (0)
//    }
//}

    "a Gate" should {
      "operate and wait for gate to be full" in {
        val doneSignal = new CountDownLatch(2);
        val operateSignal = new CountDownLatch(1);

        val g = Gate(2)
        Thread.sleep(5000)
        val t1 = new TestThread(doneSignal, {g.passGate})
        val t2 = new TestThread(doneSignal, {g.passGate})
        val t3 = new TestThread(doneSignal, {g.passGate})
        t1.start
        Thread.sleep(100)
        t2.start

        Thread.sleep(100)
        val opt = new TestThread(operateSignal, {g.operateGate})
        opt.start

        log.debug("countDown for workers started")
        operateSignal.await
        doneSignal.await
        log.debug("doneSignal has finished")
        numOfLiveThreads(t1, t2) must be equalTo(0)
        
        g.getRemaining must be equalTo (0)
      }
    }

    def op(g: Gate): Unit = {
      log.debug("operating Gate")
      g.operateGate
      log.debug("finished operating Gate")
    }

    def pass(g: Gate): Unit = {
      g.passGate
    }
}

