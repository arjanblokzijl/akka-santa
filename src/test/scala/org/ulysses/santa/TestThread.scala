package org.ulysses.santa

import se.scalablesolutions.akka.util.Logging
import java.util.concurrent.CountDownLatch

/**
 * User: arjan
 * Date: Aug 25, 2010
 * Time: 5:45:53 PM
 */

class TestThread(l: CountDownLatch, op: => Unit) extends Thread with Logging {
    override def run:Unit = {
     try {
       log.debug("start operation")
       op
       log.debug("finished operation")
       l.countDown
     } catch {
         case ex:InterruptedException => {log.warning("interrupted!")}
         case ex:Exception => {log.warning("Exception! " + ex.getMessage); throw ex}
     }
  }

}