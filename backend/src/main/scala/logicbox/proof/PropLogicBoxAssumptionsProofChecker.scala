package logicbox.proof

import logicbox.formula.ConnectiveFormula
import logicbox.framework.{Proof, ProofChecker, Diagnostic}
import logicbox.rule.PropLogicRule
import logicbox.rule.PropLogicRule._

import logicbox.framework.Diagnostic.RuleViolationAtStep
import logicbox.framework.RuleViolation.ReferenceDoesntMatchRule
import ProofCheckUtil.{checkRefHasAssumptionOnFirstLine, checkForEveryLine}

class PropLogicBoxAssumptionsProofChecker[R >: PropLogicRule, Id] 
  extends ProofChecker[Any, R, Any, Id]
{
  private type Pf = Proof[Any, R, Any, Id]

  override def check(proof: Pf): List[Diagnostic[Id]] = checkForEveryLine(proof, {
    case (id, line) => line.rule match {
      case ImplicationIntro() | NotIntro() | ProofByContradiction() => 
        checkRefHasAssumptionOnFirstLine(proof, id, line, 0, Assumption())

      case OrElim() => 
        checkRefHasAssumptionOnFirstLine(proof, id, line, 1, Assumption()) ++
        checkRefHasAssumptionOnFirstLine(proof, id, line, 2, Assumption())

      case _ => Nil
    }
  })
}
