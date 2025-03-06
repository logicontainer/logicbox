package logicbox.proof

import logicbox.framework.{ CheckableRule, RuleChecker, Reference }
import logicbox.formula.PLFormula

type PLBoxInfo = Unit

sealed trait PLRule extends CheckableRule[PLFormula, PLBoxInfo, PLViolation]

object PLRule {
  import PLFormula.*
  import PLViolation._

  enum Side { case Left; case Right }
  import Reference._
  private type Ref = Reference[PLFormula, PLBoxInfo]
  private type Viol = PLViolation

  private enum BoxOrFormula { case Box; case Formula }
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

  sealed abstract class NullRule extends PLRule {
    def check(formula: PLFormula, refs: List[Ref]): List[Viol] = Nil
  }

  case class Premise() extends NullRule 
  case class Assumption() extends NullRule 

  case class AndElim(side: Side) extends PLRule {
    private def checkImpl(formula: PLFormula, ref: PLFormula): List[Viol] = ref match {
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

    override def check(formula: PLFormula, refs: List[Ref]): List[Viol] = {
      extractNFormulasAndThen(refs, 1) {
        case List(ref) => checkImpl(formula, ref)
      }
    }
  }

  case class AndIntro() extends PLRule {
    private def checkImpl(formula: PLFormula, r0: PLFormula, r1: PLFormula): List[Viol] = 
      formula match {
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

    override def check(formula: PLFormula, refs: List[Ref]): List[Viol] = {
      extractNFormulasAndThen(refs, 2) {
        case List(r0, r1) => checkImpl(formula, r0, r1)
      }
    }
  }

  case class OrIntro(side: Side) extends PLRule {
    private def checkImpl(formula: PLFormula, ref: PLFormula): List[Viol] = (side, formula) match {
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

    override def check(formula: PLFormula, refs: List[Ref]): List[Viol] = {
      extractNFormulasAndThen(refs, 1) {
        case List(ref) => checkImpl(formula, ref)
      }
    }
  }

  case class OrElim() extends PLRule {
    private def checkImpl(
      formula: PLFormula, r0: PLFormula, 
      r1: (PLFormula, PLFormula), r2: (PLFormula, PLFormula)
    ): List[Viol] = {
      val (as1, cl1) = r1
      val (as2, cl2) = r2

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

    def check(formula: PLFormula, refs: List[Ref]): List[Viol] = {
      val pattern = { 
        import BoxOrFormula._
        List(Formula, Box, Box)
      }

      extractAndThen(refs, pattern)  {
        case List(
          Reference.Line(r0: PLFormula),
          b1: Reference.Box[PLFormula, _] @unchecked,
          b2: Reference.Box[PLFormula, _] @unchecked
        ) =>
          checkImpl(formula, r0, (b1.assumption, b1.conclusion), (b2.assumption, b2.conclusion))
      }
    }
  }

  case class ImplicationIntro() extends PLRule {
    private def checkImpl(formula: PLFormula, asmp: PLFormula, concl: PLFormula): List[Viol] = {
      formula match {
        case Implies(phi, psi) =>
          (if (phi != asmp) List(
            FormulaDoesntMatchReference(0, "left-hand side  must match assumption of box")
          ) else Nil) 
          ++
          (if (psi != concl) List(
            FormulaDoesntMatchReference(0, "right-hand side must match conclusion of box")
          ) else Nil)
        case _ => List(
          FormulaDoesntMatchRule("must be an implication (->)")
        )
      }
    }

    def check(formula: PLFormula, refs: List[Ref]): List[Viol] = {
      extractAndThen(refs, List(BoxOrFormula.Box)) {
        case List(box: Reference.Box[PLFormula, _]) => checkImpl(formula, box.assumption, box.conclusion)
      }
    }
  }

  case class ImplicationElim() extends PLRule {
    private def checkMatchesRef(formula: PLFormula, r0: PLFormula, r1: PLFormula): List[Viol] = 
      r1 match {
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

    override def check(formula: PLFormula, refs: List[Ref]): List[Viol] = {
      extractNFormulasAndThen(refs, 2) {
        case List(r0, r1) =>
          checkMatchesRef(formula, r0, r1)
      }
    }
  }

  case class NotIntro() extends PLRule {
    private def checkImpl(formula: PLFormula, asmp: PLFormula, concl: PLFormula): List[Viol] = {
      {
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
    }}
      
    override def check(formula: PLFormula, refs: List[Ref]): List[Viol] = {
      extractAndThen(refs, List(BoxOrFormula.Box)) {
        case List(Box[PLFormula, PLBoxInfo](_, asmp, concl)) => checkImpl(formula, asmp, concl)
      }
    }
  }

  case class NotElim() extends PLRule {
    private def checkImpl(formula: PLFormula, r0: PLFormula, r1: PLFormula): List[Viol] = {
      {
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

    override def check(formula: PLFormula, refs: List[Ref]): List[Viol] = 
      extractNFormulasAndThen(refs, 2) {
        case List(r0, r1) => checkImpl(formula, r0, r1)
      }
  }

  case class ContradictionElim() extends PLRule {
    override def check(formula: PLFormula, refs: List[Ref]): List[Viol] = {
      extractNFormulasAndThen(refs, 1) {
        case List(r0) =>
          if (r0 != Contradiction()) List(
            ReferenceDoesntMatchRule(0, "must be a contradiction")
          ) else Nil
      }
    }
  }

  case class NotNotElim() extends PLRule {
    override def check(formula: PLFormula, refs: List[Ref]): List[Viol] = {
      extractNFormulasAndThen(refs, 1) {
        case List(Not(Not(phi))) => 
          if (formula != phi) List(
            FormulaDoesntMatchReference(0, "must equal reference with the two outermost negations removed")
          ) else Nil
        case List(_) => List(
          ReferenceDoesntMatchRule(0, "must be a negation of a negation")
        )
      }
    }
  }

  case class ModusTollens() extends PLRule {
    private def checkImpl(formula: PLFormula, r0: PLFormula, r1: PLFormula): List[Viol] = {
      {
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

    override def check(formula: PLFormula, refs: List[Ref]): List[Viol] = {
      extractNFormulasAndThen(refs, 2) {
        case List(r0, r1) => checkImpl(formula, r0, r1)
      }
    }
  }

  case class NotNotIntro() extends PLRule {
    private def checkImpl(formula: PLFormula, ref: PLFormula): List[Viol] = {
      formula match {
        case Not(Not(phi)) => 
          if (phi != ref) List(
            FormulaDoesntMatchReference(0, "must be ")
          ) else Nil
        case _ => List(
          FormulaDoesntMatchRule("must equal the reference, but with the two outer negations removed")
        )
      }
    }

    override def check(formula: PLFormula, refs: List[Ref]): List[Viol] = {
      extractNFormulasAndThen(refs, 1) {
        case List(ref) => checkImpl(formula, ref)
      }
    }
  }

  case class ProofByContradiction() extends PLRule {
    private def checkImpl(formula: PLFormula, asmp: PLFormula, concl: PLFormula): List[Viol] = {
      { asmp match {
        case Not(phi) => 
          if (formula != phi) List(
            FormulaDoesntMatchReference(0, "must be assumption without negation")
          ) else Nil
        case _ => List(ReferenceDoesntMatchRule(0, "assumption in box must be a negation"))
      }} ++ { concl match {
        case Contradiction() => Nil
        case _ => List(ReferenceDoesntMatchRule(0, "last line in box must be a contradiction"))
      }}
    }

    override def check(formula: PLFormula, refs: List[Ref]): List[Viol] = {
      extractAndThen(refs, List(BoxOrFormula.Box)) {
        case List(box: Reference.Box[PLFormula, _] @unchecked) => 
          checkImpl(formula, box.assumption, box.conclusion)
      }
    }
  }

  case class LawOfExcludedMiddle() extends PLRule {
    override def check(formula: PLFormula, refs: List[Ref]): List[Viol] = {
      extractNFormulasAndThen(refs, 0) {
        case Nil =>
          formula match {
            case Or(lhs, Not(rhs)) if lhs == rhs => Nil
            case _ => List(FormulaDoesntMatchRule("must be the disjunction of a formula and its negation"))
          }
      }
    }
  }

  case class Copy() extends PLRule {
    override def check(formula: PLFormula, refs: List[Ref]): List[Viol] = {
      extractNFormulasAndThen(refs, 1) {
        case List(ref) => 
          if (ref != formula) List(
            FormulaDoesntMatchReference(0, "must be an exact copy of reference")
          ) else Nil
      }
    }
  }
}

