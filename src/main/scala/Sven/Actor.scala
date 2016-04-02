package Sven

import akka.actor.Actor

case object getEvents

case class newEvent(event: Event)
case class Event(name: String, payload: String)

class TestActor extends Actor {
  var events: List[Event] = List()
  var state = ""

  def receive = {
    case newEvent(x) => events = x :: events
    case getEvents => sender ! events // Send the current greeting back to the sender
  }
}