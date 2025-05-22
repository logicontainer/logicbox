package logicbox.server

import zio._
import zio.http._

import spray.json.JsonParser
import logicbox.server.PropLogicProofValidatorService
import zio.http.Middleware.{CorsConfig, cors}

object GreetingRoutes {
  val config: CorsConfig = CorsConfig()

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
            res = PropLogicProofValidatorService().validateProof(json) match {
              case Left(value) => Response.text(value.toString)
              case Right(value) => Response.text(value.prettyPrint).updateHeaders(
                headers => headers.addHeader(Header.ContentType(MediaType.application.json))
              )
            }
          } yield res).catchAll(err => ZIO.succeed(Response.status(Status.InternalServerError)))
      }
    ) @@ cors(config)
}

// TODO: bug with List() in refs to not not intro

object ServerMain extends ZIOAppDefault {
  def run = {
    Server
      .serve(GreetingRoutes())
      .provide(Server.defaultWithPort(8080))
  }
}
