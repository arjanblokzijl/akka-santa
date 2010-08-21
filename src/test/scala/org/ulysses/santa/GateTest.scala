package org.ulysses.santa

import se.scalablesolutions.akka.util.Logging
import org.specs.Specification
import java.util.concurrent.CountDownLatch

/**
 * Created by IntelliJ IDEA.
 * User: arjan
 * Date: Jan 30, 2010
 * Time: 7:50:51 AM
 * To change this template use File | Settings | File Templates.
 */

class GateTest extends Specification with Logging {
  implicit protected val tfn: String = this.getClass.getName

  //  @Test
  //  def shouldDecreaseCapacityWhenPassingGate = {
  //    val g = Gate.newGate(3)
  //    g.passGate
  //    assert(2 === g.getRemaining)
  //  }

  "a Gate" should {
    "Pass if all elements satisfy the expectation" in {
      val g = Gate(1)
      g.passGate
      g.getRemaining must be equalTo (0)
      g.passGate must throwAn[RuntimeException]
    }
  }

  "a Gate" should {
    "Operate when everyone joined" in {
      val doneSignal = new CountDownLatch(3);
      val waitSignal = new CountDownLatch(2);
      val opWaitSignal = new CountDownLatch(0);

      val g = Gate(2)
      new Thread(new Worker(opWaitSignal, doneSignal, op(g))).start
      Thread.sleep(10)

      new Thread(new Worker(waitSignal, doneSignal, pass(g))).start
      new Thread(new Worker(waitSignal, doneSignal, pass(g))).start
      log.debug("countDown for workers started")
      waitSignal.countDown
      waitSignal.countDown

      doneSignal.await()
      log.debug("doneSignal has finished")
      g.getRemaining must be equalTo (0)

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

}
//  @Test
//  def shouldFailWhenNoCapacityLeft = {
//    val g = Gate.newGate(1)
//    g.passGate
//    assert(0 === g.getRemaining)
//    try {
//      g.passGate
//      fail("Should fail when capacity is zero")
//    } catch {case e: RuntimeException => {}} //expected
//  }
//
//  @Test
//  def shouldOperateWhenEveryoneJoined = {
//    val doneSignal = new CountDownLatch(3);
//    val waitSignal = new CountDownLatch(2);
//    val opWaitSignal = new CountDownLatch(0);
//
//    val g = Gate.newGate(2)
//    new Thread(new Worker(opWaitSignal, doneSignal, op(g))).start
//    Thread.sleep(10)
//
//    new Thread(new Worker(waitSignal, doneSignal, pass(g))).start
//    new Thread(new Worker(waitSignal, doneSignal, pass(g))).start
//    log.debug("countDown for workers started")
//    waitSignal.countDown
//    waitSignal.countDown
//
//    doneSignal.await()
//    log.debug("doneSignal has finished")
//    assert(0 === g.getRemaining)
//  }
//
