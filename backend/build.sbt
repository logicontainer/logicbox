scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"            % "2.0.19",
  "dev.zio"       %% "zio-http"       % "3.0.0-RC6+36-d283e073-SNAPSHOT"
)

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

dockerExposedPorts := Seq(8080)

dockerUsername   := sys.props.get("docker.username")
dockerRepository := sys.props.get("docker.registry")

resolvers ++= Resolver.sonatypeOssRepos("snapshots")
