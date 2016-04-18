package xtuc.ping

import akka.actor.ActorRef

case class MakePing(address: String, portFrom: Int, portTo: Int, PingSender: ActorRef, cache: ActorRef)

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
