package logicbox.server

import logicbox.framework._

import scala.util.Try

import logicbox.server.format.RawProof
import logicbox.server.format.RawProofConverter
import logicbox.server.format.OutputError
import logicbox.server.ProofValidatorServiceImpl.ErrorNotConvertible

object ProofValidatorServiceImpl {
  sealed trait Error
  case class ErrorNotConvertible(id: String, error: logicbox.framework.Error) extends Error
}

class ProofValidatorServiceImpl[F, R, B](
  val rawProofConverter: RawProofConverter[Proof[F, R, B, String]],
  val proofChecker: ProofChecker[F, R, B, String],
  val createErrorConverter: Proof[F, R, B, String] => ErrorConverter
) extends ProofValidatorService[ProofValidatorServiceImpl.Error] {
  override def validateProof(rawProof: RawProof): Either[ProofValidatorServiceImpl.Error, ValidationResult] = {
    val proof = rawProofConverter.convertFromRaw(rawProof)
    val errors = proofChecker.check(proof)
    val outputRawProof = rawProofConverter.convertToRaw(proof)
    val errorConverter = createErrorConverter(proof)
  
    val outputDiagnostics = errors.map((id, e) => errorConverter.convert(id, e))
    if outputDiagnostics.forall(_.isDefined) then
      Right(ValidationResult(
        proof = outputRawProof,
        diagnostics = outputDiagnostics.flatten
      ))
    else {
      val lilRascalIdx = outputDiagnostics.indexOf(None)
      val (id, err) = errors(lilRascalIdx)
      Left(ErrorNotConvertible(id, err))
    }
  }
}
