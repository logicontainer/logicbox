scalaVersion := "3.4.3"
val circeVersion = "0.14.14"
libraryDependencies ++= Seq(
  "org.scala-lang" %% "toolkit" % "0.1.7",
  // for parsing
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.4.0",
  // unit testing library
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "org.scalatestplus" %% "mockito-5-12" % "3.2.19.0" % Test, // mocks

  // HTTP
  "dev.zio"       %% "zio"            % "2.0.19",
  "dev.zio"       %% "zio-http"       % "3.0.1",
  
  // JSON
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
)

// use java version 11 for compiled sources
javacOptions ++= Seq("-source", "11", "-target", "11")

Compile / run / mainClass := Some("logicbox.Main")
