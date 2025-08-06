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
import logicbox.framework.RulePart.MetaFormula
import logicbox.framework.RulePart.MetaVariable
import logicbox.framework.Error.Ambiguous
import logicbox.framework.Location
import logicbox.framework.Error.ReferenceBoxMissingFreshVar
import logicbox.framework.Error.Miscellaneous
import logicbox.framework.RulePart.MetaTerm
import logicbox.framework.RulePart.Formulas
import logicbox.framework.RulePart.Vars
import logicbox.framework.RulePart.Terms

class PredLogicRuleChecker[F, T, V <: T](
  substitutor: Substitutor[F, T, V]
)(using QuantifierFormula[F, T, V])
extends RuleChecker[F, PredLogicRule, FreshVarBoxInfo[V]] {
  private type R = PredLogicRule
  private type B = FreshVarBoxInfo[V]

  private def fail(v: => Error, vs: => Error*): List[Error] = (v +: vs).toList
  private def failIf(b: Boolean, v: => Error, vs: => Error*): List[Error] = {
    if !b then Nil else (v +: vs).toList
  }

  override def check(rule: R, formula: F, refs: List[Reference[F, B]]): List[Error] = rule match {
    case ForAllElim() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => ref match {
        case ∀(x, phi) => 
          failIf(
            substitutor.findReplacement(phi, formula, x).isEmpty, 
            Ambiguous(MetaFormula(Formulas.Phi), List(
              Location.conclusion.root,
              Location.premise(0).formulaInsideQuantifier
            ))
          )

        case _ => 
          fail(ShapeMismatch(Location.premise(0)))
      }
    }

    case ForAllIntro() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(b: Box[F, B]) => b.info.freshVar match {
        case None => 
          fail(ReferenceBoxMissingFreshVar(0))

        case Some(x0) => 
          val lst = extractLastLine(b)
          formula match {
            case ∀(x, phi) =>
              failIf(
                lst != Some(substitutor.substitute(phi, x0, x)),
                Ambiguous(MetaFormula(Formulas.Phi), List(
                  Location.conclusion.formulaInsideQuantifier,
                  Location.premise(0).lastLine
                ))
              )

            case _ => 
              fail(ShapeMismatch(Location.conclusion))
          }
      }
    }

    case ExistsElim() => extractAndThen(refs, List(BoxOrFormula.Formula, BoxOrFormula.Box)) {
      case List(Line(∃(x, phi)), b: Box[F, B]) => b.info.freshVar match {
        case Some(x0) =>
          val (ass, concl) = (extractFirstLine(b), extractLastLine(b))
          failIf(
            ass != Some(substitutor.substitute(phi, x0, x)),
            Ambiguous(MetaFormula(Formulas.Phi), List(
              Location.premise(0).formulaInsideQuantifier,
              Location.premise(1).firstLine
            ))
          ) ++ 
          failIf(
            concl != Some(formula), 
            Ambiguous(MetaFormula(Formulas.Chi), List(
              Location.conclusion.root,
              Location.premise(1).lastLine
            ))
          ) ++
          failIf(
            substitutor.hasFreeOccurance(formula, x0),
            Miscellaneous(Location.conclusion, "fresh variable must not occur in conclusion")
          )

        case None => 
          fail(ReferenceBoxMissingFreshVar(1))
      }
      case _ => 
        fail(ShapeMismatch(Location.premise(0)))
    }

    case ExistsIntro() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => formula match {
        case ∃(x, phi) => 
          failIf(
            substitutor.findReplacement(phi, ref, x).isEmpty,
            Ambiguous(MetaFormula(Formulas.Phi), List(
              Location.conclusion.formulaInsideQuantifier,
              Location.premise(0).root
            ))
          )

        case _ => 
          fail(ShapeMismatch(Location.conclusion))
      }
    }

    case EqualityIntro() => extractNFormulasAndThen(refs, 0) {
      case _ => formula match {
        case t1 === t2 => 
          failIf(t1 != t2, Ambiguous(MetaTerm(Terms.T), List(
            Location.conclusion.lhs,
            Location.conclusion.rhs
          )))

        case _ => 
          fail(ShapeMismatch(Location.conclusion))
      }
    }

    case EqualityElim() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => r0 match {
        case t1 === t2 => 
          failIf(
            !substitutor.equalExcept(r1, formula, t1, t2),
            Ambiguous(MetaFormula(Formulas.Phi), List(
              Location.conclusion.root,
              Location.premise(1).root
            ))
          )

        case _ => 
          fail(ShapeMismatch(Location.premise(0)))
      }
    }
  }
}
