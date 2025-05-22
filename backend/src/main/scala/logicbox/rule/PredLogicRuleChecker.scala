package logicbox.rule

import logicbox.framework.RuleChecker
import logicbox.formula.PredLogicFormula
import logicbox.framework.Reference
import logicbox.framework.RuleViolation
import logicbox.formula.QuantifierFormula

import logicbox.rule.ReferenceUtil._
import QuantifierFormula._
import logicbox.rule.PredLogicRule._
import logicbox.framework.RuleViolation._
import logicbox.framework.Reference.Line
import logicbox.framework.Reference.Box

class PredLogicRuleChecker[F <: QuantifierFormula[F, T, V], T, V <: T](
  substitutor: Substitutor[F, T, V]
) extends RuleChecker[F, PredLogicRule, PredLogicBoxInfo[V]] {
  private type R = PredLogicRule
  private type B = PredLogicBoxInfo[V]

  private def fail(v: => RuleViolation, vs: => RuleViolation*): List[RuleViolation] = (v +: vs).toList
  private def failIf(b: Boolean, v: => RuleViolation, vs: => RuleViolation*): List[RuleViolation] = {
    if !b then Nil else (v +: vs).toList
  }

  override def check(rule: R, formula: F, refs: List[Reference[F, B]]): List[RuleViolation] = rule match {
    case ForAllElim() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => ref match {
        case ForAll(x, phi) => 
          failIf(
            substitutor.findReplacement(phi, formula, x).isEmpty, 
            FormulaDoesntMatchReference(0, "resulting formula must match the contents of forall")
          )

        case _ => 
          fail(ReferenceDoesntMatchRule(0, "must be forall"))
      }
    }

    case ForAllIntro() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(b: Box[F, B]) => b.info.freshVar match {
        case None => 
          fail(ReferenceDoesntMatchRule(0, "box does not contain fresh variable"))

        case Some(x0) => 
          val lst = extractLastLine(b)
          formula match {
            case ForAll(x, phi) =>
              failIf(
                lst != Some(substitutor.substitute(phi, x0, x)),
                FormulaDoesntMatchReference(0, "last line of box must match formula")
              )

            case _ => 
              fail(FormulaDoesntMatchRule("must be forall"))
          }
      }
    }

    case ExistsElim() => extractAndThen(refs, List(BoxOrFormula.Formula, BoxOrFormula.Box)) {
      case List(Line(Exists(x, phi)), b: Box[F, B]) => b.info.freshVar match {
        case Some(x0) =>
          val (ass, concl) = (extractFirstLine(b), extractLastLine(b))
          failIf(
            ass != Some(substitutor.substitute(phi, x0, x)),
            ReferencesMismatch(List(0, 1), "assumption of box should match formula within exists-quantifier")
          ) ++ 
          failIf(
            concl != Some(formula), 
            FormulaDoesntMatchReference(1, "conclusion of box should match formula")
          ) ++
          failIf(
            substitutor.hasFreeOccurance(formula, x0),
            FormulaDoesntMatchRule(s"the formula contains a free ocurrance of $x0")
          )

        case None => 
          fail(ReferenceDoesntMatchRule(1, "box does not contain fresh variable"))
      }
      case _ => 
        fail(ReferenceDoesntMatchRule(0, "must be exists"))
    }
    
    case ExistsIntro() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => formula match {
        case Exists(x, phi) => 
          failIf(
            substitutor.findReplacement(phi, ref, x).isEmpty,
            FormulaDoesntMatchReference(0, "reference must be equal to formula, with variable replaced by a term")
          )

        case _ => 
          fail(FormulaDoesntMatchRule("must be exists"))
      }
    }

    case EqualityIntro() => extractNFormulasAndThen(refs, 0) {
      case _ => formula match {
        case Equals(t1, t2) => 
          failIf(t1 != t2, FormulaDoesntMatchRule("sides of equality must be the same"))

        case _ => 
          fail(FormulaDoesntMatchRule("must be equals"))
      }
    }

    case EqualityElim() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => r0 match {
        case Equals(t1, t2) => 
          failIf(
            !substitutor.equalExcept(r1, formula, t1, t2),
            FormulaDoesntMatchReference(1, "Invalid substitution")
          )

        case _ => 
          fail(ReferenceDoesntMatchRule(0, "must be equality"))
      }
    }
  }
}
