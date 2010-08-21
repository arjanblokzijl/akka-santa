package org.ulysses.santa

import java.util.concurrent.CountDownLatch
import se.scalablesolutions.akka.util.Logging

/**
 * Created by IntelliJ IDEA.
 * User: arjan
 * Date: Mar 7, 2010
 * Time: 8:05:13 AM
 * To change this template use File | Settings | File Templates.
 */

class Worker(startSignal:CountDownLatch, doneSignal: CountDownLatch, f: => Unit)(implicit tfn:String) extends Runnable with Logging {

  def run:Unit = {
     try {
       startSignal.await();
       log.debug("doing work f")
       f
       doneSignal.countDown();

       log.debug("Counted down on doneSignal, remaining count: " + doneSignal.getCount)
     } catch {case ex:InterruptedException => {}} // return;
  }

  def doWork(f:Gate => Unit):Unit = {
      log.debug("Worker is doing work: f")
      f
  }
}