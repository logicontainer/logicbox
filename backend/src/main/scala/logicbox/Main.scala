package logicbox

import zio._
import zio.http._

object GreetingRoutes {
  def apply(): Routes[Any, Nothing] =
    Routes(
      // GET /greet
      Method.GET / "greet" -> handler(Response.text(s"Hello World!")),

      // GET /greet/:name
      Method.GET / "greet" / string("name") -> handler {
        (name: String, _: Request) =>
          Response.text(s"Hello $name!")
      },
    )
}

object Main extends ZIOAppDefault {
  def run = {
    Server
      .serve(GreetingRoutes())
      .provide(Server.defaultWithPort(8080))
  }
}
