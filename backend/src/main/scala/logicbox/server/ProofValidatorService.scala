package logicbox.framework

import spray.json._

trait ProofValidatorService[Err] {
  def validateProof(proof: JsValue): Either[Err, JsValue]
}
