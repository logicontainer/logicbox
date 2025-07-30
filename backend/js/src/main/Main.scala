package logicbox

import scala.scalajs.js.annotation._

import logicbox.server.format.RawProof 
import logicbox.framework.ValidationResult
import logicbox.server.format.RawProof
import logicbox.server.ArithLogicProofValidatorService
import logicbox.server.PredLogicProofValidatorService
import logicbox.server.PropLogicProofValidatorService
import scala.util.Success
import scala.util.Failure

case class VerifyBody(
  proof: RawProof,
  logicName: String,
)

import io.circe._, io.circe.syntax._, io.circe.generic.semiauto._, io.circe.parser._
import server.format.JsonFormatters._

implicit val verifyBodyDecoder: Decoder[VerifyBody] = deriveDecoder[VerifyBody]
implicit val validationResultEncoder: Encoder[ValidationResult] = deriveEncoder[ValidationResult]

@JSExportTopLevel("Main")
object Main {

  @JSExport("verify")
  def verify(input: String): String = (for {
    json <- parse(input).toTry
    VerifyBody(proof, logicName) <- verifyBodyDecoder.decodeJson(json).toTry
    validator <- logicName match {
      case "propositionalLogic" => Success(PropLogicProofValidatorService())
      case "predicateLogic" => Success(PredLogicProofValidatorService())
      case "arithmetic" => Success(ArithLogicProofValidatorService())
      case _ => Failure(RuntimeException("Invalid logic name"))
    }
    res = validator.validateProof(proof) match {
      case Left(value) => value.toString
      case Right(result) => result.asJson.noSpaces
    }
  } yield res) match {
    case Success(value) => value
    case f => f.toString
  }
}
