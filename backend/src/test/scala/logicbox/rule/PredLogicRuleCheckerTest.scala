package logicbox.rule

import logicbox.framework.{Reference, Error}
import logicbox.framework.Error._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import logicbox.rule.PredLogicRuleChecker

import logicbox.rule.PredLogicRule._
import org.scalactic.Equality
import logicbox.framework.RulePosition.Premise
import logicbox.framework.RulePart.MetaFormula
import logicbox.framework.RulePart.MetaVariable
import logicbox.framework.Location
import logicbox.framework.RulePart.MetaTerm
import logicbox.framework.RulePart.Formulas
import logicbox.framework.RulePart.Terms
import logicbox.framework.RulePart.Vars
import logicbox.formula.Term
import logicbox.formula.FormulaKind
import logicbox.formula.Term.Var
import logicbox.formula.PredLogicTerm
import logicbox.formula.Parser
import logicbox.formula.Lexer
import logicbox.formula.PredLogicFormula
import org.scalatest.time.Microsecond

class PredLogicRuleCheckerTest extends AnyFunSpec {
  private type BI = FreshVarBoxInfo[Term.Var[FormulaKind.Pred]]

  private def parse(str: String): PredLogicFormula = Parser.parse(Lexer(str), Parser.predLogicFormula)

  private case class Line(formula: PredLogicFormula, rule: PredLogicRule, refs: List[Reference[PredLogicFormula, BI]])
    extends Reference.Line[PredLogicFormula]

  private case class Box(fst: Option[Reference[PredLogicFormula, BI]], lst: Option[Reference[PredLogicFormula, BI]], freshVar: Option[String]) extends Reference.Box[PredLogicFormula, BI] {
    override def info = FreshVarBoxInfo(freshVar.map(Var(_)))
    override def first = fst
    override def last = lst
  }

  private def refLine(str: String): Reference[PredLogicFormula, BI] = new Reference.Line[PredLogicFormula] {
    def formula = parse(str)
  }

  private def refBox(ass: String, concl: String, fresh: String = ""): Reference.Box[PredLogicFormula, BI] =
    Box(
      Some(ReferenceLineImpl(parse(ass))), 
      Some(ReferenceLineImpl(parse(concl))), 
      if fresh === "" then None else Some(fresh)
    )

  import logicbox.formula.{asConnectiveFormula, asQuantifierFormula}
  private val checker = PredLogicRuleChecker[PredLogicFormula, PredLogicTerm, Term.Var[FormulaKind.Pred]](
    FormulaSubstitutor()
  )

  describe("ForAllElim") {
    it("should reject if refs are empty") {
      val f = parse("forall x P(x)")
      checker.check(ForAllElim(), f, Nil) shouldBe List(
        WrongNumberOfReferences(1, 0)
      )
    }

    it("should reject if ref is not forall") {
      val f = parse("P(x)")
      checker.check(ForAllElim(), f, List(refLine("P(x)"))) shouldBe List(
        ShapeMismatch(Location.premise(0))
      )
    }

    it("should reject if inner formula is not the same with replacement") {
      val f = parse("Q(a)")
      checker.check(ForAllElim(), f, List(refLine("forall x P(x)"))) shouldBe List(
        Ambiguous(MetaFormula(Formulas.Phi), List(
          Location.conclusion.root,
          Location.premise(0).formulaInsideQuantifier
        ))
      )
    }

    it("should allow correct usage") {
      val f = parse("P(f(f(a)))")
      checker.check(ForAllElim(), f, List(refLine("forall x P(f(x))"))) shouldBe Nil
    }

    it("shouold reject if t is not free for x in f") {
      val f = parse("exists y P(y)")
      checker.check(ForAllElim(), f, List(refLine("forall x exists y P(x)"))) shouldBe List(
        Miscellaneous(Location.conclusion, "invalid substitution")
      )
    }
  }
  
  describe("ForAllIntro") {
    it("should reject when ref is not a box") {
      val f = parse("forall x P(x)")
      checker.check(ForAllIntro(), f, List(refLine("P(x)"))) shouldBe List(
        ReferenceShouldBeBox(0)
      )
    }

    it("should reject when box info has no variable") {
      val f = parse("forall x P(x)")
      checker.check(ForAllIntro(), f, List(refBox("P(a)", "P(a)"))) shouldBe List(
        ReferenceBoxMissingFreshVar(0)
      )
    }

    it("should reject when formula is not forall") {
      val f = parse("P(x)")
      checker.check(ForAllIntro(), f, List(refBox("P(a)", "P(a)", "a"))) shouldBe List(
        ShapeMismatch(Location.conclusion)
      )
    }

    it("should allow correct usage") {
      val f = parse("forall x P(x)")
      checker.check(ForAllIntro(), f, List(refBox("P(a)", "P(a)", "a"))) shouldBe Nil
    }

    it("should reject when last line doesn't match") {
      val f = parse("forall x P(x)")
      checker.check(ForAllIntro(), f, List(refBox("P(a)", "Q(x)", "a"))) shouldBe List(
        Ambiguous(MetaFormula(Formulas.Phi), List(
          Location.conclusion.formulaInsideQuantifier,
          Location.premise(0).lastLine
        ))
      )
    }

    it("should reject when substitution is invalid") {
      val refs = List(refBox(
        "true",
        "forall x_0 P(x_0)",
        "x_0"
      ))
      val f = parse("forall x forall x_0 P(x)")

      checker.check(ForAllIntro(), f, refs) shouldBe List(
        Miscellaneous(Location.premise(0).lastLine, "invalid substitution")
      )
    }
  }
  
