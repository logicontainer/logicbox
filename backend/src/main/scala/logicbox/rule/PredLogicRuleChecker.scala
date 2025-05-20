package logicbox.rule

import logicbox.framework.RuleChecker
import logicbox.formula.PredLogicFormula
import logicbox.framework.Reference
import logicbox.framework.Violation
import logicbox.formula.QuantifierFormula

import logicbox.rule.ReferenceUtil._
import QuantifierFormula._
import logicbox.rule.PredLogicRule._
import logicbox.framework.Violation._
import logicbox.framework.Reference.Line
import logicbox.framework.Reference.Box

class PredLogicRuleChecker[F <: QuantifierFormula[F, T, V], T, V <: T](
  substitutor: Substitutor[F, T, V]
) extends RuleChecker[F, PredLogicRule, PredLogicBoxInfo[V]] {
  private type R = PredLogicRule
  private type B = PredLogicBoxInfo[V]

  private def fail(v: => Violation, vs: => Violation*): List[Violation] = (v +: vs).toList
  private def failIf(b: Boolean, v: => Violation, vs: => Violation*): List[Violation] = {
    if !b then Nil else (v +: vs).toList
  }

  override def check(rule: R, formula: F, refs: List[Reference[F, B]]): List[Violation] = rule match {
    case ForAllElim() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => ref match {
        case ForAll(x, phi) => 
          failIf(
            substitutor.findReplacement(phi, formula, x).isEmpty, 
            FormulaDoesntMatchReference(0, "resulting formula must be the result of substituting ")
          )

        case _ => 
          fail(ReferenceDoesntMatchRule(0, "must be forall"))
      }
    }

    case ForAllIntro() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(Reference.Box(info, _, lst)) => info.freshVar match {
        case None => 
          fail(ReferenceDoesntMatchRule(0, "box does not contain fresh variable"))

        case Some(x0) => formula match {
          case ForAll(x, phi) =>
            failIf(
              lst != substitutor.substitute(phi, x0, x),
              FormulaDoesntMatchReference(0, "last line of box must match formula")
            )

          case _ => fail(FormulaDoesntMatchRule("must be forall"))
        }
      }
    }

    case ExistsElim() => extractAndThen(refs, List(BoxOrFormula.Formula, BoxOrFormula.Box)) {
      case List(Line(Exists(x, phi)), Box(info, ass, concl)) => info.freshVar match {
        case Some(x0) =>
          failIf(
            ass != substitutor.substitute(phi, x0, x),
            ReferencesMismatch(List(0, 1), "assumption of box should match formula within exists-quantifier")
          ) ++ 
          failIf(
            formula != concl, 
            FormulaDoesntMatchReference(1, "conclusion of box should match formula")
          ) ++
          failIf(
            substitutor.hasFreeOccurance(formula, x0),
            FormulaDoesntMatchRule(s"the formula contains a free ocurrance of $x0")
          )

        case None => 
          fail(ReferenceDoesntMatchRule(1, "box does not contain fresh variable"))
      }
      case _ => ???
    }
  }
}
