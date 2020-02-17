import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.0-M2"
  lazy val cats = "org.typelevel" %% "cats-core" % "2.1.0"
  lazy val monix = "io.monix" %% "monix" % "3.1.0"
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
}
