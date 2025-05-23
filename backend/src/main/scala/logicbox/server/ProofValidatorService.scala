package logicbox.framework

import spray.json._
import logicbox.server.format.RawProof

case class ValidationResult(
  proof: RawProof,
  diagnostics: List[Diagnostic[String]]
)

trait ProofValidatorService[Err] {
  def validateProof(proof: RawProof): Either[Err, ValidationResult]
}
