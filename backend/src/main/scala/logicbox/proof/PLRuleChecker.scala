package logicbox.proof

import logicbox.framework.RuleChecker
import logicbox.framework.Reference

import logicbox.proof.PLRule
import logicbox.proof.PLRule._

import logicbox.formula.PLFormula
import logicbox.formula.PLFormula._

class PLRuleChecker extends RuleChecker[PLFormula, PLRule, PLBoxInfo, PLViolation] {
  private type Ref = Reference[PLFormula, PLBoxInfo]
  private type Viol = PLViolation

  import PLViolation._

  private enum BoxOrFormula { case Box; case Formula }

  import Reference._
  import PLRule._

  private def extractAndThen(refs: List[Ref], pattern: Seq[BoxOrFormula]) 
    (func: PartialFunction[List[Ref], List[Viol]]): List[Viol] = 
  {
    def checkLengthMatches(refs: Seq[?], pattern: Seq[?]): List[Viol] = {
      if (refs.length != pattern.length) List(
        WrongNumberOfReferences(pattern.length, refs.length)
      ) else Nil
    }

    def extract(refs: List[Ref], pattern: Seq[BoxOrFormula]): Either[List[Viol], List[Ref]] = {
      val zp = refs.zipWithIndex.zip(pattern).map { case ((ref, idx), pattern) => (idx, pattern, ref)}

      val result = zp.map {
        // matches
        case (_, BoxOrFormula.Formula, f: Line[PLFormula] @unchecked) => Right(f)
        case (_, BoxOrFormula.Box, b: Box[PLFormula, PLBoxInfo]) => Right(b)

        // violations
        case (idx, BoxOrFormula.Box, _) => Left(ReferenceShouldBeBox(idx))
        case (idx, BoxOrFormula.Formula, _) => Left(ReferenceShouldBeLine(idx))
      }

      val good = result.forall {
        case Right(_) => true
        case Left(_) => false
      }

      if (good) {
        // collect steps 
        Right(result.collect { case Right(step) => step }) 
      } else {
        // collect violations
        Left(result.collect { case Left(mm) => mm })
      }
    }

    checkLengthMatches(refs, pattern) ++ { 
      extract(refs, pattern) match {
        case Right(ls: List[Ref]) =>
          assert(func.isDefinedAt(ls), s"Partial function is defined on given pattern $pattern")
          func.apply(ls)
        case Left(mismatches) => mismatches
      }
    }
  }

  // try to extract a list of `n` formulas from `refs` (only if there are `n`).
  // otherwise report mismatches
  private def extractNFormulasAndThen(refs: List[Ref], n: Int)
    (func: PartialFunction[List[PLFormula], List[Viol]]): List[Viol] = 
  {
    extractAndThen(refs, (1 to n).map { _ => BoxOrFormula.Formula }) {
      case lines: List[Reference.Line[PLFormula]] @unchecked =>
        func.apply(lines.map(_.formula))
    }
  }

  private def fail(v: => Viol, vs: => Viol*): List[Viol] = (v +: vs).toList
  private def failIf(b: Boolean, v: => Viol, vs: => Viol*): List[Viol] = {
    if !b then Nil else (v +: vs).toList
  }

  override def check(rule: PLRule, formula: PLFormula, refs: List[Ref]): List[Viol] = rule match {
    case Premise() | Assumption() => Nil

    case AndElim(side) => extractNFormulasAndThen(refs, 1) {
      case List(ref) => ref match {
        case And(lhs, rhs) => side match {
          case Side.Left => 
            failIf(lhs != formula, FormulaDoesntMatchReference(0, "formula doesn't match left-hand side"))
              
          case Side.Right =>
            failIf(rhs != formula, FormulaDoesntMatchReference(0, "formula doesn't match right-hand side"))
        }
        
        case _ => fail(ReferenceDoesntMatchRule(0, "must be conjunction (and)"))
      }
    }

    case AndIntro() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => formula match {
        case And(phi, psi) =>
          failIf(phi != r0, FormulaDoesntMatchReference(0, "left-hand side of formula must match")) ++
          failIf(psi != r1, FormulaDoesntMatchReference(1, "right-hand side of formula must match"))
        
        case _ => fail(FormulaDoesntMatchRule("must be a conjunction (and)"))
      }
    }

    case OrIntro(side) => extractNFormulasAndThen(refs, 1) {
      case List(ref) => (side, formula) match {
        case (Side.Left, Or(lhs, _)) => 
          failIf(lhs != ref, FormulaDoesntMatchReference(0, "left-hand side of formula must match reference"))

        case (Side.Right, Or(_, rhs)) =>
          failIf(rhs != ref, FormulaDoesntMatchReference(0, "right-hand side of formula must match reference"))

        case _ => List(FormulaDoesntMatchRule("must be a disjunction (or)"))
      }
    }

    case OrElim() => 
      val pattern = { import BoxOrFormula._; List(Formula, Box, Box) }