  describe("ExistsElim") {
    it("should reject when first line doesn't match first ref") {
      val f = parse("false")
      val refs = List(
        refLine("exists x P(x)"),
        refBox("Q(a)", "false", "a")
      )
      checker.check(ExistsElim(), f, refs) shouldBe List(
        Ambiguous(MetaFormula(Formulas.Phi), List(
          Location.premise(0).formulaInsideQuantifier,
          Location.premise(1).firstLine
        ))
      )
    }

    it("should reject when conclusion and formula are not equal") {
      val f = parse("Q(c)")
      val refs = List(
        refLine("exists x P(x)"),
        refBox("P(a)", "Q(b)", "a")
      )
      checker.check(ExistsElim(), f, refs) shouldBe List(
        Ambiguous(MetaFormula(Formulas.Chi), List(
          Location.conclusion.root,
          Location.premise(1).lastLine
        ))
      )
    }

    it("should reject when there is no fresh variable in box") {
      val f = parse("false")
      val refs = List(
        refLine("exists x P(x)"),
        refBox("P(a)", "false")
      )
      checker.check(ExistsElim(), f, refs) shouldBe List(
        ReferenceBoxMissingFreshVar(1)
      )
    }

    it("should not allow when fresh variable is used in formula") {
      val f = parse("P(a) and P(a)")
      val refs = List(
        refLine("exists x P(x)"),
        refBox("P(a)", "P(a) and P(a)", "a")
      )
      checker.check(ExistsElim(), f, refs) should matchPattern {
        case List(Miscellaneous(Location(Location.Step.Conclusion :: Nil), _)) => 
      }
    }
    
    it("should reject when first ref is not exists") {
      val f = parse("P(a)")
      val refs = List(
        refLine("forall x P(x)"),
        refBox("P(a)", "P(a)", "a")
      )
      checker.check(ExistsElim(), f, refs) shouldBe List(
        ShapeMismatch(Location.premise(0))
      )
    }

    it("should reject if substitution is invalid") {
      val f = parse("true")
      val refs = List(
        refLine("exists x exists x_0 P(x, x_0)"),
        refBox("exists x_0 P(x_0, x_0)", "true", "x_0")
      )
      checker.check(ExistsElim(), f, refs) shouldBe List(
        Miscellaneous(Location.premise(1).firstLine, "invalid substitution"),
      )
    }
  }

  describe("ExistsIntro") {
    it("should not allow when formula is not exists") {
      val f = parse("P(x)")
      val refs = List(refLine("P(a)"))
      checker.check(ExistsIntro(), f, refs) shouldBe List(
        ShapeMismatch(Location.conclusion)
      )
    }

    it("should allow copy when no occurance of quantified variable") {
      val f = parse("exists y P(x)")
      val refs = List(refLine("P(x)"))
      checker.check(ExistsIntro(), f, refs) shouldBe Nil
    }

    it("should allow correct replacement") {
      val f = parse("exists y P(f(y))")
      val refs = List(refLine("P(f(f(x)))"))
      checker.check(ExistsIntro(), f, refs) shouldBe Nil
    }

    it("should not allow incorrect replacement") {
      val f = parse("exists y Q(y)")
      val refs = List(refLine("P(x)"))
      checker.check(ExistsIntro(), f, refs) shouldBe List(
        Ambiguous(MetaFormula(Formulas.Phi), List(
          Location.conclusion.formulaInsideQuantifier,
          Location.premise(0).root
        ))
      )
    }

    it("should reject if substitution is invalid") {
      val f = parse("exists x exists y P(x, y)")
      val refs = List(refLine("exists y P(y, y)"))
      checker.check(ExistsIntro(), f, refs) shouldBe List(
        Miscellaneous(Location.premise(0), "invalid substitution")
      )
    }
  }

  describe("EqualityIntro") {
    it("should reject when there is a reference") {
      val f = parse("a = a")
      val refs = List(refLine("a = a"))
      checker.check(EqualityIntro(), f, refs) shouldBe List(
        WrongNumberOfReferences(0, 1)
      )
    }

    it("should reject when formula is not equality") {
      val f = parse("P(a)")
      checker.check(EqualityIntro(), f, Nil) shouldBe List(
        ShapeMismatch(Location.conclusion)
      )
    }

    it("should reject when lhs and rhs are not equal") {
      val f = parse("a = b")
      checker.check(EqualityIntro(), f, Nil) shouldBe List(
        Ambiguous(MetaTerm(Terms.T), List(
          Location.conclusion.lhs,
          Location.conclusion.rhs
        ))
      )
    }
  }

  describe("EqualityElim") {
    it("should reject if first reference is not equality") {
      val f = parse("P(b)")
      val refs = List(refLine("P(b)"), refLine("P(a)"))
      checker.check(EqualityElim(), f, refs) shouldBe List(
        ShapeMismatch(Location.premise(0))
      )
    }

    it("should reject if replacement doesn't not match ref 1") {
      val eq = refLine("a = b")
      val from = refLine("P(a)")
      val to = parse("P(c)") // should be P(b)

      checker.check(EqualityElim(), to, List(eq, from)) shouldBe List(
        Ambiguous(MetaFormula(Formulas.Phi), List(
          Location.conclusion.root,
          Location.premise(1).root
        ))
      )
    }

    it("should allow correct usage") {
      val eq = refLine("a = b")
      val from = refLine("a = a")
      val to = parse("b = a")

      checker.check(EqualityElim(), to, List(eq, from)) shouldBe Nil
    }

    ignore("should reject if t1 is not free for x in phi") {
      val eq = refLine("y = z")
      val from = refLine("P(y) and forall y P(y)")
      val to = parse("P(z) and forall y P(z)")

      // TODO: right now this has another error to do with the 'equalExcept' algorithm

      checker.check(EqualityElim(), to, List(eq, from)) shouldBe List(
        Miscellaneous(Location.premise(1), "invalid substitution")
      )
    }
  }
}
