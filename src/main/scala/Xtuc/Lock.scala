package xtuc

import java.util.concurrent.CountDownLatch

object Lock {
  var lock: Option[CountDownLatch] = None
}

trait AppLock {
  def lockAwait = Lock.lock.map(_.await)
  def lockCountDown = Lock.synchronized(Lock.lock.map(_.countDown))
}