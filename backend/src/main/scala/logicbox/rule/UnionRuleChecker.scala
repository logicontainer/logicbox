package logicbox.rule

import logicbox.framework._

class UnionRuleChecker[F, R1, R2, B](
  val c1: RuleChecker[F, R1, B],
  val c2: RuleChecker[F, R2, B],
  val isR1: (R1 | R2) => Boolean,
) extends RuleChecker[F, R1 | R2, B] {
  override def check(rule: R1 | R2, formula: F, refs: List[Reference[F, B]]): List[Error] = rule match {
    case r: R1 @unchecked if isR1(r) => c1.check(r, formula, refs)
    case r: R2 @unchecked => c2.check(r, formula, refs)
    case _ => Nil
  }
}

class Union3RuleChecker[F, R1, R2, R3, B](
  val c1: RuleChecker[F, R1, B],
  val c2: RuleChecker[F, R2, B],
  val c3: RuleChecker[F, R3, B],
  val which: (R1 | R2 | R3) => Int,
) extends RuleChecker[F, R1 | R2, B] {
  override def check(rule: R1 | R2, formula: F, refs: List[Reference[F, B]]): List[Error] = rule match {
    case r: R1 @unchecked if which(r) == 1 => c1.check(r, formula, refs)
    case r: R2 @unchecked if which(r) == 2 => c2.check(r, formula, refs)
    case r: R3 @unchecked if which(r) == 3 => c3.check(r, formula, refs)
    case r => throw RuntimeException(s"Which returned ${which(r)}")
  }
}
