package xtuc
package cache

import xtuc.ping.Ping

case class CacheResult[A](op: Ping, r: Option[A])
object GetResults