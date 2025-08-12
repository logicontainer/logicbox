package logicbox.proof

import logicbox.framework.ProofChecker
import logicbox.rule.FreshVarBoxInfo
import logicbox.framework.Proof
import logicbox.framework.Proof.Box
import logicbox.framework.Error
import logicbox.framework.Error.RedefinitionOfFreshVar

class FreshVariableDuplicateChecker[V] extends ProofChecker[Any, Any, FreshVarBoxInfo[V], String] {
  override def check(proof: Proof[Any, Any, FreshVarBoxInfo[V], String]): List[(String, Error)] = {
    def visitSteps(steps: Seq[String], seenVars: Map[V, String]): List[(String, Error)] = {
      steps.toList.flatMap(visit(_, seenVars))
    }

    def visit(id: String, seenVars: Map[V, String]): List[(String, Error)] = proof.getStep(id) match {
      case Some(Proof.Box(FreshVarBoxInfo(Some(freshVar)), steps)) => 
        if seenVars contains freshVar then 
          (id, RedefinitionOfFreshVar(seenVars(freshVar))) :: visitSteps(steps, seenVars)
        else 
          visitSteps(steps, seenVars + (freshVar -> id))

      case Some(Proof.Box(FreshVarBoxInfo(None), steps)) => 
        visitSteps(steps, seenVars)

      case _ => Nil
    }

    visitSteps(proof.rootSteps, Map())
  }
}
