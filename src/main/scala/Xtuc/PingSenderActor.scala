package Xtuc

import java.net.{InetSocketAddress, Socket}

import akka.actor.{ActorRef, Actor}
import akka.util.Timeout
import scala.concurrent.Future
import scala.util.{Failure, Success}

class PingSenderActor extends Actor with Utils {
  implicit val ec = context.dispatcher

  def receive = {
    case (Ping(ip, port, timeout), cache: ActorRef) => ping(ip, port, cache)(timeout)
  }

  def ping(ip: String, port: Int, cache: ActorRef)(timeout: Int): Unit = {
//    println(Console.BLUE + " ping " + ip + ":" + port + Console.RESET)

    val f: Future[Boolean] = Future {
      val socket = new Socket()
      socket.connect(new InetSocketAddress(ip, port), timeout)
      socket.close()
      true
    }

    f onComplete {
      case x =>
        Lock.synchronized(Lock.lock.map(_.countDown))
        cache ! CacheResult[Boolean](Ping(ip, port), x toOption)
    }
  }
}