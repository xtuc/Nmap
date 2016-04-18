package Xtuc

import java.util.concurrent.CountDownLatch
import akka.actor.{ActorRef, Props, ActorSystem}
import akka.util.Timeout
import org.apache.commons.net.util.SubnetUtils
import scala.collection.immutable.SortedMap
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.Success
import akka.pattern.ask

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
  val inputAddress = args(0)
  val ports: List[Int] = if(args(2).isEmpty) args(1).toInt to args(2).toInt toList
                         else List(args(1).toInt)

  val system = ActorSystem("default-sys")
  implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

  val ping = system.actorOf(Props[PingSenderActor], "default-ping-sender")
  val resultsCache: ActorRef = system.actorOf(Props[ResultsCacheActor], "results-cache")

  val hosts: List[String] = if (inputAddress.contains("/")) new SubnetUtils(inputAddress).getInfo.getAllAddresses.toList
                            else List(inputAddress)

  println(s"${hosts.length} host(s) to ping (${ports.length} port(s))")

  Lock.lock = Some(new CountDownLatch(ports.length * hosts.length))

  hosts.foreach(h => {
    ports.foreach(p => ping ! (Ping(h, p), resultsCache, () => Lock.synchronized(Lock.lock.map(_.countDown))))
  })

  Lock.lock.map(_.await)

  resultsCache ? GetResults onComplete {
    case Success(Some(x: SortedMap[Ping, Option[Boolean]])) =>
      showTable(x.filter(_._2.isDefined).map(show).toList)
      system.terminate()
    case _ => println("No results")
  }
}

object Lock {
  var lock: Option[CountDownLatch] = None
}