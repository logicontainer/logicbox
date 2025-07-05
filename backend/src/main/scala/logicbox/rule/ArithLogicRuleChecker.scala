package logicbox.rule

import logicbox.framework._
import logicbox.formula._
import logicbox.formula.QuantifierFormula._
import logicbox.formula.ConnectiveFormula.Not
import logicbox.formula.ArithmeticTerm._
import logicbox.rule.ArithLogicRule._
import logicbox.rule.ReferenceUtil.extractNFormulasAndThen
import logicbox.rule.ReferenceUtil.BoxOrFormula
import logicbox.rule.ReferenceUtil.extractAndThen
import logicbox.framework.Reference._
import logicbox.rule.ReferenceUtil.extractFirstLine
import logicbox.rule.ReferenceUtil.extractLastLine
import logicbox.framework.Error.ShapeMismatch
import logicbox.framework.RulePosition.Conclusion
import logicbox.rule.RulePart.MetaTerm
import logicbox.framework.Error.Ambiguous
import logicbox.framework.RulePosition.Premise
import logicbox.framework.Error.ReferenceBoxMissingFreshVar
import logicbox.rule.RulePart.MetaVariable
import logicbox.rule.RulePart.MetaFormula
import logicbox.framework.Error.Miscellaneous
import logicbox.rule.RulePart.Terms
import logicbox.rule.RulePart.Vars
import logicbox.rule.RulePart.Formulas

