package logicbox

import spray.json._
import logicbox.server.format.RawProof
import logicbox.framework.ValidationResult
import logicbox.server.format.RawProof
import logicbox.server.ArithLogicProofValidatorService
import logicbox.server.PredLogicProofValidatorService
import logicbox.server.PropLogicProofValidatorService

case class VerifyBody(
  proof: RawProof,
  logicName: String,
)

object Main extends DefaultJsonProtocol {
  import logicbox.server.format.SprayFormatters._
  implicit val bodyFormat: JsonFormat[VerifyBody] = jsonFormat2(VerifyBody.apply)
  implicit object validationResultWriter extends JsonWriter[ValidationResult] {
    override def write(result: ValidationResult): JsValue = {
      JsObject(
        "proof" -> rawProofFormat.write(result.proof),
        "diagnostics" -> JsArray(
          result.diagnostics.map(outputErrorWriter.write)
        )
      )
    }
  }

  def main(args: Array[String]): Unit = println("hello world")
  def verify(input: String): String = (for {
    _ <- Some(())
    VerifyBody(proof, logicName) = bodyFormat.read(JsonParser(input))
    validator <- logicName match {
      case "propositionalLogic" => Some(PropLogicProofValidatorService())
      case "predicateLogic" => Some(PredLogicProofValidatorService())
      case "arithmetic" => Some(ArithLogicProofValidatorService())
      case _ => None
    }
    res = validator.validateProof(proof) match {
      case Left(value) => value.toString
      case Right(result) => validationResultWriter.write(result).prettyPrint
    }
  } yield res).getOrElse("err")
}
