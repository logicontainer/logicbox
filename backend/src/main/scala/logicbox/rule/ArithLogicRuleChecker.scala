package logicbox.rule

import logicbox.framework.RuleChecker
import logicbox.framework.Reference
import logicbox.framework.RuleViolation
import logicbox.formula.ArithmeticTerm
import logicbox.formula.ConnectiveFormula
import logicbox.formula.QuantifierFormula

class ArithLogicRuleChecker[
  F <: ConnectiveFormula[F] & QuantifierFormula[F, T, V], 
  T <: ArithmeticTerm[T], 
  V <: T
] extends RuleChecker[F, ArithLogicRule, Any] {
  override def check(rule: ArithLogicRule, formula: F, refs: List[Reference[F, Any]]): List[RuleViolation] = ???
}
