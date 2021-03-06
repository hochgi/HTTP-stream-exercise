import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.hochgi",
      scalaVersion := "2.12.8",
      version      := "0.0.1"
    )),
    name := "HTTP stream exercise",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      scalaLogging,
      akkaStream,
      akkaHttp,
      scallop,
      uPickle,
      uJson),
    mappings in Universal += file("resources/generator-linux-amd64") -> "resources/generator-linux-amd64"
  ).enablePlugins(JavaAppPackaging)
