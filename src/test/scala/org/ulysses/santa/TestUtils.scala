package org.ulysses.santa

import java.util.concurrent.CountDownLatch

/**
 * User: arjan
 * Date: Aug 24, 2010
 * Time: 5:01:09 AM
 */

object TestUtils {

   def numOfLiveThreads(ts:Thread*) = {
      ts.foldLeft(0){(i,t) => if (t.isAlive) i+1 else i}
    }


    def interruptAliveThreads(ts:Thread*) = {
        ts.foreach{t => if (t.isAlive) t.interrupt}
    }
}