class ArithLogicRuleChecker[
  F <: ConnectiveFormula[F] & QuantifierFormula[F, T, V],
  T <: ArithmeticTerm[T], 
  V <: T
](
  substitutor: Substitutor[F, T, V]
) extends RuleChecker[F, ArithLogicRule, FreshVarBoxInfo[V]] {
  private type BI = FreshVarBoxInfo[V]

  private def fail(v: => Error, vs: => Error*): List[Error] = (v +: vs).toList
  private def failIf(b: Boolean, v: => Error, vs: => Error*): List[Error] = {
    if !b then Nil else (v +: vs).toList
  }

  override def check(rule: ArithLogicRule, formula: F, refs: List[Reference[F, BI]]): List[Error] = rule match {
    case Peano1() => extractNFormulasAndThen(refs, 0) {
      case Nil => formula match {
        case (t1 + Zero()) ~= t2 => 
          failIf(t1 != t2, Ambiguous(MetaTerm(Terms.T), List(
            (Conclusion, Location.lhs.lhs),
            (Conclusion, Location.rhs)
          )))

        case _ => 
          fail(ShapeMismatch(Conclusion, RulePart.Equals(RulePart.Plus(MetaTerm(Terms.T), RulePart.Zero()), MetaTerm(Terms.T))))
      }
    }

    case Peano2() => extractNFormulasAndThen(refs, 0) {
      case Nil => formula match {
        case (t1 + (t2 + One())) ~= ((t3 + t4) + One()) => 
          failIf(t1 != t3, Ambiguous(MetaTerm(Terms.T1), List(
            (Conclusion, Location.lhs.lhs),
            (Conclusion, Location.rhs.lhs.lhs)
          ))) ++
          failIf(t2 != t4, Ambiguous(MetaTerm(Terms.T2), List(
            (Conclusion, Location.lhs.rhs.lhs),
            (Conclusion, Location.rhs.lhs.rhs)
          )))

        case _ => 
          fail(ShapeMismatch(Conclusion, 
            RulePart.Equals(
              RulePart.Plus(MetaTerm(Terms.T1), RulePart.Plus(MetaTerm(Terms.T2), RulePart.One())),
              RulePart.Plus(RulePart.Plus(MetaTerm(Terms.T1), MetaTerm(Terms.T2)), RulePart.One())
            )
          ))
      }
    }

    case Peano3() => extractNFormulasAndThen(refs, 0) {
      case _ => formula match {
        case (_ ~* Zero()) ~= Zero() => Nil

        case _ => 
          fail(ShapeMismatch(Conclusion, RulePart.Equals(
            RulePart.Mult(MetaTerm(Terms.T), RulePart.Zero()),
            RulePart.Zero()
          )))
      }
    }

    case Peano4() => extractNFormulasAndThen(refs, 0) {
      case Nil => formula match {
        case (t1 ~* (t2 + One())) ~= ((t3 ~* t4) + t5) =>
          failIf(Set(t1, t3, t5).size > 1, Ambiguous(MetaTerm(Terms.T1), List(
            (Conclusion, Location.lhs.lhs),
            (Conclusion, Location.rhs.lhs.lhs),
            (Conclusion, Location.rhs.rhs)
          ))) ++
          failIf(t2 != t4, Ambiguous(MetaTerm(Terms.T2), List(
            (Conclusion, Location.lhs.rhs.lhs),
            (Conclusion, Location.rhs.lhs.rhs)
          )))

        case _ => 
          fail(ShapeMismatch(Conclusion, RulePart.Equals(
            RulePart.Mult(MetaTerm(Terms.T1), RulePart.Plus(MetaTerm(Terms.T2), RulePart.One())),
            RulePart.Plus(RulePart.Mult(MetaTerm(Terms.T1), MetaTerm(Terms.T2)), MetaTerm(Terms.T1))
          )))
      }
    }

    case Peano5() => extractNFormulasAndThen(refs, 0) {
      case Nil => formula match {
        case Not(Zero() ~= t + One()) => 
          Nil

        case _ => 
          fail(ShapeMismatch(Conclusion, RulePart.Not(RulePart.Equals(
            RulePart.Zero(), 
            RulePart.Plus(MetaTerm(Terms.T), RulePart.One())
          ))))
      }
    }

    case Peano6() => extractNFormulasAndThen(refs, 1) {
      case List(t1 + One() ~= t2 + One()) => (formula match {
        case t3 ~= t4 =>
          failIf(t3 != t1, Ambiguous(MetaTerm(Terms.T1), List(
            (Conclusion, Location.lhs),
            (Premise(0), Location.lhs.lhs)
          ))) ++
          failIf(t4 != t2, Ambiguous(MetaTerm(Terms.T2), List(
            (Conclusion, Location.rhs),
            (Premise(1), Location.rhs.lhs)
          )))

        case _ => 
          fail(ShapeMismatch(Conclusion, RulePart.Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T2))))
      })

      case List(_) => 
        fail(ShapeMismatch(Premise(0), RulePart.Equals(
          RulePart.Plus(MetaTerm(Terms.T1), RulePart.One()),
          RulePart.Plus(MetaTerm(Terms.T2), RulePart.One())
        )))
    }

    case Induction() => extractAndThen(refs, List(BoxOrFormula.Formula, BoxOrFormula.Box)) {
      case List(Line(r0), box: Box[F, BI]) => box.info.freshVar match {
        case Some(n) => formula match {
          case ForAll(x, phi) =>
            (substitutor.findReplacement(phi, r0, x) match {
              case Some(() | Zero()) => Nil

              case _ => fail(Ambiguous(MetaFormula(Formulas.Phi), List(
                (Conclusion, Location.formulaInsideQuantifier),
                (Premise(0), Location.root),
                (Premise(1), Location.assumption),
                (Premise(1), Location.conclusion)
              )))

            }) ++ (extractFirstLine(box) match {
              case Some(ass) => 
                substitutor.findReplacement(phi, ass, x) match {
                  case Some(y) if y == n || y == () => Nil
                  case _ => fail(Ambiguous(MetaFormula(Formulas.Phi), List(
                    (Conclusion, Location.formulaInsideQuantifier),
                    (Premise(0), Location.root),
                    (Premise(1), Location.assumption),
                    (Premise(1), Location.conclusion)
                  )))
                }
                
              case _ => 
                fail(Miscellaneous(Premise(1), "first step in box must be a line"))

            }) ++ (extractLastLine(box) match {
              case Some(ass) => 
                substitutor.findReplacement(phi, ass, x) match {
                  case Some(y + One()) if y == n => Nil
                  case Some(()) => Nil
                  case _ => fail(Ambiguous(MetaFormula(Formulas.Phi), List(
                    (Conclusion, Location.formulaInsideQuantifier),
                    (Premise(0), Location.root),
                    (Premise(1), Location.assumption),
                    (Premise(1), Location.conclusion)
                  )))
                }
                
              case _ => 
                fail(Miscellaneous(Premise(1), "last step in box must be a line"))
            })

          case _ => 
            fail(ShapeMismatch(Conclusion, RulePart.ForAll(MetaVariable(Vars.X), MetaFormula(Formulas.Phi))))
        }

        case None =>
          fail(ReferenceBoxMissingFreshVar(1))
      }
    }
  }
}
