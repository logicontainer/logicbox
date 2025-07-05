package logicbox.rule

import logicbox.framework.RuleChecker
import logicbox.framework.Reference
import logicbox.framework.Error

import logicbox.rule.PropLogicRule

import logicbox.formula.ConnectiveFormula
import logicbox.formula.ConnectiveFormula._
import logicbox.framework.Error._
import logicbox.rule.RulePart.MetaFormula
import logicbox.framework.Location
import logicbox.rule.RulePart.Formulas

class PropLogicRuleChecker[F <: ConnectiveFormula[F]] extends RuleChecker[F, PropLogicRule, Any] {
  private type Ref = Reference[F, Any]

  import Reference._
  import logicbox.framework.RulePosition.{Conclusion, Premise => PPremise}
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
              MetaFormula(Formulas.Phi), List(
                (Conclusion, Location.root),
                (PPremise(0), Location.lhs)
              )
            ))
              
          case Side.Right =>
            failIf(rhs != formula, Ambiguous(
              MetaFormula(Formulas.Phi), List(
                (Conclusion, Location.root),
                (PPremise(1), Location.rhs)
              )
            ))
        }
        
        case _ => fail(ShapeMismatch(PPremise(0), RulePart.And(MetaFormula(Formulas.Phi), MetaFormula(Formulas.Psi))))
      }
    }
  
    case AndIntro() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => formula match {
        case And(phi, psi) =>
          failIf(phi != r0, Ambiguous(MetaFormula(Formulas.Phi), List(
            (Conclusion, Location.lhs),
            (PPremise(0), Location.root)
          ))) ++
          failIf(psi != r1, Ambiguous(MetaFormula(Formulas.Psi), List(
            (Conclusion, Location.rhs),
            (PPremise(1), Location.root)
          )))
        
        case _ => fail(ShapeMismatch(Conclusion, RulePart.And(MetaFormula(Formulas.Phi), MetaFormula(Formulas.Psi))))
      }
    }
  
    case OrIntro(side) => extractNFormulasAndThen(refs, 1) {
      case List(ref) => (side, formula) match {
        case (Side.Left, Or(lhs, _)) => 
          failIf(lhs != ref, Ambiguous(MetaFormula(Formulas.Phi), List(
            (Conclusion, Location.lhs),
            (PPremise(0), Location.root)
          )))

        case (Side.Right, Or(_, rhs)) =>
          failIf(rhs != ref, Ambiguous(MetaFormula(Formulas.Psi), List(
            (Conclusion, Location.rhs),
            (PPremise(0), Location.root)
          )))

        case _ => fail(ShapeMismatch(Conclusion, RulePart.Or(MetaFormula(Formulas.Phi), MetaFormula(Formulas.Psi))))
      }
    }

    case OrElim() => 
      val pattern = { import BoxOrFormula._; List(BoxOrFormula.Formula, Box, Box) }

      extractAndThen(refs, pattern)  {
        case List(
          Reference.Line(r0: F),
          b1: Reference.Box[F, Any],
          b2: Reference.Box[F, Any],
        ) => {
          val (as1, cl1) = (extractFirstLine(b1), extractLastLine(b1))
          val (as2, cl2) = (extractFirstLine(b2), extractLastLine(b2))

          // conclusions must match formula
          failIf(Set(Some(formula), cl1, cl2).size > 1, Ambiguous(MetaFormula(Formulas.Chi), List(
            (Conclusion, Location.root),
            (PPremise(1), Location.conclusion),
            (PPremise(2), Location.conclusion)
          ))) ++
          { r0 match {
            case Or(lhs, rhs) =>
              // assumptions must match each operand of r0
              failIf(as1 != Some(lhs), Ambiguous(MetaFormula(Formulas.Phi), List(
                (PPremise(0), Location.lhs),
                (PPremise(1), Location.assumption)
              ))) ++
              failIf(as2 != Some(rhs), Ambiguous(MetaFormula(Formulas.Psi), List(
                (PPremise(0), Location.rhs),
                (PPremise(2), Location.assumption)
              )))

            case _ => fail(ShapeMismatch(PPremise(0), RulePart.Or(MetaFormula(Formulas.Phi), MetaFormula(Formulas.Psi))))
          }}
        }
      }
  
    case ImplicationIntro() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(box: Reference.Box[F, _]) => formula match {
        case Implies(phi, psi) =>
          val (ass, concl) = (extractFirstLine(box), extractLastLine(box))
          failIf(Some(phi) != ass, Ambiguous(MetaFormula(Formulas.Phi), List(
            (Conclusion, Location.lhs),
            (PPremise(0), Location.assumption)
          ))) ++
          failIf(Some(psi) != concl, Ambiguous(MetaFormula(Formulas.Psi), List(
            (Conclusion, Location.rhs),
            (PPremise(0), Location.conclusion)
          )))

        case _ => 
          fail(ShapeMismatch(Conclusion, RulePart.Implies(MetaFormula(Formulas.Phi), MetaFormula(Formulas.Psi))))
      }
    }

    case ImplicationElim() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => r1 match {
        case Implies(from, to) => 
          failIf(from != r0, Ambiguous(MetaFormula(Formulas.Phi), List(
            (PPremise(0), Location.root),
            (PPremise(1), Location.lhs)
          ))) ++
          failIf(to != formula, Ambiguous(MetaFormula(Formulas.Psi), List(
            (PPremise(1), Location.rhs),
            (Conclusion, Location.root)
          )))

        case _ => 
          fail(ShapeMismatch(PPremise(1), RulePart.Implies(MetaFormula(Formulas.Phi), MetaFormula(Formulas.Psi))))
      }
    }

    case NotIntro() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(b: Box[F, ?]) => 
        { extractLastLine(b) match {
          case Some(Contradiction()) => Nil

          case _ => 
            fail(ShapeMismatch((PPremise(0), Location.conclusion), RulePart.Contradiction()))

        }} ++ { 
          formula match {
            case Not(phi) => 
              failIf(Some(phi) != extractFirstLine(b), Ambiguous(MetaFormula(Formulas.Phi), List( // TODO: what if not some?
                (Conclusion, Location.negated),
                (PPremise(0), Location.assumption)
              )))

            case _ => 
              fail(ShapeMismatch(Conclusion, RulePart.Not(MetaFormula(Formulas.Phi))))
          }
        }
    }
  
    case NotElim() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) =>
        { formula match {
          case Contradiction() => Nil
          case _ => fail(ShapeMismatch(Conclusion, RulePart.Contradiction()))
        }} ++ { r1 match {
          case Not(phi) => 
            failIf(r0 != phi, Ambiguous(MetaFormula(Formulas.Phi), List(
              (PPremise(0), Location.root),
              (PPremise(1), Location.negated)
            )))

          case _ =>
            fail(ShapeMismatch(PPremise(1), RulePart.Not(MetaFormula(Formulas.Phi))))
        }}
    }

    case ContradictionElim() => extractNFormulasAndThen(refs, 1) {
      case List(Contradiction()) => Nil
      case _ => fail(ShapeMismatch(PPremise(0), RulePart.Contradiction()))
    }

    case NotNotElim() => extractNFormulasAndThen(refs, 1) {
      case List(Not(Not(phi))) => 
        failIf(formula != phi, Ambiguous(MetaFormula(Formulas.Phi), List(
          (Conclusion, Location.root),
          (PPremise(0), Location.negated.negated)
        )))

      case List(_) => 
        fail(ShapeMismatch(PPremise(0), RulePart.Not(RulePart.Not(MetaFormula(Formulas.Phi)))))
    }
  
    case ModusTollens() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => 
        { formula match {
          case Not(_) => Nil
          case _ => 
            fail(ShapeMismatch(Conclusion, RulePart.Not(MetaFormula(Formulas.Phi))))
        }} ++ { r0 match {
          case Implies(_, _) => Nil
          case _ => 
            fail(ShapeMismatch(PPremise(0), RulePart.Implies(MetaFormula(Formulas.Phi), MetaFormula(Formulas.Psi))))
        }} ++ { r1 match {
          case Not(_) => Nil
          case _ => 
            fail(ShapeMismatch(PPremise(1), RulePart.Not(MetaFormula(Formulas.Psi))))
        }} ++ { (formula, r0, r1) match {
          case (Not(phi2), Implies(phi1, psi1), Not(psi2)) =>
            failIf(phi2 != phi1, Ambiguous(MetaFormula(Formulas.Phi), List(
              (Conclusion, Location.negated),
              (PPremise(0), Location.lhs)
            ))) ++
            failIf(psi1 != psi2, Ambiguous(MetaFormula(Formulas.Psi), List(
              (PPremise(0), Location.rhs),
              (PPremise(1), Location.negated)
            )))

          case _ => Nil
        }}
    }

    case NotNotIntro() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => formula match {
        case Not(Not(phi)) => 
          failIf(phi != ref, Ambiguous(MetaFormula(Formulas.Phi), List(
            (Conclusion, Location.negated.negated),
            (PPremise(0), Location.root)
          )))

        case _ => 
          fail(ShapeMismatch(Conclusion, RulePart.Not(RulePart.Not(MetaFormula(Formulas.Phi)))))
      }
    }
  
    case ProofByContradiction() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(Reference.Box(_, ass, concl)) => { 
        ass match {
          case Some(Reference.Line(Not(phi))) => 
            failIf(formula != phi, Ambiguous(MetaFormula(Formulas.Phi), List(
              (Conclusion, Location.root),
              (PPremise(0), Location.assumption.negated)
            )))

          case _ => 
            fail(ShapeMismatch((PPremise(0), Location.assumption), RulePart.Not(MetaFormula(Formulas.Phi))))
        }
      } ++ { 
        concl match {
          case Some(Reference.Line(Contradiction())) => Nil
          case _ => 
            fail(ShapeMismatch((PPremise(0), Location.conclusion), RulePart.Contradiction()))
        }
      }
    }

    case LawOfExcludedMiddle() => extractNFormulasAndThen(refs, 0) {
      case _ => formula match {
        case Or(lhs, Not(rhs)) => 
          failIf(lhs != rhs, Ambiguous(MetaFormula(Formulas.Phi), List(
            (Conclusion, Location.lhs),
            (Conclusion, Location.rhs.negated)
          )))

        case _ => fail(ShapeMismatch(Conclusion, RulePart.Or(MetaFormula(Formulas.Phi), RulePart.Not(MetaFormula(Formulas.Phi)))))
      }
    }

    case Copy() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => 
        failIf(ref != formula, Ambiguous(MetaFormula(Formulas.Phi), List(
          (Conclusion, Location.root),
          (PPremise(0), Location.root)
        )))
    }
  }
}
