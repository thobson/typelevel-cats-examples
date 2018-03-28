import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "uk.tobyhobson",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "blog-samples",
    libraryDependencies ++= Seq(
      cats,
      scalaTest % Test,
      compilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6")
    )
  )
