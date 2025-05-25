package logicbox.rule

import logicbox.framework._
import logicbox.formula._
import logicbox.formula.QuantifierFormula._
import logicbox.formula.ConnectiveFormula.Not
import logicbox.formula.ArithmeticTerm._
import logicbox.rule.ArithLogicRule._
import logicbox.rule.ReferenceUtil.extractNFormulasAndThen
import logicbox.framework.RuleViolation._
import logicbox.rule.ReferenceUtil.BoxOrFormula

class ArithLogicRuleChecker[
  F <: ConnectiveFormula[F] & QuantifierFormula[F, T, V],
  T <: ArithmeticTerm[T], 
  V <: T
] extends RuleChecker[F, ArithLogicRule, Any] {

  private def fail(v: => RuleViolation, vs: => RuleViolation*): List[RuleViolation] = (v +: vs).toList
  private def failIf(b: Boolean, v: => RuleViolation, vs: => RuleViolation*): List[RuleViolation] = {
    if !b then Nil else (v +: vs).toList
  }

  override def check(rule: ArithLogicRule, formula: F, refs: List[Reference[F, Any]]): List[RuleViolation] = rule match {
    case Peano1() => extractNFormulasAndThen(refs, 0) {
      case Nil => formula match {
        case (t1 + t2) ~= t3 => 
          (t2 match {
            case Zero() => Nil

            case _ => 
              fail(FormulaDoesntMatchRule("TODO"))
          }) ++ 
          failIf(
            t1 != t3, 
            FormulaDoesntMatchRule("TODO")
          )

        case _ ~= _ => 
          fail(FormulaDoesntMatchRule("TODO"))

        case _ => 
          fail(FormulaDoesntMatchRule("TODO"))
      }
    }

    case Peano2() => extractNFormulasAndThen(refs, 0) {
      case Nil => formula match {
        case (t1 + (t2 + One())) ~= ((t3 + t4) + One()) => 
          failIf(t1 != t3, FormulaDoesntMatchRule("TODO")) ++
          failIf(t2 != t4, FormulaDoesntMatchRule("TODO"))

        case lhs ~= rhs => 
          failIf(Plus.unapply(lhs).isEmpty, FormulaDoesntMatchRule("TODO")) ++
          failIf(Plus.unapply(rhs).isEmpty, FormulaDoesntMatchRule("TODO"))
          ++ (lhs match {
            case _ + (_ + notone) if !One.unapply(notone) => 
              fail(FormulaDoesntMatchRule("TODO"))

            case _ + t if Plus.unapply(t).isEmpty => 
              fail(FormulaDoesntMatchRule("TODO"))

            case _ => Nil
          }) ++ (rhs match {
            case _ + notone if !One.unapply(notone) =>
              fail(FormulaDoesntMatchRule("TODO"))

            case t + _ if Plus.unapply(t).isEmpty =>
              fail(FormulaDoesntMatchRule("TODO"))

            case _ => Nil
          })

        case _ => 
          fail(FormulaDoesntMatchRule("TODO"))
      }
    }

    case Peano3() => extractNFormulasAndThen(refs, 0) {
      case _ => formula match {
        case (_ ~* Zero()) ~= Zero() => Nil

        case lhs ~= rhs => 
          failIf(Mult.unapply(lhs).isEmpty, FormulaDoesntMatchRule("TODO")) ++
          failIf(!Zero.unapply(rhs), FormulaDoesntMatchRule("TODO")) ++
          (lhs match {
            case _ ~* notzero if !Zero.unapply(notzero) =>
              fail(FormulaDoesntMatchRule("TODO"))
            case _ => Nil
          })

        case _ => 
          fail(FormulaDoesntMatchRule("TODO"))
      }
    }

    case Peano4() => extractNFormulasAndThen(refs, 0) {
      case Nil => formula match {
        case (t1 ~* (t2 + One())) ~= ((t3 ~* t4) + t5) =>
          failIf(t1 != t3, FormulaDoesntMatchRule("TODO")) ++
          failIf(t1 != t5, FormulaDoesntMatchRule("TODO")) ++
          failIf(t2 != t4, FormulaDoesntMatchRule("TODO"))

        case lhs ~= rhs => 
          failIf(Mult.unapply(lhs).isEmpty, FormulaDoesntMatchRule("TODO")) ++
          failIf(Plus.unapply(rhs).isEmpty, FormulaDoesntMatchRule("TODO"))
          ++ (lhs match {
            case _ ~* (_ + notone) if !One.unapply(notone) => 
              fail(FormulaDoesntMatchRule("TODO"))

            case _ ~* t if Plus.unapply(t).isEmpty => 
              fail(FormulaDoesntMatchRule("TODO"))

            case _ => Nil
          }) ++ (rhs match {
            case t + _ if Mult.unapply(t).isEmpty =>
              fail(FormulaDoesntMatchRule("TODO"))

            case _ => Nil
          })

        case _ => 
          fail(FormulaDoesntMatchRule("TODO"))
      }
    }

    case Peano5() => extractNFormulasAndThen(refs, 0) {
      case Nil => formula match {
        case Not(Zero() ~= t + One()) => 
          Nil

        case Not(lhs ~= rhs) => 
          failIf(!Zero.unapply(lhs), FormulaDoesntMatchRule("TODO")) ++
          failIf(Plus.unapply(rhs).isEmpty, FormulaDoesntMatchRule("TODO")) ++
          (rhs match {
            case t1 + notone if !One.unapply(notone) => 
              fail(FormulaDoesntMatchRule("TODO"))
            case _ => Nil
          })

        case _ => 
          fail(FormulaDoesntMatchRule("TODO"))
      }
    }

    case Peano6() => extractNFormulasAndThen(refs, 1) {
      case List(t1 + o1 ~= t2 + o2) => (formula match {
        case t3 ~= t4 =>
          failIf(t3 != t1, FormulaDoesntMatchReference(0, "TODO")) ++
          failIf(t4 != t2, FormulaDoesntMatchReference(0, "TODO"))

        case _ => 
          fail(FormulaDoesntMatchRule("TODO"))
      }) ++
      failIf(!One.unapply(o1), ReferenceDoesntMatchRule(0, "TODO")) ++
      failIf(!One.unapply(o2), ReferenceDoesntMatchRule(0, "TODO"))

      case List(a ~= b) => 
        failIf(Plus.unapply(a).isEmpty, ReferenceDoesntMatchRule(0, "TODO")) ++
        failIf(Plus.unapply(b).isEmpty, ReferenceDoesntMatchRule(0, "TODO"))

      case List(_) => 
        fail(ReferenceDoesntMatchRule(0, "TODO"))
    }

    case _ => ???
  }
}
