package logicbox.server

import zio._
import zio.http._

import logicbox.server.PropLogicProofValidatorService
import zio.http.Middleware.{CorsConfig, cors}
import logicbox.server.format.RawProof
import logicbox.framework.ValidationResult

import io.circe._, io.circe.syntax._, io.circe.parser._

object GreetingRoutes {
  val config: CorsConfig = CorsConfig()

  def apply(): Routes[Any, Nothing] =
    Routes(
      Method.POST / "verify" -> handler {
        (req: Request) => 
          (for {
            body <- req.body.asString
            json <- ZIO.fromEither(parse(body))
            resultJson = JsonVerifier.verify(json)
            response = Response.text(resultJson.noSpaces).updateHeaders(
              headers => headers.addHeader(Header.ContentType(MediaType.application.json))
            )
          } yield response).catchAll(err => ZIO.succeed(Response.status(Status.InternalServerError)))
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
