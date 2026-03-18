ThisBuild / scalaVersion := "3.4.3"
val circeVersion = "0.14.14"

lazy val root = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" %%% "toolkit" % "0.1.7",
      "org.scala-lang.modules" %%% "scala-parser-combinators" % "2.4.0",
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion,

      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "org.scalatestplus" %% "mockito-5-12" % "3.2.19.0" % Test
    )
  ).enablePlugins(ScalaJSPlugin)
