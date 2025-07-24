package logicbox.proof

import logicbox.framework.ProofChecker
import logicbox.framework.Proof
import logicbox.framework.Error
import logicbox.framework.Error.PremiseInsideBox
import logicbox.framework.Error.InvalidAssumption

class StructuralProofChecker[R, Id](
  premiseRule: R,
  assumptionRule: R
) extends ProofChecker[Any, R, Any, Id] {
  private def checkBoxSteps(proof: Proof[Any, R, Any, Id], steps: Seq[Id]): List[(Id, Error)] = {
    steps.map(id => (id, proof.getStep(id))).zipWithIndex.flatMap {
      case ((id, Some(Proof.Line(_, rule, _))), _) if rule == premiseRule => 
        List((id, PremiseInsideBox()))

      case ((id, Some(Proof.Line(_, rule, _))), idx) if rule == assumptionRule && idx != 0 =>
        List((id, InvalidAssumption()))

      case ((_, Some(Proof.Box(_, steps))), _) => 
        checkBoxSteps(proof, steps)

      case _ => Nil
    }.toList
  }

  override def check(proof: Proof[Any, R, Any, Id]): List[(Id, Error)] = {
    proof.rootSteps.zip(proof.rootSteps.map(proof.getStep(_))).flatMap {
      case (_, Some(Proof.Box(_, steps))) => checkBoxSteps(proof, steps)
      case (id, Some(Proof.Line(_, rule, _))) if rule == assumptionRule =>
        List((id, InvalidAssumption()))
      case _ => Nil
    }.toList
  }
}
