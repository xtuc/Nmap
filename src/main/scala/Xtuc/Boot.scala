package Xtuc

import java.util.concurrent.CountDownLatch

import akka.actor.{ActorRef, Props, ActorSystem}
import akka.util.Timeout
import org.apache.commons.net.util.SubnetUtils
import scala.collection.immutable.{SortedMap}
import scala.concurrent.{ExecutionContextExecutor}
import scala.concurrent.duration._
import scala.util.Success
import akka.pattern.ask

case class Ping(ipv4: String, port: Int, timeout: Int = 50) extends Ordered[Ping] {

  def compare(that: Ping): Int = that match {
    case Ping(h, p, _) =>
      if(ipv4.length > h.length) 1
      else
        if (port > p) 1
        else -1
    case _ => 0
  }
}

case class MakePing(address: String, portFrom: Int, portTo: Int, PingSender: ActorRef, cache: ActorRef)
case class CacheResult[A](op: Ping, r: Option[A])
object GetResults

trait Utils {
  implicit val timeout = Timeout(30 seconds)

  def padRight(s: String, n: Int) = String.format("%1$-" + n + "s", s)
  def padLeft(s: String, n: Int) = String.format("%1$" + n + "s", s)

  val OPEN = Console.BLUE + "Open" + Console.RESET
  val CLOSED = Console.RED + "Closed" + Console.RESET
  val SPACE = 30

  def show(x: (Ping, Option[Boolean])) = x match {
    case (Ping(ip, port, _), Some(_)) => padRight(ip + ":" + port, SPACE) + OPEN
    case (Ping(ip, port, _), None) => padRight(ip + ":" + port, SPACE) + CLOSED
  }

  def showTable(l: List[String]): Unit = padRight("PORT", SPACE) + "STATE" :: l foreach println
}

object Boot extends App with Utils {
  println("args", args mkString ", ")

  val system = ActorSystem("default-sys")
  implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

  val ping = system.actorOf(Props[PingSenderActor], "default-ping-sender")
  val resultsCache: ActorRef = system.actorOf(Props[ResultsCacheActor], "results-cache")

  val portRange = 21 to 80

  val utils = new SubnetUtils("192.168.1.0/24")
  val hosts = utils.getInfo.getAllAddresses

  println(s"${hosts.length} hosts to ping")

  Lock.lock = new CountDownLatch(portRange.length * hosts.length)

  hosts.foreach(h =>
    portRange.foreach(p => {
      ping ! (Ping(h, p), resultsCache)
    })
  )

  Lock.lock.await()

  resultsCache ? GetResults onComplete {
    case Success(Some(x: SortedMap[Ping, Option[Boolean]])) =>
      showTable(x.filter(_._2.isDefined).map(show).toList)
      system.terminate()
    case Success(None) => println("No results")
  }
}

object Lock {
  var lock: CountDownLatch = null
}