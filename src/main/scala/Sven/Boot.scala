package Sven

import java.util.Date

import akka.actor.{ActorRef, Inbox, Props, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import scala.concurrent.duration._

object Boot extends App {

  implicit val timeout = akka.util.Timeout(3.seconds)
  implicit val system = ActorSystem("akka-http-test")
  implicit val materializer = ActorMaterializer()

  val greeter = system.actorOf(Props[Greeter], "greeter")

  // Create an "actor-in-a-box"
  val inbox = Inbox.create(system)

  val host = "0.0.0.0"
  val port = 8080

  val WebService = new WebService(inbox, greeter)

  // create a new HttpServer using our handler and tell it where to bind to
  Http() bindAndHandle(WebService.routes, host, port)

  println(s"HTTP server bound to host $host and port $port.")
}