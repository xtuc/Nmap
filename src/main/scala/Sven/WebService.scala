package Sven

import java.util.Date

import akka.actor.{ActorRef, Inbox}
import akka.http.scaladsl.model.{HttpHeader, HttpResponse}
import akka.http.scaladsl.model.StatusCodes.{OK, NotFound}
import akka.http.scaladsl.server.Directives._
import scala.concurrent.duration._

/**
  * Created by Sven on 02/04/2016.
  */
case class WebService(inbox: Inbox, greeter: ActorRef, persistentActor: ActorRef) {

  def fooRoute = {
    inbox.send(greeter, getEvents) // Ask for state
    val events = inbox.receive(10.seconds)

    if (events == Nil) complete(NotFound)
    else complete(events.toString)
  }

  def newStateRoute = {
    val state = (new Date).toString
    greeter.tell(newEvent(Event("Date", state)), ActorRef.noSender)

    complete(state)
  }

  def loginRoute(username: String, password: String) = {
    complete(username)
  }

  def persistentActorFooRoute = {
    val command = Cmd((new Date).toString)
    persistentActor.tell(command, ActorRef.noSender)

    complete(command.toString)
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
      } ~
      path("login") {
        post {
          formFields("username") { username =>
            formFields("password") { password =>
              loginRoute(username, password)
            }
          }
        }
      } ~
      pathPrefix("persistent") {
        path("test") {
          get {
            persistentActorFooRoute
          }
        }
      }
    }
  }
}

object WebService {

}