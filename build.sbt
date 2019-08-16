import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "uk.tobyhobson",
      scalaVersion := "2.12.8",
      version      := "1.0-SNAPSHOT"
    )),
    name := "blog-samples",
    scalacOptions ++= Seq("-feature", "-language:higherKinds"),
    libraryDependencies ++= Seq(
      cats,
      shapeless,
      scalaTest % Test,
      compilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6")
    )
  )
