package Sven

import java.net.{InetSocketAddress, Socket}

import akka.actor.{ActorRef, Actor}
import akka.util.Timeout
import scala.concurrent.Future
import scala.util.{Failure, Success}

class PingSenderActor extends Actor with Utils {
  implicit val ec = context.dispatcher

  def receive = {
    case (Ping(ip, port), cache: ActorRef) => ping(ip, port, cache)
  }

  def ping(ip: String, port: Int, cache: ActorRef)(implicit timeout: Timeout): Unit = {
//    println(Console.BLUE + " ping " + ip + ":" + port + Console.RESET)

    val f: Future[Boolean] = Future {
      val socket = new Socket()
      socket.connect(new InetSocketAddress(ip, port), timeout.duration.toMillis.toInt)
      socket.close()
      true
    }

    f onComplete {
      case Success(x) => cache ! CacheResult[Boolean](Ping(ip, port), Some(x))
      case Failure(ex) => cache ! CacheResult[Boolean](Ping(ip, port), None)
    }
  }
}