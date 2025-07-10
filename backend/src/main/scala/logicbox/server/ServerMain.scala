package logicbox.server

import zio._
import zio.http._

import spray.json._
import logicbox.server.PropLogicProofValidatorService
import zio.http.Middleware.{CorsConfig, cors}
import logicbox.server.format.RawProof
import logicbox.framework.ValidationResult
import logicbox.server.format.Stringifiers

case class VerifyBody(
  proof: RawProof,
  logicName: String,
)

object GreetingRoutes {
  import logicbox.server.format.SprayFormatters._
  val config: CorsConfig = CorsConfig()

  implicit val bodyFormat: JsonFormat[VerifyBody] = jsonFormat2(VerifyBody.apply)
  implicit object validationResultWriter extends JsonWriter[ValidationResult] {
    override def write(result: ValidationResult): JsValue = {
      JsObject(
        "proof" -> rawProofFormat.write(result.proof),
        "diagnostics" -> JsArray(
          result.diagnostics.map(???)
        )
      )
    }
  }

  def apply(): Routes[Any, Nothing] =
    Routes(
      Method.POST / "verify" -> handler {
        (req: Request) => 
          (for {
            body <- req.body.asString
            VerifyBody(proof, logicName) = bodyFormat.read(JsonParser(body))
            validator <- logicName match {
              case "propositionalLogic" => ZIO.succeed(PropLogicProofValidatorService())
              case "predicateLogic" => ZIO.succeed(PredLogicProofValidatorService())
              case "arithmetic" => ZIO.succeed(ArithLogicProofValidatorService())
              case _ => ZIO.fail(s"unknown logicName: $logicName")
            }
            res = validator.validateProof(proof) match {
              case Left(value) => 
                Response.text(value.toString)

              case Right(result) => 
                Response.text(validationResultWriter.write(result).prettyPrint).updateHeaders(
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
