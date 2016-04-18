package xtuc

import akka.util.Timeout
import xtuc.ping.Ping
import scala.concurrent.duration._

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
    case _ => ""
  }

  def genTable(l: List[String]): List[String] = padRight("PORT", SPACE) + "STATE" :: l
  def genHeader(hostsToPing: Int, portsToPing: Int) = s"${hostsToPing} host(s) to ping (${portsToPing} port(s))"
}
