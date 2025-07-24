package logicbox.proof

import logicbox.framework.ProofChecker
import logicbox.framework.Proof
import logicbox.framework.Error
import logicbox.framework.Error.PremiseInsideBox

class StructuralProofChecker[R, Id](
  premiseRule: R
) extends ProofChecker[Any, R, Any, Id] {
  private def checkSteps(proof: Proof[Any, R, Any, Id], steps: Seq[Id]): List[(Id, Error)] = {
    steps.map(id => (id, proof.getStep(id))).flatMap {
      case (id, Right(Proof.Line(_, rule, _))) if rule == premiseRule => 
        List((id, PremiseInsideBox()))

      case (id, Right(Proof.Box(_, steps))) => 
        checkSteps(proof, steps)

      case _ => Nil
    }.toList
  }

  override def check(proof: Proof[Any, R, Any, Id]): List[(Id, Error)] = {
    proof.rootSteps.map(proof.getStep(_)).flatMap {
      case Right(Proof.Box(_, steps)) => checkSteps(proof, steps)
      case _ => Nil
    }.toList
  }
}
