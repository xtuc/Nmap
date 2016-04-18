package xtuc

import java.util.concurrent.CountDownLatch
import xtuc.cache.{GetResults, ResultsCacheActor}
import akka.actor.{ActorRef, Props, ActorSystem}
import org.apache.commons.net.util.SubnetUtils
import xtuc.ping.{PingSenderActor, Ping}
import scala.collection.immutable.SortedMap
import scala.concurrent.ExecutionContextExecutor
import scala.util.Success
import akka.pattern.ask

object Boot extends App with Utils with AppLock {
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
    ports.foreach(p => ping ! (Ping(h, p), resultsCache, () => lockCountDown))
  })

  lockAwait

  resultsCache ? GetResults onComplete {
    case Success(Some(x: SortedMap[Ping, Option[Boolean]])) =>
      showTable(x.filter(_._2.isDefined).map(show).toList)
      system.terminate()
    case _ => println("No results")
  }
}