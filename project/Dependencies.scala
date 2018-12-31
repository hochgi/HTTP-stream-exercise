import sbt._

object Dependencies {

  val akkaVersion = "2.5.19"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
}
