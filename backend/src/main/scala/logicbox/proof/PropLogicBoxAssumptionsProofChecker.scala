package logicbox.proof

import logicbox.formula.ConnectiveFormula
import logicbox.framework.{Proof, ProofChecker, Diagnostic}
import logicbox.rule.PropLogicRule
import logicbox.rule.PropLogicRule._

import logicbox.framework.Diagnostic.RuleViolationAtStep
import logicbox.framework.RuleViolation.ReferenceDoesntMatchRule

class PropLogicBoxAssumptionsProofChecker[Id] 
  extends ProofChecker[Any, PropLogicRule, Any, Id]
{
  private type Pf = Proof[Any, PropLogicRule, Any, Id]

  private def getFirstLine(proof: Pf, box: Proof.Box[Any, Id]): Option[Proof.Line[Any, PropLogicRule, Id]] = box match {
    case Proof.Box(_, lines) => for {
      firstStepId <- lines.headOption
      firstStep <- proof.getStep(firstStepId).toOption
      line <- firstStep match {
        case l: Proof.Line[Any, PropLogicRule, Id] => Some(l)
        case _ => None
      }
    } yield line
  }

  private def checkRefHasAssumptionOnFirstLine(proof: Pf, stepId: Id, line: Proof.Line[Any, PropLogicRule, Id], refIdx: Int): List[Diagnostic[Id]] = {
    val firstRule = line.refs.drop(refIdx).headOption.flatMap {
      case boxRefId => proof.getStep(boxRefId).toOption
    }.collect { 
      case box: Proof.Box[Any, Id] => getFirstLine(proof, box)
    }.flatten.map(_.rule)

    firstRule match {
      case Some(rule) if rule != Assumption() => List(
        RuleViolationAtStep(stepId,
          ReferenceDoesntMatchRule(refIdx, "first line in box must be assumption")
        )
      )
      case _ => Nil
    }
  }

  private def checkLine(proof: Pf, stepId: Id, line: Proof.Line[Any, PropLogicRule, Id]): List[Diagnostic[Id]] = line.rule match {
    case ImplicationIntro() | NotIntro() | ProofByContradiction() => 
      checkRefHasAssumptionOnFirstLine(proof, stepId, line, 0)

    case OrElim() => 
      checkRefHasAssumptionOnFirstLine(proof, stepId, line, 1) ++
      checkRefHasAssumptionOnFirstLine(proof, stepId, line, 2)

    case _ => Nil
  }

  private def checkSteps(proof: Pf, steps: Seq[Id]): List[Diagnostic[Id]] = for {
    stepId <- steps.toList
    diag <- proof.getStep(stepId) match {
      case Right(l: Proof.Line[Any, PropLogicRule, Id]) => checkLine(proof, stepId, l)
      case Right(b: Proof.Box[Any, Id]) => checkSteps(proof, b.steps)
      case _ => Nil
    }
  } yield diag

  override def check(proof: Pf): List[Diagnostic[Id]] = checkSteps(proof, proof.rootSteps)
}
