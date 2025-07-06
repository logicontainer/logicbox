package logicbox.server

import logicbox.framework.{Proof, Error}
import logicbox.server.format.OutputError
import logicbox.framework.Error._
import logicbox.framework.RulePosition.Conclusion
import logicbox.framework.RulePosition.Premise
import logicbox.rule.RulePart
import logicbox.framework.RulePosition

class ErrorConverterImpl[F, R, B](
  getRulePart: (R, RulePosition) => Option[RulePart],
  formulaToString: F => String,
  rulePartToString: RulePart => String
) extends ErrorConverter[F, R, B] {
  override def convert(proof: Proof[F, R, B, String], stepId: String, error: Error): OutputError = error match {
    case ShapeMismatch(rulePos, loc) => rulePos match {
      case Conclusion => proof.getStep(stepId) match {
        case Right(Proof.Line(formula, rule, _)) => OutputError.ShapeMismatch(
          uuid = stepId,
          rulePosition = "conclusion",
          expected = getRulePart(rule, rulePos).map(rulePartToString).getOrElse(???),
          actual = formulaToString(formula)
        )
        case _ => ???
      }

      case Premise(idx) => ???
    }
    
    case _ => ???
  }
}
