package xtuc

import xtuc.cache.{GetResults, ResultsCacheActor}
import akka.actor.{ActorRef, Props, ActorSystem}
import org.apache.commons.net.util.SubnetUtils
import xtuc.ping.{PingSenderActor, Ping}
import scala.collection.immutable.SortedMap
import scala.concurrent.ExecutionContextExecutor
import scala.util.Success
import akka.pattern.ask

object App extends App with Utils with AppLock {
  val rawAddress = args(0)

  val hosts: List[String] = if (rawAddress.contains("/")) new SubnetUtils(rawAddress).getInfo.getAllAddresses.toList
                            else List(rawAddress)

  val ports: List[Int] = if(args.length > 2) args(1).toInt to args(2).toInt toList
                         else List(args(1).toInt)

  val system = ActorSystem("default-sys")
  implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

  val ping = system.actorOf(Props[PingSenderActor], "default-ping-sender")
  val resultsCache: ActorRef = system.actorOf(Props[ResultsCacheActor], "results-cache")

  println(genHeader(hosts.length, ports.length))

  createLock(ports.length * hosts.length)

  hosts.foreach(h => {
    ports.foreach(p => ping ! (Ping(h, p), resultsCache, () => lockCountDown))
  })

  lockAwait

  resultsCache ? GetResults onComplete {
    case Success(Some(x: SortedMap[Ping, Option[Boolean]])) =>
      genTable(x.filter(_._2.isDefined).map(show).toList) foreach println
      system.terminate()
    case _ => println("No results")
  }
}