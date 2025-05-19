package logicbox.rule

import logicbox.framework.RuleChecker
import logicbox.formula.PredLogicFormula
import logicbox.framework.Reference
import logicbox.framework.Violation
import logicbox.rule.PredLogicRule.ForAllElim
import logicbox.formula.QuantifierFormula

import logicbox.rule.ReferenceUtil._

class PredLogicRuleChecker[F <: QuantifierFormula[F, T, V], T, V <: T] extends RuleChecker[F, PredLogicRule, PredLogicBoxInfo] {
  private type R = PredLogicRule
  private type B = PredLogicBoxInfo

  override def check(rule: R, formula: F, refs: List[Reference[F, B]]): List[Violation] = rule match {
    case ForAllElim() => extractAndThen(refs, List(BoxOrFormula.Formula)) {
      case List(ref) => ???
    }
  }
}
