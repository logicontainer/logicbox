package logicbox.rule

import logicbox.framework.RuleChecker
import logicbox.framework.Reference
import logicbox.framework.Error

import logicbox.rule.PropLogicRule
import logicbox.rule.PropLogicRule._

import logicbox.formula.ConnectiveFormula
import logicbox.formula.ConnectiveFormula._
import logicbox.framework.Error._
import logicbox.framework.RulePosition._
import logicbox.rule.RulePart.MetaFormula
import logicbox.framework.Location

class PropLogicRuleChecker[F <: ConnectiveFormula[F]] extends RuleChecker[F, PropLogicRule, Any] {
  private type Ref = Reference[F, Any]

  import Reference._
  import PropLogicRule._
  import ReferenceUtil._

  private def fail(v: => Error, vs: => Error*): List[Error] = (v +: vs).toList
  private def failIf(b: Boolean, v: => Error, vs: => Error*): List[Error] = {
    if !b then Nil else (v +: vs).toList
  }

  override def check(rule: PropLogicRule, formula: F, refs: List[Ref]): List[Error] = rule match {
    case Premise() | Assumption() => Nil

    case AndElim(side) => extractNFormulasAndThen(refs, 1) {
      case List(ref) => ref match {
        case And(lhs, rhs) => side match {
          case Side.Left => 
            failIf(lhs != formula, Ambiguous(
              MetaFormula(0), List(
                (Formula, Location.root),
                (Ref(0), Location.lhs)
              )
            ))
              
          case Side.Right =>
            failIf(rhs != formula, Ambiguous(
              MetaFormula(0), List(
                (Formula, Location.root),
                (Ref(1), Location.rhs)
              )
            ))
        }
        
        case _ => fail(ShapeMismatch(Ref(0), RulePart.And(MetaFormula(0), MetaFormula(1))))
      }
    }
  
    case AndIntro() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => formula match {
        case And(phi, psi) =>
          failIf(phi != r0, Ambiguous(MetaFormula(0), List(
            (Formula, Location.lhs),
            (Ref(0), Location.root)
          ))) ++
          failIf(psi != r1, Ambiguous(MetaFormula(1), List(
            (Formula, Location.rhs),
            (Ref(1), Location.root)
          )))
        
        case _ => fail(ShapeMismatch(Formula, RulePart.And(MetaFormula(0), MetaFormula(1))))
      }
    }
  
    case OrIntro(side) => extractNFormulasAndThen(refs, 1) {
      case List(ref) => (side, formula) match {
        case (Side.Left, Or(lhs, _)) => 
          failIf(lhs != ref, Ambiguous(MetaFormula(0), List(
            (Formula, Location.lhs),
            (Ref(0), Location.root)
          )))

        case (Side.Right, Or(_, rhs)) =>
          failIf(rhs != ref, Ambiguous(MetaFormula(1), List(
            (Formula, Location.rhs),
            (Ref(0), Location.root)
          )))

        case _ => fail(ShapeMismatch(Formula, RulePart.Or(MetaFormula(0), MetaFormula(1))))
      }
    }

    case OrElim() => 
      val pattern = { import BoxOrFormula._; List(Formula, Box, Box) }

      extractAndThen(refs, pattern)  {
        case List(
          Reference.Line(r0: F),
          b1: Reference.Box[F, Any],
          b2: Reference.Box[F, Any],
        ) => {
          val (as1, cl1) = (extractFirstLine(b1), extractLastLine(b1))
          val (as2, cl2) = (extractFirstLine(b2), extractLastLine(b2))

          // conclusions must match formula
          failIf(Set(Some(formula), cl1, cl2).size > 1, Ambiguous(MetaFormula(2), List(
            (Formula, Location.root),
            (Ref(1), Location.conclusion),
            (Ref(2), Location.conclusion)
          ))) ++
          { r0 match {
            case Or(lhs, rhs) =>
              // assumptions must match each operand of r0
              failIf(as1 != Some(lhs), Ambiguous(MetaFormula(0), List(
                (Ref(0), Location.lhs),
                (Ref(1), Location.assumption)
              ))) ++
              failIf(as2 != Some(rhs), Ambiguous(MetaFormula(1), List(
                (Ref(0), Location.rhs),
                (Ref(2), Location.assumption)
              )))

            case _ => fail(ShapeMismatch(Ref(0), RulePart.Or(MetaFormula(0), MetaFormula(1))))
          }}
        }
      }
  
