package logicbox.framework

import logicbox.server.format.RawProof
import logicbox.server.format.OutputError

case class ValidationResult(
  proof: RawProof,
  diagnostics: List[OutputError]
)

trait ProofValidatorService[Err] {
  def validateProof(proof: RawProof): Either[Err, ValidationResult]
}
