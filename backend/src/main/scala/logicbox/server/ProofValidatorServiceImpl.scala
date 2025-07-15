package logicbox.server

import logicbox.framework._

import spray.json._
import scala.util.Try
import spray.json.JsonParser.ParsingException

import logicbox.server.format.RawProof
import logicbox.server.format.RawProofConverter
import logicbox.server.format.OutputError

class ProofValidatorServiceImpl[F, R, B](
  val rawProofConverter: RawProofConverter[Proof[F, R, B, String]],
  val proofChecker: ProofChecker[F, R, B, String],
  val createErrorConverter: Proof[F, R, B, String] => ErrorConverter
) extends ProofValidatorService[Unit] {
  override def validateProof(rawProof: RawProof): Either[Unit, ValidationResult] = {
    val proof = rawProofConverter.convertFromRaw(rawProof)
    val errors = proofChecker.check(proof)
    val outputRawProof = rawProofConverter.convertToRaw(proof)
    val errorConverter = createErrorConverter(proof)
    Right(ValidationResult(
      proof = outputRawProof,
      diagnostics = errors.flatMap((id, e) => errorConverter.convert(id, e))
    ))
  }
}
