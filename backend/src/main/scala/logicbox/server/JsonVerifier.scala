package logicbox.server

import logicbox.server.format.RawProof
import logicbox.framework.ValidationResult

object JsonVerifier {
  case class VerifyBody(
    proof: RawProof,
    logicName: String,
  )

  import io.circe._, io.circe.syntax._, io.circe.generic.semiauto._, io.circe.parser._
  import format.JsonFormatters._

  implicit val verifyBodyDecoder: Decoder[VerifyBody] = deriveDecoder[VerifyBody]
  implicit val validationResultEncoder: Encoder[ValidationResult] = deriveEncoder[ValidationResult]

  def verify(input: Json): Json = (for {
    VerifyBody(proof, logicName) <- verifyBodyDecoder.decodeJson(input)
      .left.map(d => Json.obj("message" -> d.toString.asJson))

    service <- logicName match {
      case "propositionalLogic" => Right(PropLogicProofValidatorService())
      case "predicateLogic" => Right(PredLogicProofValidatorService())
      case "arithmetic" => Right(ArithLogicProofValidatorService())
      case _ => Left(Json.obj("message" -> s"Invalid logicName: $logicName".asJson))
    }

    result <- service.validateProof(proof).left.map(d => Json.obj("message" -> d.toString.asJson))
  } yield result.asJson).merge
}
