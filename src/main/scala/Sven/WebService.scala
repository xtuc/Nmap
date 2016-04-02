package Sven

import java.util.Date

import akka.actor.{ActorRef, Actor, Inbox}
import akka.http.scaladsl.model.StatusCodes.{OK}
import akka.http.scaladsl.server.Directives._
import scala.concurrent.duration._

/**
  * Created by Sven on 02/04/2016.
  */
case class WebService(inbox: Inbox, greeter: ActorRef) {

  def fooRoute = {
    inbox.send(greeter, getState) // Ask for state
    val State(state) = inbox.receive(10.seconds)

    complete(state)
  }

  def newStateRoute = {
    val state = (new Date).toString
    greeter.tell(newState(state), ActorRef.noSender)

    complete(state)
  }

  def routes = {
    logRequestResult(("Web server", akka.event.Logging.InfoLevel)) {
      path("state") {
        get {
          fooRoute
        }
      } ~
      path("newState") {
        get {
          newStateRoute
        }
      }
    }
  }
}

object WebService {

}