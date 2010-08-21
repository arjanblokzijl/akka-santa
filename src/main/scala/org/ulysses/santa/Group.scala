package org.ulysses.santa

import se.scalablesolutions.akka.util.Logging
import se.scalablesolutions.akka.stm.Ref



import se.scalablesolutions.akka.stm.Transaction.Local._
import se.scalablesolutions.akka.util.Logging;

/**
 * Created by IntelliJ IDEA.
 * User: arjan
 * Date: Jan 17, 2010
 * Time: 8:34:36 AM
 * To change this template use File | Settings | File Templates.
 */
object Group {
  def apply(capacity: Int)(implicit tfn: String): Group = {
    val ref = Ref[(Int, Gate, Gate)]
    new Group(capacity, ref)
  }
}

case class Group(capacity:Int, ref:Ref[(Int, Gate, Gate)]) extends Logging {

}