      extractAndThen(refs, pattern)  {
        case List(
          Reference.Line(r0: PLFormula),
          b1: Reference.Box[PLFormula, _] @unchecked,
          b2: Reference.Box[PLFormula, _] @unchecked
        ) => {
          val Box(_, as1, cl1) = b1
          val Box(_, as2, cl2) = b2

          failIf(formula != cl1, FormulaDoesntMatchReference(1, "must match last line of box")) ++
          failIf(formula != cl2, FormulaDoesntMatchReference(2, "must match last line of box")) ++
          failIf(cl1 != cl2, ReferencesMismatch(List(1, 2), "last lines of boxes must match")) ++
          { r0 match {
            case Or(lhs, rhs) =>
              failIf(as1 != lhs, ReferencesMismatch(List(0, 1), "left-hand side must match assumption")) ++
              failIf(as2 != rhs, ReferencesMismatch(List(0, 2), "right-hand side must match assumption"))

            case _ => 
              fail(ReferenceDoesntMatchRule(0, "must be a disjunction (or)"))
          }}
        }
      }

    case ImplicationIntro() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(box: Reference.Box[PLFormula, _]) => formula match {
        case Implies(phi, psi) =>
          failIf(phi != box.assumption, FormulaDoesntMatchReference(0, "left-hand side  must match assumption of box")) ++
          failIf(psi != box.conclusion, FormulaDoesntMatchReference(0, "right-hand side must match conclusion of box"))

        case _ => 
          fail(FormulaDoesntMatchRule("must be an implication (->)"))
      }
    }

    case ImplicationElim() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => r1 match {
        case Implies(from, to) => 
          failIf(from != r0, ReferencesMismatch(List(0, 1), "must match left-hand side of implication")) ++
          failIf(to != formula, FormulaDoesntMatchReference(1, "must match right-hand side of implication"))

        case _ => 
          fail(ReferenceDoesntMatchRule(1, "must be an implication"))
      }
    }

    case NotIntro() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(Box[PLFormula, PLBoxInfo](_, asmp, concl)) =>
        failIf(concl != Contradiction(), ReferenceDoesntMatchRule(0, "last line of box must be contradiction")) ++
        { formula match {
            case Not(phi) => 
              failIf(phi != asmp, FormulaDoesntMatchReference(0, "must be the negation of the assumption in the box"))

            case _ => 
              fail(FormulaDoesntMatchRule("must be a negation"))
        }}
    }

    case NotElim() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) =>
        failIf(formula != Contradiction(), FormulaDoesntMatchRule("must be contradiction")) ++
        { r1 match {
          case Not(phi) => 
            failIf(r0 != phi, ReferencesMismatch(List(0, 1), "second reference must be negation of first"))

          case _ =>
            fail(ReferenceDoesntMatchRule(1, "must be negation"))
        }}
    }

    case ContradictionElim() => extractNFormulasAndThen(refs, 1) {
      case List(r0) =>
        failIf(r0 != Contradiction(), ReferenceDoesntMatchRule(0, "must be a contradiction"))
    }

    case NotNotElim() => extractNFormulasAndThen(refs, 1) {
      case List(Not(Not(phi))) => 
        failIf(formula != phi, FormulaDoesntMatchReference(0, "must equal reference with the two outermost negations removed"))

      case List(_) => 
        fail(ReferenceDoesntMatchRule(0, "must be a negation of a negation"))
    }

    case ModusTollens() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => 
        { formula match {
          case Not(_) => Nil
          case _ => List(FormulaDoesntMatchRule("must be a negation"))
        }} ++ { r0 match {
          case Implies(_, _) => Nil
          case _ => List(ReferenceDoesntMatchRule(0, "must be an implication"))
        }} ++ { r1 match {
          case Not(_) => Nil
          case _ => List(ReferenceDoesntMatchRule(1, "must be a negation"))
        }} ++ { (formula, r0, r1) match {
          case (Not(phi2), Implies(phi1, psi1), Not(psi2)) =>
            failIf(phi2 != phi1, FormulaDoesntMatchReference(0, "must be negation of left-hand side of implication")) ++
            failIf(psi1 != psi2, ReferencesMismatch(List(0, 1), "second reference must be the negation of the right-hand side of the implication"))

          case _ => Nil
        }}
    }

    case NotNotIntro() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => formula match {
        case Not(Not(phi)) => 
          failIf(phi != ref, FormulaDoesntMatchReference(0, "must equal the reference, but with two outer negations removed"))

        case _ => 
          fail(FormulaDoesntMatchRule("must equal the reference, but with the two outer negations removed"))
      }
    }

    case ProofByContradiction() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(box: Reference.Box[PLFormula, _] @unchecked) => { 
        box.assumption match {
          case Not(phi) => 
            failIf(formula != phi, FormulaDoesntMatchReference(0, "must be assumption without negation"))

          case _ => 
            fail(ReferenceDoesntMatchRule(0, "assumption in box must be a negation"))
        }
      } ++ { 
        box.conclusion match {
          case Contradiction() => Nil
          case _ => fail(ReferenceDoesntMatchRule(0, "last line in box must be a contradiction"))
        }
      }
    }

    case LawOfExcludedMiddle() => extractNFormulasAndThen(refs, 0) {
      case _ => formula match {
        case Or(lhs, Not(rhs)) if lhs == rhs => Nil
        case _ => fail(FormulaDoesntMatchRule("must be the disjunction of a formula and its negation"))
      }
    }

    case Copy() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => 
        failIf(ref != formula, FormulaDoesntMatchReference(0, "must be an exact copy of reference"))
    }
  }
}
