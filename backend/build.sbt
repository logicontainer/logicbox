scalaVersion := "3.4.3"
libraryDependencies ++= Seq(
  "org.scala-lang" %% "toolkit" % "0.1.7",
  // for parsing
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.4.0",
  // unit testing library
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "org.scalatestplus" %% "mockito-5-12" % "3.2.19.0" % Test, // mocks

  // for JSON marshalling/unmarshalling
  "io.spray" %%  "spray-json" % "1.3.6",
)

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

// use java version 11 for compiled sources
javacOptions ++= Seq("-source", "11", "-target", "11")

// require that docker JVM supports java 11
dockerBaseImage := "openjdk:11"
dockerExposedPorts := Seq(8080)

dockerUsername   := sys.props.get("docker.username")
dockerRepository := sys.props.get("docker.registry")

resolvers ++= Resolver.sonatypeOssRepos("snapshots")

assembly / assemblyJarName := "logicbox_lib.jar"  // Output JAR name
assembly / test := {}  // Skip tests during assembly
assembly / mainClass := Some("logicbox.Main")

// Merge strategy for conflicts
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("META-INF", "versions", _*) => MergeStrategy.discard
  case PathList("META-INF", _*) => MergeStrategy.first
  case "module-info.class" => MergeStrategy.discard
  case x => 
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}
