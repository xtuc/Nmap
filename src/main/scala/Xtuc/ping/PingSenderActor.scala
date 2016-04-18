package xtuc.ping

import java.net.{InetSocketAddress, Socket}

import akka.actor.{Actor, ActorRef}
import xtuc.Utils
import xtuc.cache.CacheResult

import scala.concurrent.Future

class PingSenderActor extends Actor with Utils {
  implicit val ec = context.dispatcher

  def receive = {
    case (Ping(ip, port, timeout), cache: ActorRef, onResult: (() => Unit)) => ping(ip, port, cache, onResult)(timeout)
  }

  def ping(ip: String, port: Int, cache: ActorRef, onResult: () => Unit)(timeout: Int): Unit = {

    val f: Future[Boolean] = Future {
      val socket = new Socket()
      socket.connect(new InetSocketAddress(ip, port), timeout)
      socket.close()
      true
    }

    f onComplete {
      case x =>
        onResult()
        cache ! CacheResult[Boolean](Ping(ip, port), x toOption)
    }
  }
}