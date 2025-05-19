package logicbox.rule

import logicbox.framework.RuleChecker
import logicbox.formula.PredLogicFormula
import logicbox.framework.Reference
import logicbox.framework.Violation
import logicbox.rule.PredLogicRule.ForAllElim
import logicbox.formula.QuantifierFormula

import logicbox.rule.ReferenceUtil._
import QuantifierFormula._
import logicbox.rule.PredLogicRule.ForAllIntro
import logicbox.framework.Violation._

class PredLogicRuleChecker[F <: QuantifierFormula[F, T, V], T, V <: T] extends RuleChecker[F, PredLogicRule, PredLogicBoxInfo] {
  private type R = PredLogicRule
  private type B = PredLogicBoxInfo

  private def fail(v: => Violation, vs: => Violation*): List[Violation] = (v +: vs).toList
  private def failIf(b: Boolean, v: => Violation, vs: => Violation*): List[Violation] = {
    if !b then Nil else (v +: vs).toList
  }

  override def check(rule: R, formula: F, refs: List[Reference[F, B]]): List[Violation] = rule match {
    case ForAllElim() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => ref match {
        case ForAll(x, phi) => 
          // TODO: fake
          failIf(phi != formula, FormulaDoesntMatchReference(0, "must be equal to reference"))

        case _ => 
          fail(ReferenceDoesntMatchRule(0, "must be forall"))
      }
    }
    case ForAllIntro() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(Reference.Box(info, _, _)) => 
        failIf(info.freshVar.isEmpty, ReferenceDoesntMatchRule(0, "box does not contain fresh variable")) ++ {
          formula match {
            case ForAll(_, _) => Nil // TODO: fake
            case _ => fail(FormulaDoesntMatchRule("must be forall"))
          }
        }
    }
  }
}
