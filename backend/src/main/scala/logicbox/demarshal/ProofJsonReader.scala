package logicbox.demarshal

import spray.json._
import logicbox.framework._
import logicbox.json._
import logicbox.framework.Justification

case class ProofJsonReader[F, R, Id](
  formulaParser: String => F,
  ruleParser: String => R,
  idParser: String => Id,
  jsonProofFormat: RootJsonFormat[JsonProof]
) extends RootJsonReader[Proof[F, R, Unit, Id]] {

  private def convertSteps(steps: List[JsonProofStep]): Map[Id, Proof.Step[F, R, Unit, Id]] = ???

  override def read(json: JsValue): Proof[F, R, Unit, Id] = {
    val jsPf: List[JsonProofStep] = jsonProofFormat.read(json)
    ???
  }
}
