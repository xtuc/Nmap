package xtuc

import java.util.concurrent.CountDownLatch

object Lock {
  var lock: Option[CountDownLatch] = None
}

trait AppLock {
  def createLock(n: Int) = Lock.lock = Some(new CountDownLatch(n))
  def lockAwait = Lock.lock.map(_.await)
  def lockCountDown = Lock.synchronized(Lock.lock.map(_.countDown))
}