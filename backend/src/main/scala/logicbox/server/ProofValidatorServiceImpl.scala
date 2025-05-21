package logicbox.server

import logicbox.framework.ProofValidatorService
import logicbox.framework.ModifyProofCommand
import logicbox.framework.JsonReaderWithErr
import logicbox.framework.ProofChecker

import spray.json._
import logicbox.framework.ModifiableProof
import logicbox.framework.Proof
import logicbox.proof.ProofModifier
import logicbox.framework.Diagnostic

class ProofValidatorServiceImpl[F, R, B, Id, E](
  val proofReader: JsonReaderWithErr[List[ModifyProofCommand[F, R, Id]], E],
  val proofChecker: ProofChecker[F, R, B, Id],
  val proofWriter: JsonWriter[Proof[F, R, B, Id]],
  val getEmptyProof: () => ModifiableProof[F, R, B, Id],
  val diagnosticWriter: JsonWriter[Diagnostic[Id]]
) extends ProofValidatorService[E | ModifiableProof.Error[Id]] {
  private type Err = E | ModifiableProof.Error[Id]

  override def validateProof(proofJson: JsValue): Either[Err, JsValue] = for {
    cmds <- proofReader.read(proofJson)
    proof <- ProofModifier.modify(getEmptyProof(), cmds)
    diagnostics = proofChecker.check(proof).map(diagnosticWriter.write)
  } yield JsObject(
    "proof" -> proofWriter.write(proof),
    "diagnostics" -> JsArray(diagnostics)
  )
}
