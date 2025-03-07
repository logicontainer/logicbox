package logicbox.proof
import logicbox.framework._

object ProofModifier {
  def modify[F, R, B, Id](proof: ModifiableProof[F, R, B, Id], cmd: ModifyProofCommand[F, R, Id]): Either[ModifiableProof.Error[Id], ModifiableProof[F, R, B, Id]] = {
    cmd match {
      case AddLine(id, where) => proof.addLine(id, where)
      case AddBox(id, where) => proof.addBox(id, where)
      case UpdateFormula(lineId, formula) => proof.updateFormula(lineId, formula)
      case UpdateRule(lineId, rule) => proof.updateRule(lineId, rule)
      case UpdateReferences(lineId, refs) => proof.updateReferences(lineId, refs)
      case RemoveStep(id) => proof.removeStep(id)
    }
  }
  
  def modify[F, R, B, Id](proof: ModifiableProof[F, R, B, Id], cmds: Seq[ModifyProofCommand[F, R, Id]]): Either[ModifiableProof.Error[Id], ModifiableProof[F, R, B, Id]] = 
    cmds.foldLeft(Right(proof): Either[ModifiableProof.Error[Id], ModifiableProof[F, R, B, Id]]) {
      case (Right(proof), cmd) => modify(proof, cmd)
      case (err @ Left(_), _) => err
    }
}
