package logicbox.server

import zio._
import zio.http._

import spray.json._
import logicbox.server.PropLogicProofValidatorService
import zio.http.Middleware.{CorsConfig, cors}
import logicbox.server.format.RawProof

case class VerifyBody(
  proof: RawProof,
  ruleset: String,
)

object GreetingRoutes {
  import logicbox.server.format.SprayFormatters._
  val config: CorsConfig = CorsConfig()

  implicit val bodyFormat: JsonFormat[VerifyBody] = jsonFormat2(VerifyBody.apply)

  def apply(): Routes[Any, Nothing] =
    Routes(
      Method.POST / "verify" -> handler {
        (req: Request) => 
          (for {
            body <- req.body.asString
            json = bodyFormat.read(JsonParser(body))
            // res = PropLogicProofValidatorService().validateProof(json) match {
            //   case Left(value) => Response.text(value.toString)
            //   case Right(value) => Response.text(value.prettyPrint).updateHeaders(
            //     headers => headers.addHeader(Header.ContentType(MediaType.application.json))
            //   )
            // }
          } yield ???).catchAll(err => ZIO.succeed(Response.status(Status.InternalServerError)))
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
