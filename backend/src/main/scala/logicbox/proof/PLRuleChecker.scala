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

  // private def failIf(b: Boolean, violations: => Viol*): List[Viol] = {
  //   if !b then Nil else violations.toList
  // }

  override def check(rule: PLRule, formula: PLFormula, refs: List[Ref]): List[Viol] = rule match {
    case Premise() | Assumption() => Nil

    case AndElim(side) => extractNFormulasAndThen(refs, 1) {
      case List(ref) => ref match {
        case And(lhs, rhs) => side match {
          case Side.Left => 
            if (lhs != formula) List(
              FormulaDoesntMatchReference(0, "formula doesn't match left-hand side")
            ) else Nil
              
          case Side.Right =>
            if (rhs != formula) List(
              FormulaDoesntMatchReference(0, "formula doesn't match right-hand side")
            ) else Nil
        }
        
        case _ => List(
          ReferenceDoesntMatchRule(0, "must be conjuction (and)")
        )
      }
    }

    case AndIntro() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => formula match {
        case And(phi, psi) => List(
          (if (phi != r0) List(
            FormulaDoesntMatchReference(0, "left-hand side of formula must match")
          ) else Nil)
          ++
          (if (psi != r1) List(
            FormulaDoesntMatchReference(1, "right-hand side of formula must match")
          ) else Nil)
        ).flatten
        
        case _ => List(FormulaDoesntMatchRule("must be a conjunction (and)"))
      }
    }

    case OrIntro(side) => extractNFormulasAndThen(refs, 1) {
      case List(ref) => (side, formula) match {
        case (Side.Left, Or(lhs, _)) => 
          if (lhs != ref) List(
            FormulaDoesntMatchReference(0, "left-hand side of formula must match reference")
          ) else Nil

        case (Side.Right, Or(_, rhs)) =>
          if (rhs != ref) List(
            FormulaDoesntMatchReference(0, "right-hand side of formula must match reference")
          ) else Nil

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

        {
          if (formula != cl1) List(
            FormulaDoesntMatchReference(1, "must match last line of box")
          ) else Nil
        } ++ {
          if (formula != cl2) List(
            FormulaDoesntMatchReference(2, "must match last line of box")
          ) else Nil
        } ++ {
          if (cl1 != cl2) List(
            ReferencesMismatch(List(1, 2), "last lines of boxes must match") 
          ) else Nil
        } ++ { 
          r0 match {
            case Or(lhs, rhs) => {
              if (as1 != lhs) List(
                ReferencesMismatch(List(0, 1), "left-hand side must match assumption")
              ) else Nil
            } ++ {
              if (as2 != rhs) List(
                ReferencesMismatch(List(0, 2), "right-hand side must match assumption")
              ) else Nil
            }
            case _ => List(
              ReferenceDoesntMatchRule(0, "must be a disjunction (or)")
            )
          }
        }
      }
    }

    case ImplicationIntro() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(box: Reference.Box[PLFormula, _]) => formula match {
        case Implies(phi, psi) =>
          (if (phi != box.assumption) List(
            FormulaDoesntMatchReference(0, "left-hand side  must match assumption of box")
          ) else Nil) 
          ++
          (if (psi != box.conclusion) List(
            FormulaDoesntMatchReference(0, "right-hand side must match conclusion of box")
          ) else Nil)
        case _ => List(
          FormulaDoesntMatchRule("must be an implication (->)")
        )
      }
    }

    case ImplicationElim() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => r1 match {
        case Implies(from, to) => 
          (if (from != r0) List(
            ReferencesMismatch(List(0, 1), "must match left-hand side of implication")
          ) else Nil)
          ++
          (if (to != formula) List(
            FormulaDoesntMatchReference(1, "must match right-hand side of implication")
          ) else Nil)
        case _ => List(
          ReferenceDoesntMatchRule(1, "must be an implication")
        )
      }
    }

    case NotIntro() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(Box[PLFormula, PLBoxInfo](_, asmp, concl)) => {
          if (concl != Contradiction()) List(
            ReferenceDoesntMatchRule(0, "last line of box must be contradiction")
          ) else Nil
        } ++ { formula match {
          case Not(phi) => 
            if (phi != asmp) List(
              FormulaDoesntMatchReference(0, "must be the negation of the assumption in the box")
            ) else Nil
          case _ => List(
            FormulaDoesntMatchRule("must be a negation")
          )
        }
      }
    }

    case NotElim() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => {
        if (formula != Contradiction()) List(
          FormulaDoesntMatchRule("must be contradiction")
        ) else Nil
      } ++ { r1 match {
        case Not(phi) => 
          if (r0 != phi) List(
            ReferencesMismatch(List(0, 1), "second reference must be negation of first")
          ) else Nil
        case _ => List(
          ReferenceDoesntMatchRule(1, "must be negation")
        )
      }}
    }

    case ContradictionElim() => extractNFormulasAndThen(refs, 1) {
      case List(r0) =>
        if (r0 != Contradiction()) List(
          ReferenceDoesntMatchRule(0, "must be a contradiction")
        ) else Nil
    }

    case NotNotElim() => extractNFormulasAndThen(refs, 1) {
      case List(Not(Not(phi))) => 
        if (formula != phi) List(
          FormulaDoesntMatchReference(0, "must equal reference with the two outermost negations removed")
        ) else Nil
      case List(_) => List(
        ReferenceDoesntMatchRule(0, "must be a negation of a negation")
      )
    }

    case ModusTollens() => extractNFormulasAndThen(refs, 2) {
      case List(r0, r1) => {
        formula match {
          case Not(_) => Nil
          case _ => List(FormulaDoesntMatchRule("must be a negation"))
        }
      } ++ {
        r0 match {
          case Implies(_, _) => Nil
          case _ => List(ReferenceDoesntMatchRule(0, "must be an implication"))
        }
      } ++ {
        r1 match {
          case Not(_) => Nil
          case _ => List(ReferenceDoesntMatchRule(1, "must be a negation"))
        }
      } ++ {
        (formula, r0, r1) match {
          case (Not(phi2), Implies(phi1, psi1), Not(psi2)) => {
            if (phi2 != phi1) List(
              FormulaDoesntMatchReference(0, "must be negation of left-hand side of implication")
            ) else Nil
          } ++ {
            if (psi1 != psi2) List(
              ReferencesMismatch(List(0, 1), "second reference must be the negation of the right-hand side of the implication")
            ) else Nil
          }
          case _ => Nil
        }
      }
    }

    case NotNotIntro() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => formula match {
        case Not(Not(phi)) => 
          if (phi != ref) List(
            FormulaDoesntMatchReference(0, "must equal the reference, but with two outer negations removed")
          ) else Nil
        case _ => List(
          FormulaDoesntMatchRule("must equal the reference, but with the two outer negations removed")
        )
      }
    }

    case ProofByContradiction() => extractAndThen(refs, List(BoxOrFormula.Box)) {
      case List(box: Reference.Box[PLFormula, _] @unchecked) => { 
        box.assumption match {
          case Not(phi) => 
            if (formula != phi) List(
              FormulaDoesntMatchReference(0, "must be assumption without negation")
            ) else Nil
          case _ => List(ReferenceDoesntMatchRule(0, "assumption in box must be a negation"))
        }
      } ++ { 
        box.conclusion match {
          case Contradiction() => Nil
          case _ => List(ReferenceDoesntMatchRule(0, "last line in box must be a contradiction"))
        }
      }
    }

    case LawOfExcludedMiddle() => extractNFormulasAndThen(refs, 0) {
      case _ => formula match {
        case Or(lhs, Not(rhs)) if lhs == rhs => Nil
        case _ => List(FormulaDoesntMatchRule("must be the disjunction of a formula and its negation"))
      }
    }

    case Copy() => extractNFormulasAndThen(refs, 1) {
      case List(ref) => 
        if (ref != formula) List(
          FormulaDoesntMatchReference(0, "must be an exact copy of reference")
        ) else Nil
    }
  }
}
