import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "uk.tobyhobson",
      scalaVersion := "2.13.1",
      version      := "1.0-SNAPSHOT"
    )),
    name := "blog-samples",
    scalacOptions ++= Seq("-feature", "-language:higherKinds", "-deprecation"),
    libraryDependencies ++= Seq(
      cats,
      monix,
      scalaTest % Test,
    )
  )

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)
