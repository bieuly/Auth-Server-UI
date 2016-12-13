name := """test-app"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "net.liftweb" %% "lift-json" % "2.6+",
  "com.digitalpaytech" %% "auth-client" % "1.3.4"

)

