package logicbox.server

import logicbox.framework.{Proof, Error}
import logicbox.server.format.OutputError
import logicbox.framework.Error._
import logicbox.framework.RulePosition.Conclusion
import logicbox.framework.RulePosition.Premise
import logicbox.rule.RulePart
import logicbox.framework.RulePosition
import logicbox.framework.Navigator
import logicbox.server.format.OutputError.AmbiguityEntry
import logicbox.framework.Location

class ErrorConverterImpl[F, R, B](
  getRulePart: (R, RulePosition) => Option[RulePart],
  formulaToString: F => String,
  rulePartToString: RulePart => String,
  proofNavigator: Navigator[(Proof[F, R, B, String], String), F],
  rulePartNavigator: Navigator[RulePart, RulePart],
) extends ErrorConverter[F, R, B] {
  private def getIdOfStepAtRulePos(
    proof: Proof[F, R, B, String], originId: String, rulePos: RulePosition
  ): Option[String] = rulePos match {
    case Conclusion => Some(originId)
    case Premise(idx) => proof.getStep(originId) match {
      case (Right(Proof.Line(_, _, refIds))) => refIds.lift(idx)
      case _ => None
    }
  }

  private def rulePosToStr(rulePos: RulePosition): String = rulePos match {
    case Conclusion => "conclusion"
    case Premise(idx) => s"premise $idx"
  }

  override def convert(proof: Proof[F, R, B, String], stepId: String, error: Error): Option[OutputError] = error match {
    case ShapeMismatch(rulePos, loc) => for {
      _ <- Some(())
      rule <- proof.getStep(stepId).toOption.collect { case Proof.Line(_, rule, _) => rule }
      rulePart <- getRulePart(rule, rulePos).flatMap(rulePartNavigator.get(_, loc))
      actualPartId <- getIdOfStepAtRulePos(proof, stepId, rulePos)
      actualPart <- proofNavigator.get((proof, actualPartId), loc)

      rulePosStr = rulePosToStr(rulePos)
      expectedStr = rulePartToString(rulePart)
      actualStr = formulaToString(actualPart)
    } yield OutputError.ShapeMismatch(stepId, rulePosStr, expectedStr, actualStr)

    case Ambiguous(what, ls) => for {
      rule <- proof.getStep(stepId).toOption.collect { case Proof.Line(_, rule, _) => rule }
      entries <- ls.foldRight(Some(Nil): Option[List[AmbiguityEntry]]) {
        case ((rulePos, loc), Some(es)) => for {
          rulePart <- getRulePart(rule, rulePos).flatMap(rulePartNavigator.get(_, loc))
          actualPartId <- getIdOfStepAtRulePos(proof, stepId, rulePos)
          actualPart <- proofNavigator.get((proof, actualPartId), loc)
        } yield OutputError.AmbiguityEntry(
          rulePosition = rulePosToStr(rulePos),
          meta = rulePartToString(rulePart),
          actual = formulaToString(actualPart)
        ) :: es

        case _ => None
      }
    } yield OutputError.Ambiguous(
      uuid = stepId,
      subject = rulePartToString(what),
      entries = entries
    )

    case _ => ???
  }
}
