package logicbox.rule

import logicbox.framework.{Reference, Error}
import logicbox.framework.Error._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import logicbox.formula.{PredLogicLexer, PredLogicParser, PredLogicFormula}
import logicbox.rule.PredLogicRuleChecker

import logicbox.formula.PredLogicFormula._
import logicbox.formula.PredLogicTerm
import logicbox.formula.PredLogicTerm._
import logicbox.rule.PredLogicRule._
import org.scalactic.Equality
import logicbox.framework.RulePosition.Premise
import logicbox.rule.RulePart.MetaFormula
import logicbox.rule.RulePart.MetaVariable
import logicbox.framework.Location
import logicbox.framework.RulePosition.Conclusion
import logicbox.rule.RulePart.MetaTerm
import logicbox.rule.RulePart.Formulas
import logicbox.rule.RulePart.Terms
import logicbox.rule.RulePart.Vars

class PredLogicRuleCheckerTest extends AnyFunSpec {
  private val lexer = PredLogicLexer()
  private val parser = PredLogicParser()

  private type BI = FreshVarBoxInfo[PredLogicTerm.Var]

  private def parse(str: String): PredLogicFormula = parser.parseFormula(lexer(str))

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

  private val checker = PredLogicRuleChecker[PredLogicFormula, PredLogicTerm, PredLogicTerm.Var](
    PredLogicFormulaSubstitutor()
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
        ShapeMismatch(Premise(0), RulePart.ForAll(MetaVariable(Vars.X), MetaFormula(Formulas.Phi)))
      )
    }

    it("should reject if inner formula is not the same with replacement") {
      val f = parse("Q(a)")
      checker.check(ForAllElim(), f, List(refLine("forall x P(x)"))) shouldBe List(
        Ambiguous(MetaFormula(Formulas.Phi), List(
          (Conclusion, Location.root),
          (Premise(0), Location.formulaInsideQuantifier)
        ))
      )
    }

    it("should allow correct usage") {
      val f = parse("P(f(f(a)))")
      checker.check(ForAllElim(), f, List(refLine("forall x P(f(x))"))) shouldBe Nil
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
        ShapeMismatch(Conclusion, RulePart.ForAll(MetaVariable(Vars.X), MetaFormula(Formulas.Phi)))
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
          (Conclusion, Location.formulaInsideQuantifier),
          (Premise(0), Location.conclusion)
        ))
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
          (Premise(0), Location.formulaInsideQuantifier),
          (Premise(1), Location.assumption)
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
        Ambiguous(MetaFormula(Formulas.Psi), List(
          (Conclusion, Location.root),
          (Premise(1), Location.conclusion)
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
        case List(Miscellaneous(Conclusion, _)) => 
      }
    }
    
    it("should reject when first ref is not exists") {
      val f = parse("P(a)")
      val refs = List(
        refLine("forall x P(x)"),
        refBox("P(a)", "P(a)", "a")
      )
      checker.check(ExistsElim(), f, refs) shouldBe List(
        ShapeMismatch(Premise(0), RulePart.Exists(MetaVariable(Vars.X), MetaFormula(Formulas.Phi)))
      )
    }
  }

  describe("ExistsIntro") {
    it("should not allow when formula is not exists") {
      val f = parse("P(x)")
      val refs = List(refLine("P(a)"))
      checker.check(ExistsIntro(), f, refs) shouldBe List(
        ShapeMismatch(Conclusion, RulePart.Exists(MetaVariable(Vars.X), MetaFormula(Formulas.Phi)))
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
          (Conclusion, Location.formulaInsideQuantifier),
          (Premise(0), Location.root)
        ))
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
        ShapeMismatch(Conclusion, RulePart.Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T2)))
      )
    }

    it("should reject when lhs and rhs are not equal") {
      val f = parse("a = b")
      checker.check(EqualityIntro(), f, Nil) shouldBe List(
        Ambiguous(MetaTerm(Terms.T1), List(
          (Conclusion, Location.lhs),
          (Conclusion, Location.rhs)
        ))
      )
    }
  }

  describe("EqualityElim") {
    it("should reject if first reference is not equality") {
      val f = parse("P(b)")
      val refs = List(refLine("P(b)"), refLine("P(a)"))
      checker.check(EqualityElim(), f, refs) shouldBe List(
        ShapeMismatch(Premise(0), RulePart.Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T2)))
      )
    }

    it("should reject if replacement doesn't not match ref 1") {
      val eq = refLine("a = b")
      val from = refLine("P(a)")
      val to = parse("P(c)") // should be P(b)

      checker.check(EqualityElim(), to, List(eq, from)) shouldBe List(
        Ambiguous(MetaFormula(Formulas.Phi), List(
          (Conclusion, Location.root),
          (Premise(1), Location.root)
        ))
      )
    }

    it("should allow correct usage") {
      val eq = refLine("a = b")
      val from = refLine("a = a")
      val to = parse("b = a")

      checker.check(EqualityElim(), to, List(eq, from)) shouldBe Nil
    }
  }
}
