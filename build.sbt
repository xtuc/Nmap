name := "Sven"

version := "1.0"

scalaVersion := "2.11.6"

lazy val akkaVersion = "2.4.3"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion,
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.iq80.leveldb" %% "leveldb" % "0.7",
  "org.fusesource.leveldbjni" %% "leveldbjni-all" % "1.8"
)