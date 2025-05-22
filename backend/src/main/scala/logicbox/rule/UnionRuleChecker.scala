package logicbox.rule

import logicbox.framework._

class UnionRuleChecker[F, R1, R2, B](
  val c1: RuleChecker[F, R1, B],
  val c2: RuleChecker[F, R2, B],
  val isR1: (R1 | R2) => Boolean,
) extends RuleChecker[F, R1 | R2, B] {
  override def check(rule: R1 | R2, formula: F, refs: List[Reference[F, B]]): List[RuleViolation] = rule match {
    case r: R1 if isR1(r) => c1.check(r, formula, refs)
    case r: R2 => c2.check(r, formula, refs)
  }
}
