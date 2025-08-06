package logicbox.rule

import logicbox.framework.RuleChecker
import logicbox.framework.Reference
import logicbox.framework.Error

import logicbox.rule.PropLogicRule

import logicbox.formula.ConnectiveFormula
import logicbox.formula.ConnectiveFormula._
import logicbox.framework.Error._
import logicbox.framework.RulePart.MetaFormula
import logicbox.framework.Location
import logicbox.framework.RulePart.Formulas

class PropLogicRuleChecker[F](using ConnectiveFormula[F])
extends RuleChecker[F, PropLogicRule, Any] {
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
        case lhs & rhs => side match {
          case Side.Left => 
            failIf(lhs != formula, Ambiguous(
              MetaFormula(Formulas.Phi), List(
                Location.conclusion.root,
                Location.premise(0).lhs
              )
            ))
              
          case Side.Right =>
            failIf(rhs != formula, Ambiguous(
              MetaFormula(Formulas.Phi), List(
                Location.conclusion.root,
                Location.premise(0).rhs
              )
            ))
        }
        
        case _ => fail(ShapeMismatch(Location.premise(0)))
      }
    }
  
    case AndIntro() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => formula match {
        case phi & psi =>
          failIf(phi != r0, Ambiguous(MetaFormula(Formulas.Phi), List(
            Location.conclusion.lhs,
            Location.premise(0).root
          ))) ++
          failIf(psi != r1, Ambiguous(MetaFormula(Formulas.Psi), List(
            Location.conclusion.rhs,
            Location.premise(1).root
          )))
        
        case _ => fail(ShapeMismatch(Location.conclusion))
      }
    }
  
    case OrIntro(side) => extractNFormulasAndThen(refs, 1) {
      case List(ref) => (side, formula) match {
        case (Side.Left, lhs || _) => 
          failIf(lhs != ref, Ambiguous(MetaFormula(Formulas.Phi), List(
            Location.conclusion.lhs,
            Location.premise(0).root
          )))

        case (Side.Right, _ || rhs) =>
          failIf(rhs != ref, Ambiguous(MetaFormula(Formulas.Psi), List(
            Location.conclusion.rhs,
            Location.premise(0).root
          )))

        case _ => fail(ShapeMismatch(Location.conclusion))
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
            Location.conclusion.root,
            Location.premise(1).lastLine,
            Location.premise(2).lastLine
          ))) ++
          { r0 match {
            case lhs || rhs =>
              // assumptions must match each operand of r0
              failIf(as1 != Some(lhs), Ambiguous(MetaFormula(Formulas.Phi), List(
                Location.premise(0).lhs,
                Location.premise(1).firstLine
              ))) ++
              failIf(as2 != Some(rhs), Ambiguous(MetaFormula(Formulas.Psi), List(
                Location.premise(0).rhs,
                Location.premise(2).firstLine
              )))

            case _ => fail(ShapeMismatch(Location.premise(0)))
          }}
        }
      }
  
    case ImplicationIntro() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(box: Reference.Box[F, _]) => formula match {
        case phi --> psi =>
          val (ass, concl) = (extractFirstLine(box), extractLastLine(box))
          failIf(Some(phi) != ass, Ambiguous(MetaFormula(Formulas.Phi), List(
            Location.conclusion.lhs,
            Location.premise(0).firstLine
          ))) ++
          failIf(Some(psi) != concl, Ambiguous(MetaFormula(Formulas.Psi), List(
            Location.conclusion.rhs,
            Location.premise(0).lastLine
          )))

        case _ => 
          fail(ShapeMismatch(Location.conclusion))
      }
    }

    case ImplicationElim() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => r1 match {
        case from --> to => 
          failIf(from != r0, Ambiguous(MetaFormula(Formulas.Phi), List(
            Location.premise(0).root,
            Location.premise(1).lhs
          ))) ++
          failIf(to != formula, Ambiguous(MetaFormula(Formulas.Psi), List(
            Location.premise(1).rhs,
            Location.conclusion.root
          )))

        case _ => 
          fail(ShapeMismatch(Location.premise(1)))
      }
    }

    case NotIntro() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(b: Box[F, ?]) => 
        { extractLastLine(b) match {
          case Some(⊥) => Nil

          case _ => 
            fail(ShapeMismatch(Location.premise(0).lastLine))

        }} ++ { 
          formula match {
            case ~(phi) => 
              failIf(Some(phi) != extractFirstLine(b), Ambiguous(MetaFormula(Formulas.Phi), List( // TODO: what if not some?
                Location.conclusion.negated,
                Location.premise(0).firstLine
              )))

            case _ => 
              fail(ShapeMismatch(Location.conclusion))
          }
        }
    }
  
    case NotElim() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) =>
        { formula match {
          case ⊥ => Nil
          case _ => fail(ShapeMismatch(Location.conclusion))
        }} ++ { r1 match {
          case ~(phi) => 
            failIf(r0 != phi, Ambiguous(MetaFormula(Formulas.Phi), List(
              Location.premise(0).root,
              Location.premise(1).negated
            )))

          case _ =>
            fail(ShapeMismatch(Location.premise(1)))
        }}
    }

    case ContradictionElim() => extractNFormulasAndThen(refs, 1) {
      case List(⊥) => Nil
      case _ => fail(ShapeMismatch(Location.premise(0)))
    }

    case NotNotElim() => extractNFormulasAndThen(refs, 1) {
      case List(~(~(phi))) => 
        failIf(formula != phi, Ambiguous(MetaFormula(Formulas.Phi), List(
          Location.conclusion.root,
          Location.premise(0).negated.negated
        )))

      case List(_) => 
        fail(ShapeMismatch(Location.premise(0)))
    }
  
    case ModusTollens() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => 
        { formula match {
          case ~(_) => Nil
          case _ => 
            fail(ShapeMismatch(Location.conclusion))
        }} ++ { r0 match {
          case _ --> _ => Nil
          case _ => 
            fail(ShapeMismatch(Location.premise(0)))
        }} ++ { r1 match {
          case ~(_) => Nil
          case _ => 
            fail(ShapeMismatch(Location.premise(1)))
        }} ++ { (formula, r0, r1) match {
          case (~(phi2), phi1 --> psi1, ~(psi2)) =>
            failIf(phi2 != phi1, Ambiguous(MetaFormula(Formulas.Phi), List(
              Location.conclusion.negated,
              Location.premise(0).lhs
            ))) ++
            failIf(psi1 != psi2, Ambiguous(MetaFormula(Formulas.Psi), List(
              Location.premise(0).rhs,
              Location.premise(1).negated
            )))

          case _ => Nil
        }}
    }

    case NotNotIntro() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => formula match {
        case ~(~(phi)) => 
          failIf(phi != ref, Ambiguous(MetaFormula(Formulas.Phi), List(
            Location.conclusion.negated.negated,
            Location.premise(0).root
          )))

        case _ => 
          fail(ShapeMismatch(Location.conclusion))
      }
    }
  
    case ProofByContradiction() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(Reference.Box(_, ass, concl)) => { 
        ass match {
          case Some(Reference.Line(~(phi))) => 
            failIf(formula != phi, Ambiguous(MetaFormula(Formulas.Phi), List(
              Location.conclusion.root,
              Location.premise(0).firstLine.negated
            )))

          case _ => 
            fail(ShapeMismatch(Location.premise(0).firstLine))
        }
      } ++ { 
        concl match {
          case Some(Reference.Line(⊥)) => Nil
          case _ => 
            fail(ShapeMismatch(Location.premise(0).lastLine))
        }
      }
    }

    case LawOfExcludedMiddle() => extractNFormulasAndThen(refs, 0) {
      case _ => formula match {
        case lhs || ~(rhs) => 
          failIf(lhs != rhs, Ambiguous(MetaFormula(Formulas.Phi), List(
            Location.conclusion.lhs,
            Location.conclusion.rhs.negated
          )))

        case _ => fail(ShapeMismatch(Location.conclusion))
      }
    }

    case Copy() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => 
        failIf(ref != formula, Ambiguous(MetaFormula(Formulas.Phi), List(
          Location.conclusion.root,
          Location.premise(0).root
        )))
    }
  }
}
