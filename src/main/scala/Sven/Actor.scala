package Sven

import akka.actor.Actor

case object getState

case class newState(state: String)
case class State(state: String)

class Greeter extends Actor {
  var state = ""

  def receive = {
    case newState(x) => state = x
    case getState           => sender ! State(state) // Send the current greeting back to the sender
  }
}