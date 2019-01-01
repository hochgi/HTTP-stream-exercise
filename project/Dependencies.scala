import sbt._

object Dependencies {
  
  lazy val scalaTest    = "org.scalatest"              %% "scalatest"     % "3.0.5"
  lazy val akkaStream   = "com.typesafe.akka"          %% "akka-stream"   % "2.5.19"
  lazy val akkaHttp     = "com.typesafe.akka"          %% "akka-http"     % "10.1.6"
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
  lazy val scallop      = "org.rogach"                 %% "scallop"       % "3.1.5"
  
  val jsoniterVersion = "0.37.6"
  
  lazy val jsoniterCore   = "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core"   % jsoniterVersion 
  lazy val jsoniterMacros = "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % jsoniterVersion 
}
