package xtuc
package cache

import akka.actor.Actor
import akka.pattern.ask
import xtuc.ping.Ping

import scala.collection.immutable.SortedMap

class ResultsCacheActor extends Actor with Utils {
  var cache = Option(SortedMap[Ping, Option[Boolean]]())

  def receive = {
    case CacheResult(op, x: Option[Boolean]) => cacheResult(op, x)
    case GetResults => sender ? cache
  }

  def cacheResult(op: Ping, result: Option[Boolean]) = cache = cache.map(_.updated(op, result))
}
