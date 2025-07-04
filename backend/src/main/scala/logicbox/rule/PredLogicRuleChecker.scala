package logicbox.rule

import logicbox.framework.RuleChecker
import logicbox.formula.PredLogicFormula
import logicbox.framework.Reference
import logicbox.framework.Error
import logicbox.formula.QuantifierFormula

import logicbox.rule.ReferenceUtil._
import QuantifierFormula._
import logicbox.rule.PredLogicRule._
import logicbox.framework.Reference.Line
import logicbox.framework.Reference.Box
import logicbox.framework.Error.ShapeMismatch
import logicbox.framework.RulePosition.Premise
import logicbox.rule.RulePart.MetaFormula
import logicbox.rule.RulePart.MetaVariable
import logicbox.framework.Error.Ambiguous
import logicbox.framework.RulePosition.Conclusion
import logicbox.framework.Location
import logicbox.framework.Error.ReferenceBoxMissingFreshVar
import logicbox.framework.Error.Miscellaneous
import logicbox.rule.RulePart.MetaTerm

class PredLogicRuleChecker[F <: QuantifierFormula[F, T, V], T, V <: T](
  substitutor: Substitutor[F, T, V]
) extends RuleChecker[F, PredLogicRule, FreshVarBoxInfo[V]] {
  private type R = PredLogicRule
  private type B = FreshVarBoxInfo[V]

  private def fail(v: => Error, vs: => Error*): List[Error] = (v +: vs).toList
  private def failIf(b: Boolean, v: => Error, vs: => Error*): List[Error] = {
    if !b then Nil else (v +: vs).toList
  }

  override def check(rule: R, formula: F, refs: List[Reference[F, B]]): List[Error] = rule match {
    case ForAllElim() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => ref match {
        case ForAll(x, phi) => 
          failIf(
            substitutor.findReplacement(phi, formula, x).isEmpty, 
            Ambiguous(MetaFormula(0), List(
              (Conclusion, Location.root),
              (Premise(0), Location.formulaInsideQuantifier)
            ))
          )

        case _ => 
          fail(ShapeMismatch(Premise(0), RulePart.ForAll(MetaVariable(0), MetaFormula(0))))
      }
    }

    case ForAllIntro() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(b: Box[F, B]) => b.info.freshVar match {
        case None => 
          fail(ReferenceBoxMissingFreshVar(0))

        case Some(x0) => 
          val lst = extractLastLine(b)
          formula match {
            case ForAll(x, phi) =>
              failIf(
                lst != Some(substitutor.substitute(phi, x0, x)),
                Ambiguous(MetaFormula(0), List(
                  (Conclusion, Location.formulaInsideQuantifier),
                  (Premise(0), Location.conclusion)
                ))
              )

            case _ => 
              fail(ShapeMismatch(Conclusion, RulePart.ForAll(MetaVariable(0), MetaFormula(0))))
          }
      }
    }

    case ExistsElim() => extractAndThen(refs, List(BoxOrFormula.Formula, BoxOrFormula.Box)) {
      case List(Line(Exists(x, phi)), b: Box[F, B]) => b.info.freshVar match {
        case Some(x0) =>
          val (ass, concl) = (extractFirstLine(b), extractLastLine(b))
          failIf(
            ass != Some(substitutor.substitute(phi, x0, x)),
            Ambiguous(MetaFormula(0), List(
              (Premise(0), Location.formulaInsideQuantifier),
              (Premise(1), Location.assumption)
            ))
          ) ++ 
          failIf(
            concl != Some(formula), 
            Ambiguous(MetaFormula(1), List(
              (Conclusion, Location.root),
              (Premise(1), Location.conclusion)
            ))
          ) ++
          failIf(
            substitutor.hasFreeOccurance(formula, x0),
            Miscellaneous(Conclusion, "fresh variable must not occur in conclusion")
          )

        case None => 
          fail(ReferenceBoxMissingFreshVar(1))
      }
      case _ => 
        fail(ShapeMismatch(Premise(0), RulePart.Exists(MetaVariable(0), MetaFormula(0))))
    }

    case ExistsIntro() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => formula match {
        case Exists(x, phi) => 
          failIf(
            substitutor.findReplacement(phi, ref, x).isEmpty,
            Ambiguous(MetaFormula(0), List(
              (Conclusion, Location.formulaInsideQuantifier),
              (Premise(0), Location.root)
            ))
          )

        case _ => 
          fail(ShapeMismatch(Conclusion, RulePart.Exists(MetaVariable(0), MetaFormula(0))))
      }
    }

    case EqualityIntro() => extractNFormulasAndThen(refs, 0) {
      case _ => formula match {
        case Equals(t1, t2) => 
          failIf(t1 != t2, Ambiguous(MetaTerm(0), List(
            (Conclusion, Location.lhs),
            (Conclusion, Location.rhs)
          )))

        case _ => 
          fail(ShapeMismatch(Conclusion, RulePart.Equals(MetaTerm(0), MetaTerm(1))))
      }
    }

    case EqualityElim() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => r0 match {
        case Equals(t1, t2) => 
          failIf(
            !substitutor.equalExcept(r1, formula, t1, t2),
            Ambiguous(MetaFormula(0), List(
              (Conclusion, Location.root),
              (Premise(1), Location.root)
            ))
          )

        case _ => 
          fail(ShapeMismatch(Premise(0), RulePart.Equals(MetaTerm(0), MetaTerm(1))))
      }
    }
  }
}
