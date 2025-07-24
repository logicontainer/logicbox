package logicbox.proof

import logicbox.framework.ProofChecker
import logicbox.rule.FreshVarBoxInfo
import logicbox.framework.Proof
import logicbox.framework.Error.FreshVarEscaped
import logicbox.framework.Error
import logicbox.framework.Location.Step

class FreshVariableEscapeChecker[F, V](
  vOccursInF: (V, F) => Boolean
) extends ProofChecker[F, Any, FreshVarBoxInfo[V], String] {
  private def checkSteps(proof: Proof[F, Any, FreshVarBoxInfo[V], String], steps: Seq[String], blacklist: Set[(String, V)]): List[(String, Error)] =
    steps.zip(steps.map(proof.getStep(_))).flatMap {
      case (id, Some(Proof.Line(formula, _, _))) =>
        blacklist
          .filter { (_, vr) => vOccursInF(vr, formula) }
          .map { (boxId, _) => (id, FreshVarEscaped(boxId)) }

      case (_, Some(Proof.Box(FreshVarBoxInfo(optFreshVar), steps))) =>
        val newBlacklist = blacklist.filter((_, vr) => optFreshVar != Some(vr))
        checkSteps(proof, steps, newBlacklist)

      case _ => Nil
    }.toList

  private def initialBlacklist(proof: Proof[F, Any, FreshVarBoxInfo[V], String]): Set[(String, V)] = {
    def visit(steps: Seq[String]): Set[(String, V)] = {
      steps.zip(steps.map(proof.getStep(_))).flatMap {
        case (id, Some(Proof.Box(FreshVarBoxInfo(Some(freshVar)), steps))) => 
          visit(steps) ++ Set((id, freshVar))
        
        case (_, Some(Proof.Box(_, steps))) => visit(steps)

        case _ => Nil
      }.toSet
    }

    visit(proof.rootSteps)
  }

  override def check(proof: Proof[F, Any, FreshVarBoxInfo[V], String]): List[(String, Error)] = {
    checkSteps(proof, proof.rootSteps, initialBlacklist(proof))
  }
}
