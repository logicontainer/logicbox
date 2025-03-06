scalaVersion := "3.4.3"
libraryDependencies ++= Seq(
  "org.scala-lang" %% "toolkit" % "0.1.7",
  // for parsing
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.4.0",
  // unit testing library
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  // for JSON marshalling/unmarshalling
  "io.spray" %%  "spray-json" % "1.3.6",

  // HTTP
  "dev.zio"       %% "zio"            % "2.0.19",
  "dev.zio"       %% "zio-http"       % "3.0.1"
)

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

dockerExposedPorts := Seq(8080)

dockerUsername   := sys.props.get("docker.username")
dockerRepository := sys.props.get("docker.registry")

resolvers ++= Resolver.sonatypeOssRepos("snapshots")

