package logicbox

import zio._
import zio.http._

import spray.json.JsonParser
import logicbox.server.StandardProofValidatorService

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

      Method.POST / "verify" -> handler {
        (req: Request) => 
          (for {
            body <- req.body.asString
            json = JsonParser(body)
            res = StandardProofValidatorService().validateProof(json) match {
              case Left(value) => Response.text(value.toString)
              case Right(value) => Response.text(value.prettyPrint)
            }
          } yield res).catchAll(err => ZIO.succeed(Response.text("bruh")))
      }
    )
}

// TODO: bug with List() in refs to not not intro

object Main extends ZIOAppDefault {
  def run = {
    Server
      .serve(GreetingRoutes())
      .provide(Server.defaultWithPort(8080))
  }
}
