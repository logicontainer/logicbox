package logicbox.server

import logicbox.framework._

import spray.json._
import scala.util.Try
import spray.json.JsonParser.ParsingException

import logicbox.server.format.RawProof
import logicbox.server.format.RawProofConverter

class ProofValidatorServiceImpl[F, R, B](
  val rawProofConverter: RawProofConverter[Proof[F, R, B, String]],
  val proofChecker: ProofChecker[F, R, B, String],
) extends ProofValidatorService[Unit] {

  override def validateProof(rawProof: RawProof): Either[Unit, ValidationResult] = {
    val proof = rawProofConverter.convertFromRaw(rawProof)
    val diagnostics = proofChecker.check(proof)
    val outputRawProof = rawProofConverter.convertToRaw(proof)
    Right(ValidationResult(
      proof = outputRawProof,
      diagnostics = diagnostics
    ))
  }
}