    case ImplicationIntro() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(box: Reference.Box[F, _]) => formula match {
        case Implies(phi, psi) =>
          val (ass, concl) = (extractFirstLine(box), extractLastLine(box))
          failIf(Some(phi) != ass, Ambiguous(MetaFormula(0), List(
            (Formula, Location.lhs),
            (Ref(0), Location.assumption)
          ))) ++
          failIf(Some(psi) != concl, Ambiguous(MetaFormula(1), List(
            (Formula, Location.rhs),
            (Ref(0), Location.conclusion)
          )))

        case _ => 
          fail(ShapeMismatch(Formula, RulePart.Implies(MetaFormula(0), MetaFormula(1))))
      }
    }

    case ImplicationElim() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => r1 match {
        case Implies(from, to) => 
          failIf(from != r0, Ambiguous(MetaFormula(0), List(
            (Ref(0), Location.root),
            (Ref(1), Location.lhs)
          ))) ++
          failIf(to != formula, Ambiguous(MetaFormula(1), List(
            (Ref(1), Location.rhs),
            (Formula, Location.root)
          )))

        case _ => 
          fail(ShapeMismatch(Ref(1), RulePart.Implies(MetaFormula(0), MetaFormula(1))))
      }
    }

    case NotIntro() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(b: Box[F, ?]) => 
        { extractLastLine(b) match {
          case Some(Contradiction()) => Nil

          case _ => 
            fail(ShapeMismatch((Ref(0), Location.conclusion), RulePart.Contradiction()))

        }} ++ { 
          formula match {
            case Not(phi) => 
              failIf(Some(phi) != extractFirstLine(b), Ambiguous(MetaFormula(0), List( // TODO: what if not some?
                (Formula, Location.negated),
                (Ref(0), Location.assumption)
              )))

            case _ => 
              fail(ShapeMismatch(Formula, RulePart.Not(MetaFormula(0))))
          }
        }
    }
  
    case NotElim() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) =>
        { formula match {
          case Contradiction() => Nil
          case _ => fail(ShapeMismatch(Formula, RulePart.Contradiction()))
        }} ++ { r1 match {
          case Not(phi) => 
            failIf(r0 != phi, Ambiguous(MetaFormula(0), List(
              (Ref(0), Location.root),
              (Ref(1), Location.negated)
            )))

          case _ =>
            fail(ShapeMismatch(Ref(1), RulePart.Not(MetaFormula(0))))
        }}
    }

    case ContradictionElim() => extractNFormulasAndThen(refs, 1) {
      case List(Contradiction()) => Nil
      case _ => fail(ShapeMismatch(Ref(0), RulePart.Contradiction()))
    }

    case NotNotElim() => extractNFormulasAndThen(refs, 1) {
      case List(Not(Not(phi))) => 
        failIf(formula != phi, Ambiguous(MetaFormula(0), List(
          (Formula, Location.root),
          (Ref(0), Location.negated.negated)
        )))

      case List(_) => 
        fail(ShapeMismatch(Ref(0), RulePart.Not(RulePart.Not(MetaFormula(0)))))
    }
  
    case ModusTollens() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => 
        { formula match {
          case Not(_) => Nil
          case _ => 
            fail(ShapeMismatch(Formula, RulePart.Not(MetaFormula(0))))
        }} ++ { r0 match {
          case Implies(_, _) => Nil
          case _ => 
            fail(ShapeMismatch(Ref(0), RulePart.Implies(MetaFormula(0), MetaFormula(1))))
        }} ++ { r1 match {
          case Not(_) => Nil
          case _ => 
            fail(ShapeMismatch(Ref(1), RulePart.Not(MetaFormula(1))))
        }} ++ { (formula, r0, r1) match {
          case (Not(phi2), Implies(phi1, psi1), Not(psi2)) =>
            failIf(phi2 != phi1, Ambiguous(MetaFormula(0), List(
              (Formula, Location.negated),
              (Ref(0), Location.lhs)
            ))) ++
            failIf(psi1 != psi2, Ambiguous(MetaFormula(1), List(
              (Ref(0), Location.rhs),
              (Ref(1), Location.negated)
            )))

          case _ => Nil
        }}
    }

    case NotNotIntro() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => formula match {
        case Not(Not(phi)) => 
          failIf(phi != ref, Ambiguous(MetaFormula(0), List(
            (Formula, Location.negated.negated),
            (Ref(0), Location.root)
          )))

        case _ => 
          fail(ShapeMismatch(Formula, RulePart.Not(RulePart.Not(MetaFormula(0)))))
      }
    }
  
    case ProofByContradiction() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(Reference.Box(_, ass, concl)) => { 
        ass match {
          case Some(Reference.Line(Not(phi))) => 
            failIf(formula != phi, Ambiguous(MetaFormula(0), List(
              (Formula, Location.root),
              (Ref(0), Location.assumption.negated)
            )))

          case _ => 
            fail(ShapeMismatch((Ref(0), Location.assumption), RulePart.Not(MetaFormula(0))))
        }
      } ++ { 
        concl match {
          case Some(Reference.Line(Contradiction())) => Nil
          case _ => 
            fail(ShapeMismatch((Ref(0), Location.conclusion), RulePart.Contradiction()))
        }
      }
    }

    case LawOfExcludedMiddle() => extractNFormulasAndThen(refs, 0) {
      case _ => formula match {
        case Or(lhs, Not(rhs)) => 
          failIf(lhs != rhs, Ambiguous(MetaFormula(0), List(
            (Formula, Location.lhs),
            (Formula, Location.rhs.negated)
          )))

        case _ => fail(ShapeMismatch(Formula, RulePart.Or(MetaFormula(0), RulePart.Not(MetaFormula(0)))))
      }
    }

    case Copy() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => 
        failIf(ref != formula, Ambiguous(MetaFormula(0), List(
          (Formula, Location.root),
          (Ref(0), Location.root)
        )))
    }
  }
}
