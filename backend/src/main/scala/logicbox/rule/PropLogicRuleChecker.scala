package logicbox.rule

import logicbox.framework.RuleChecker
import logicbox.framework.Reference
import logicbox.framework.Error

import logicbox.rule.PropLogicRule
import logicbox.rule.PropLogicRule._

import logicbox.formula.ConnectiveFormula
import logicbox.formula.ConnectiveFormula._

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
    case _ => ???
  //
  //   case AndElim(side) => extractNFormulasAndThen(refs, 1) {
  //     case List(ref) => ref match {
  //       case And(lhs, rhs) => side match {
  //         case Side.Left => 
  //           failIf(lhs != formula, FormulaDoesntMatchReference(0, "formula doesn't match left-hand side"))
  //             
  //         case Side.Right =>
  //           failIf(rhs != formula, FormulaDoesntMatchReference(0, "formula doesn't match right-hand side"))
  //       }
  //       
  //       case _ => fail(ReferenceDoesntMatchRule(0, "must be conjunction (and)"))
  //     }
  //   }
  //
  //   case AndIntro() => extractNFormulasAndThen(refs, 2) {
  //     case List(r0, r1) => formula match {
  //       case And(phi, psi) =>
  //         failIf(phi != r0, FormulaDoesntMatchReference(0, "left-hand side of formula must match")) ++
  //         failIf(psi != r1, FormulaDoesntMatchReference(1, "right-hand side of formula must match"))
  //       
  //       case _ => fail(FormulaDoesntMatchRule("must be a conjunction (and)"))
  //     }
  //   }
  //
  //   case OrIntro(side) => extractNFormulasAndThen(refs, 1) {
  //     case List(ref) => (side, formula) match {
  //       case (Side.Left, Or(lhs, _)) => 
  //         failIf(lhs != ref, FormulaDoesntMatchReference(0, "left-hand side of formula must match reference"))
  //
  //       case (Side.Right, Or(_, rhs)) =>
  //         failIf(rhs != ref, FormulaDoesntMatchReference(0, "right-hand side of formula must match reference"))
  //
  //       case _ => List(FormulaDoesntMatchRule("must be a disjunction (or)"))
  //     }
  //   }
  //
  //   case OrElim() => 
  //     val pattern = { import BoxOrFormula._; List(Formula, Box, Box) }
  //
  //     extractAndThen(refs, pattern)  {
  //       case List(
  //         Reference.Line(r0: F),
  //         b1: Reference.Box[F, Any],
  //         b2: Reference.Box[F, Any],
  //       ) => {
  //         val (as1, cl1) = (extractFirstLine(b1), extractLastLine(b1))
  //         val (as2, cl2) = (extractFirstLine(b2), extractLastLine(b2))
  //
  //         failIf(Some(formula) != cl1, FormulaDoesntMatchReference(1, "must match last line of box")) ++
  //         failIf(Some(formula) != cl2, FormulaDoesntMatchReference(2, "must match last line of box")) ++
  //         failIf(cl1 != cl2, ReferencesMismatch(List(1, 2), "last lines of boxes must match")) ++
  //         { r0 match {
  //           case Or(lhs, rhs) =>
  //             failIf(as1 != Some(lhs), ReferencesMismatch(List(0, 1), "left-hand side must match assumption")) ++
  //             failIf(as2 != Some(rhs), ReferencesMismatch(List(0, 2), "right-hand side must match assumption"))
  //
  //           case _ => 
  //             fail(ReferenceDoesntMatchRule(0, "must be a disjunction (or)"))
  //         }}
  //       }
  //     }
  //
  //   case ImplicationIntro() => extractAndThen(refs, List(BoxOrFormula.Box)) {
  //     case List(box: Reference.Box[F, _]) => formula match {
  //       case Implies(phi, psi) =>
  //         val (ass, concl) = (extractFirstLine(box), extractLastLine(box))
  //         failIf(Some(phi) != ass, FormulaDoesntMatchReference(0, "left-hand side  must match assumption of box")) ++
  //         failIf(Some(psi) != concl, FormulaDoesntMatchReference(0, "right-hand side must match conclusion of box"))
  //
  //       case _ => 
  //         fail(FormulaDoesntMatchRule("must be an implication (->)"))
  //     }
  //   }
  //
  //   case ImplicationElim() => extractNFormulasAndThen(refs, 2) {
  //     case List(r0, r1) => r1 match {
  //       case Implies(from, to) => 
  //         failIf(from != r0, ReferencesMismatch(List(0, 1), "must match left-hand side of implication")) ++
  //         failIf(to != formula, FormulaDoesntMatchReference(1, "must match right-hand side of implication"))
  //
  //       case _ => 
  //         fail(ReferenceDoesntMatchRule(1, "must be an implication"))
  //     }
  //   }
  //
  //   case NotIntro() => extractAndThen(refs, List(BoxOrFormula.Box)) {
  //     case List(b: Box[F, ?]) => 
  //       { extractLastLine(b) match {
  //         case Some(Contradiction()) => Nil
  //         case _ => fail(ReferenceDoesntMatchRule(0, "last line of box must be contradiction"))
  //       }} ++ { 
  //         formula match {
  //           case Not(phi) => 
  //             failIf(Some(phi) != extractFirstLine(b), FormulaDoesntMatchReference(0, "must be the negation of the assumption in the box"))
  //
  //           case _ => 
  //             fail(FormulaDoesntMatchRule("must be a negation"))
  //         }
  //       }
  //   }
  //
  //   case NotElim() => extractNFormulasAndThen(refs, 2) {
  //     case List(r0, r1) =>
  //       { formula match {
  //         case Contradiction() => Nil
  //         case _ => fail(FormulaDoesntMatchRule("must be a contradiction"))
  //       }} ++ { r1 match {
  //         case Not(phi) => 
  //           failIf(r0 != phi, ReferencesMismatch(List(0, 1), "second reference must be negation of first"))
  //
  //         case _ =>
  //           fail(ReferenceDoesntMatchRule(1, "must be negation"))
  //       }}
  //   }
  //
  //   case ContradictionElim() => extractNFormulasAndThen(refs, 1) {
  //     case List(Contradiction()) => Nil
  //     case _ => fail(ReferenceDoesntMatchRule(0, "must be a contradiction"))
  //   }
  //
  //   case NotNotElim() => extractNFormulasAndThen(refs, 1) {
  //     case List(Not(Not(phi))) => 
  //       failIf(formula != phi, FormulaDoesntMatchReference(0, "must equal reference with the two outermost negations removed"))
  //
  //     case List(_) => 
  //       fail(ReferenceDoesntMatchRule(0, "must be a negation of a negation"))
  //   }
  //
  //   case ModusTollens() => extractNFormulasAndThen(refs, 2) {
  //     case List(r0, r1) => 
  //       { formula match {
  //         case Not(_) => Nil
  //         case _ => List(FormulaDoesntMatchRule("must be a negation"))
  //       }} ++ { r0 match {
  //         case Implies(_, _) => Nil
  //         case _ => List(ReferenceDoesntMatchRule(0, "must be an implication"))
  //       }} ++ { r1 match {
  //         case Not(_) => Nil
  //         case _ => List(ReferenceDoesntMatchRule(1, "must be a negation"))
  //       }} ++ { (formula, r0, r1) match {
  //         case (Not(phi2), Implies(phi1, psi1), Not(psi2)) =>
  //           failIf(phi2 != phi1, FormulaDoesntMatchReference(0, "must be negation of left-hand side of implication")) ++
  //           failIf(psi1 != psi2, ReferencesMismatch(List(0, 1), "second reference must be the negation of the right-hand side of the implication"))
  //
  //         case _ => Nil
  //       }}
  //   }
  //
  //   case NotNotIntro() => extractNFormulasAndThen(refs, 1) {
  //     case List(ref) => formula match {
  //       case Not(Not(phi)) => 
  //         failIf(phi != ref, FormulaDoesntMatchReference(0, "must equal the reference, but with two outer negations removed"))
  //
  //       case _ => 
  //         fail(FormulaDoesntMatchRule("must equal the reference, but with the two outer negations removed"))
  //     }
  //   }
  //
  //   case ProofByContradiction() => extractAndThen(refs, List(BoxOrFormula.Box)) {
  //     case List(Reference.Box(_, ass, concl)) => { 
  //       ass match {
  //         case Some(Reference.Line(Not(phi))) => 
  //           failIf(formula != phi, FormulaDoesntMatchReference(0, "must be assumption without negation"))
  //
  //         case _ => 
  //           fail(ReferenceDoesntMatchRule(0, "assumption in box must be a negation"))
  //       }
  //     } ++ { 
  //       concl match {
  //         case Some(Reference.Line(Contradiction())) => Nil
  //         case _ => fail(ReferenceDoesntMatchRule(0, "last line in box must be a contradiction"))
  //       }
  //     }
  //   }
  //
  //   case LawOfExcludedMiddle() => extractNFormulasAndThen(refs, 0) {
  //     case _ => formula match {
  //       case Or(lhs, Not(rhs)) if lhs == rhs => Nil
  //       case _ => fail(FormulaDoesntMatchRule("must be the disjunction of a formula and its negation"))
  //     }
  //   }
  //
  //   case Copy() => extractNFormulasAndThen(refs, 1) {
  //     case List(ref) => 
  //       failIf(ref != formula, FormulaDoesntMatchReference(0, "must be an exact copy of reference"))
  //   }
  }
}
