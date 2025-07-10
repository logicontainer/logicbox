package logicbox.server

import logicbox.framework._
import logicbox.server.format.OutputError
import logicbox.framework.Error._
import logicbox.framework.RulePosition._
import logicbox.server.format.OutputError.AmbiguityEntry
import logicbox.framework.Location.Step
import logicbox.framework.Proof.Line
import logicbox.framework.Proof.Box

class ErrorConverterImpl[F, R, B, O](
  proofNavigator: Navigator[(Proof[F, R, B, String], String), O],
  infRuleNavigator: Navigator[InfRule, RulePart],
  getInfRule: R => Option[InfRule],
  actualExpToString: O => String,
  rulePartToString: RulePart => String,
  proof: Proof[F, R, B, String],
) extends ErrorConverter {
  private def getRulePosition(firstStep: Location.Step): Option[String] = firstStep match {
    case Step.Premise(idx) => Some(s"premise $idx")
    case Step.Conclusion => Some("conclusion")
    case _ => None
  }

  private def getRuleFromStep(stepId: String): Option[R] = {
    proof.getStep(stepId).toOption.collect {
      case Proof.Line(_, rule, _) => rule
    }
  }

  override def convert(stepId: String, error: Error): Option[OutputError] = error match {
    case ShapeMismatch(loc) => for {
      rulePosition <- loc.steps.headOption.flatMap(getRulePosition(_))
      rule <- getRuleFromStep(stepId) 
      infrule <- getInfRule(rule)
      rulePart <- infRuleNavigator.get(infrule, loc)
      actual <- proofNavigator.get((proof, stepId), loc)
    } yield OutputError.ShapeMismatch(
      uuid = stepId,
      rulePosition = rulePosition,
      expected = rulePartToString(rulePart),
      actual = actualExpToString(actual)
    )

    case Ambiguous(what, entries) => 
      entries.foldRight(Some(Nil): Option[List[AmbiguityEntry]]) { 
        case (loc, Some(es)) => for {
          firstStep <- loc.steps.headOption
          rulePosition <- getRulePosition(firstStep)
          rule <- getRuleFromStep(stepId)
          infRule <- getInfRule(rule)
          rulePart <- infRuleNavigator.get(infRule, loc)
          actual <- proofNavigator.get((proof, stepId), loc)
        } yield OutputError.AmbiguityEntry(
          rulePosition = rulePosition,
          meta = rulePartToString(rulePart),
          actual = actualExpToString(actual)
        ) :: es
        case _ => None
      }.map { es =>
        OutputError.Ambiguous(
          uuid = stepId,
          subject = rulePartToString(what),
          entries = es
        )
      }

    case Miscellaneous(loc, expl) => 
      loc.steps.headOption
        .flatMap(getRulePosition(_))
        .map(OutputError.Miscellaneous(stepId, _, expl))
    
    case MissingFormula() => Some(OutputError.Simple(stepId, "MissingFormula"))
    case MissingRule() => Some(OutputError.Simple(stepId, "MissingRule"))
    case MissingRef(refIdx) => Some(OutputError.RefErr(stepId, "MissingRef", refIdx))

    case ReferenceOutOfScope(refIdx) => Some(OutputError.RefErr(stepId, "ReferenceOutOfScope", refIdx))
    case ReferenceToLaterStep(refIdx) => Some(OutputError.RefErr(stepId, "ReferenceToLaterStep", refIdx))
    case ReferenceToUnclosedBox(refIdx) => Some(OutputError.RefErr(stepId, "ReferenceToUnclosedBox", refIdx))
    case ReferenceBoxMissingFreshVar(refIdx) => Some(OutputError.RefErr(stepId, "ReferenceBoxMissingFreshVar", refIdx))
    case ReferenceShouldBeBox(refIdx) => Some(OutputError.RefErr(stepId, "ReferenceShouldBeBox", refIdx))
    case ReferenceShouldBeLine(refIdx) => Some(OutputError.RefErr(stepId, "ReferenceShouldBeLine", refIdx))

    case WrongNumberOfReferences(exp, actual) => Some(OutputError.WrongNumberOfReferences(stepId, exp, actual))
  }
}
