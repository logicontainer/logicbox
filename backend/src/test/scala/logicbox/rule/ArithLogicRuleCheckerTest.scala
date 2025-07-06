package logicbox.rule

import org.scalatest.funspec.AnyFunSpec

import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import logicbox.formula.ArithLogicTerm
import logicbox.formula.ArithLogicFormula
import logicbox.formula.ArithLogicParser
import logicbox.formula.ArithLogicLexer
import logicbox.framework._
import logicbox.rule.ArithLogicRule._
import logicbox.framework.Error._
import logicbox.rule.RulePart._
import logicbox.framework.RulePosition.Conclusion
import logicbox.framework.RulePosition.Premise

class ArithLogicRuleCheckerTest extends AnyFunSpec {
  def parse(str: String): ArithLogicFormula = {
    ArithLogicParser().parseFormula(ArithLogicLexer()(str))
  }

  val checker = ArithLogicRuleChecker[
    ArithLogicFormula,
    ArithLogicTerm,
    ArithLogicTerm.Var
  ](ArithLogicFormulaSubstitutor())

  private type BI = FreshVarBoxInfo[ArithLogicTerm.Var]

  private case class Line(formula: ArithLogicFormula, rule: ArithLogicRule, refs: List[Reference[ArithLogicFormula, BI]])
    extends Reference.Line[ArithLogicFormula]

  private case class Box(fst: Option[Reference[ArithLogicFormula, BI]], lst: Option[Reference[ArithLogicFormula, BI]], freshVar: Option[String]) extends Reference.Box[ArithLogicFormula, BI] {
    override def info = FreshVarBoxInfo(freshVar.map(ArithLogicTerm.Var(_)))
    override def first = fst
    override def last = lst
  }

  private def refLine(str: String): Reference[ArithLogicFormula, BI] = new Reference.Line[ArithLogicFormula] {
    def formula = parse(str)
  }

  private def refBox(ass: String, concl: String, fresh: String = ""): Reference.Box[ArithLogicFormula, BI] =
    Box(
      Some(ReferenceLineImpl(parse(ass))), 
      Some(ReferenceLineImpl(parse(concl))), 
      if fresh === "" then None else Some(fresh)
    )

  describe("Peano1") {
    val formulaShape = Equals(Plus(MetaTerm(Terms.T), Zero()), MetaTerm(Terms.T))
    it("should fail if has ref") {
      val refs = List(refLine("x = x"))
      val f = parse("n + 0 = n")
      checker.check(Peano1(), f, refs) shouldBe List(
        WrongNumberOfReferences(0, 1)
      )
    }

    it("should fail if formula is not equality") {
      val f = parse("0 = 0 and n + 0 = n")
      checker.check(Peano1(), f, Nil) shouldBe List(
        ShapeMismatch(Conclusion)
      )
    }

    it("should fail if rhs is not 0") {
      val f = parse("n + 1 = n")
      checker.check(Peano1(), f, Nil) shouldBe List(
        ShapeMismatch(Conclusion)
      )
    }

    it("should fail if lhs of addition and rhs of equality don't match") {
      val f = parse("n + 0 = m")
      checker.check(Peano1(), f, Nil) shouldBe List(
        Ambiguous(MetaTerm(Terms.T), List(
          (Conclusion, Location.lhs.lhs),
          (Conclusion, Location.rhs)
        ))
      )
    }
  }

  describe("Peano2") {
    it("should fail if has ref") {
      val refs = List(refLine("x = x"))
      val f = parse("n + (m + 1) = (n + m) + 1")
      checker.check(Peano2(), f, refs) shouldBe List(
        WrongNumberOfReferences(0, 1)
      )
    }

    val formulaShape = Equals(
      Plus(MetaTerm(Terms.T1), Plus(MetaTerm(Terms.T2), One())),
      Plus(Plus(MetaTerm(Terms.T1), MetaTerm(Terms.T2)), One())
    )
    it("should fail if not equality") {
      val f = parse("0 = 0 and (n + (m + 1) = (n + m) + 1)")
      checker.check(Peano2(), f, Nil) shouldBe List(
        ShapeMismatch(Conclusion)
      )
    }

    it("should fail if lhs is not addition") {
      val f = parse("n * (m + 1) = (n + m) + 1")
      checker.check(Peano2(), f, Nil) shouldBe List(
        ShapeMismatch(Conclusion)
      )
    }

    it("should fail if t1's are not equal") {
      val f = parse("n + (m + 1) = (k + m) + 1")
      checker.check(Peano2(), f, Nil) shouldBe List(
        Ambiguous(MetaTerm(Terms.T1), List(
          (Conclusion, Location.lhs.lhs),
          (Conclusion, Location.rhs.lhs.lhs)
        ))
      )
    }

    it("should fail if t2's are not equal") {
      val f = parse("n + (m + 1) = (n + k) + 1")
      checker.check(Peano2(), f, Nil) shouldBe List(
        Ambiguous(MetaTerm(Terms.T2), List(
          (Conclusion, Location.lhs.rhs.lhs),
          (Conclusion, Location.rhs.lhs.rhs)
        ))
      )
    }

    it("should allow correct usage") {
      val f = parse("n + (m + 1) = (n + m) + 1")
      checker.check(Peano2(), f, Nil) shouldBe Nil
    }
  }
  
  describe("Peano3") {
    val formulaShape = Equals(Mult(MetaTerm(Terms.T), Zero()), Zero())
    it("should fail if has ref") {
      val refs = List(refLine("x = x"))
      val f = parse("n * 0 = 0")
      checker.check(Peano3(), f, refs) shouldBe List(
        WrongNumberOfReferences(0, 1)
      )
    }

    it("should fail if not equality") {
      val f = parse("0 = 0 and n * 0 = 0")
      checker.check(Peano3(), f, Nil) shouldBe List(
        ShapeMismatch(Conclusion)
      )
    }

    it("should fail if not multiplication") {
      val f = parse("n + 0 = 0")
      checker.check(Peano3(), f, Nil) shouldBe List(
        ShapeMismatch(Conclusion)
      )
    }
    
    it("should allow correct usage") {
      val f = parse("n * 0 = 0")
      checker.check(Peano3(), f, Nil) shouldBe Nil
    }
  }
  
  describe("Peano4") {
    val formulaShape = Equals(
      Mult(MetaTerm(Terms.T1), Plus(MetaTerm(Terms.T2), One())),
      Plus(Mult(MetaTerm(Terms.T1), MetaTerm(Terms.T2)), MetaTerm(Terms.T1))
    )

    it("should fail if has ref") {
      val refs = List(refLine("x = x"))
      val f = parse("n * (m + 1) = (n * m) + n")
      checker.check(Peano4(), f, refs) shouldBe List(
        WrongNumberOfReferences(0, 1)
      )
    }

    it("should fail if not equality") {
      val f = parse("0 = 0 and (n * (m * 1) = (n * m) + m)")
      checker.check(Peano4(), f, Nil) shouldBe List(
        ShapeMismatch(Conclusion)
      )
    }

    it("should fail if lhs is not mult") {
      val f = parse("n + (m + 1) = (n * m) + n")
      checker.check(Peano4(), f, Nil) shouldBe List(
        ShapeMismatch(Conclusion)
      )
    }

    it("should fail if lhs doesn't not have correct 1") {
      val f = parse("n * (m + 0) = (n * m) + n")
      checker.check(Peano4(), f, Nil) shouldBe List(
        ShapeMismatch(Conclusion)
      )
    }

    it("should fail if t1's are not equal") {
      val f1 = parse("n * (m + 1) = (k * m) + n")
      checker.check(Peano4(), f1, Nil) shouldBe List(
        Ambiguous(MetaTerm(Terms.T1), List(
          (Conclusion, Location.lhs.lhs),
          (Conclusion, Location.rhs.lhs.lhs),
          (Conclusion, Location.rhs.rhs)
        ))
      )

      val f2 = parse("n * (m + 1) = (n * m) + k")
      checker.check(Peano4(), f2, Nil) shouldBe List(
        Ambiguous(MetaTerm(Terms.T1), List(
          (Conclusion, Location.lhs.lhs),
          (Conclusion, Location.rhs.lhs.lhs),
          (Conclusion, Location.rhs.rhs)
        ))
      )
    }

    it("should fail if t2's are not equal") {
      val f = parse("n * (m + 1) = (n * k) + n")

      checker.check(Peano4(), f, Nil) shouldBe List(
        Ambiguous(MetaTerm(Terms.T2), List(
          (Conclusion, Location.lhs.rhs.lhs),
          (Conclusion, Location.rhs.lhs.rhs)
        ))
      )
    }

    it("should allow correct usage") {
      val f = parse("n * (m + 1) = (n * m) + n")
      checker.check(Peano4(), f, Nil) shouldBe Nil
    }
  }
  
  describe("Peano5") {
    it("should fail if has ref") {
      val refs = List(refLine("x = x"))
      val f = parse("not (0 = t + 1)")
      checker.check(Peano5(), f, refs) shouldBe List(
        WrongNumberOfReferences(0, 1)
      )
    }
    
    val formulaShape = Not(Equals(Zero(), Plus(MetaTerm(Terms.T), One())))
    it("should reject if not negation") {
      val f = parse("0 = 0 and not (0 = t + 1)")
      checker.check(Peano5(), f, Nil) shouldBe List(
        ShapeMismatch(Conclusion)
      )
    }

    it("should reject if inner is not equality") {
      val f = parse("not (not 0 = t + 1)")
      checker.check(Peano5(), f, Nil) shouldBe List(
        ShapeMismatch(Conclusion)
      )
    }

    it("should reject when lhs is not 0") {
      val f = parse("not (1 = t + 1)")
      checker.check(Peano5(), f, Nil) shouldBe List(
        ShapeMismatch(Conclusion)
      )
    }

    it("should reject when rhs is not addition") {
      val f = parse("not (0 = t * 1)")
      checker.check(Peano5(), f, Nil) shouldBe List(
        ShapeMismatch(Conclusion)
      )
    }

    it("should reject ift rhs of addition is not 1") {
      val f = parse("not (0 = t + 0)") 
      checker.check(Peano5(), f, Nil) shouldBe List(
        ShapeMismatch(Conclusion)
      )
    }

    it("should allow correct usage") {
      val f = parse("not (0 = t + 1)") 
      checker.check(Peano5(), f, Nil) shouldBe Nil
    }
  }
  
  describe("Peano6") {
    it("should fail if no ref") {
      val f = parse("m = n")
      checker.check(Peano6(), f, Nil) shouldBe List(
        WrongNumberOfReferences(1, 0)
      )
    }
    val refShape = Equals(Plus(MetaTerm(Terms.T1), One()), Plus(MetaTerm(Terms.T2), One()))
    val formulaShape = Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T2))
    it("should fail if not eq") {
      val refs = List(refLine("m + 1 = n + 1"))
      val f = parse("m = n and m = n")
      checker.check(Peano6(), f, refs) shouldBe List(
        ShapeMismatch(Conclusion)
      )
    }

    it("should fail if ref is not eq") {
      val refs = List(refLine("m + 1 = n + 1 and top"))
      val f = parse("m = n")
      checker.check(Peano6(), f, refs) shouldBe List(
        ShapeMismatch(Premise(0))
      )
    }

    it("should fail if t1's mismatch") {
      val refs = List(refLine("m + 1 = n + 1"))
      val f = parse("k = n")
      checker.check(Peano6(), f, refs) shouldBe List(
        Ambiguous(MetaTerm(Terms.T1), List(
          (Conclusion, Location.lhs),
          (Premise(0), Location.lhs.lhs)
        ))
      )
    }

    it("should fail if t2's mismatch") {
      val refs = List(refLine("m + 1 = n + 1"))
      val f = parse("m = k")
      checker.check(Peano6(), f, refs) shouldBe List(
        Ambiguous(MetaTerm(Terms.T2), List(
          (Conclusion, Location.rhs),
          (Premise(1), Location.rhs.lhs)
        ))
      )
    }

    it("should allow correct usage") {
      val refs = List(refLine("m + 1 = n + 1"))
      val f = parse("m = n")
      checker.check(Peano6(), f, refs) shouldBe Nil
    }
  }
  
  describe("Inducation") {
    it("should fail if there are no refs") {
      val f = parse("forall x 1 = 1")
      checker.check(Induction(), f, Nil) shouldBe List(
        WrongNumberOfReferences(2, 0)
      )
    }

    it("should fail if only lines as refs") {
      val refs = List(
        refLine("1 = 1"),
        refLine("1 = 1 -> 1 = 1")
      )
      val f = parse("forall x 1 = 1")
      checker.check(Induction(), f, refs) shouldBe List(
        ReferenceShouldBeBox(1)
      )
    }

    it("should fail if box has no fresh") {
      val refs = List(
        refLine("0 = 0"),
        refBox("n = n", "n + 1 = n + 1") // no fresh
      )
      val f = parse("forall x x = x")
      checker.check(Induction(), f, refs) shouldBe List(
        ReferenceBoxMissingFreshVar(1)
      )
    }

    it("should match if formula is not forall") {
      val refs = List(
        refLine("0 = 0"),
        refBox("n = n", "n + 1 = n + 1", "n")
      )
      val f = parse("x = x")
      checker.check(Induction(), f, refs) shouldBe List(
        ShapeMismatch(Conclusion)
      )
    }

    it("should match if first ref doesn't match inside of forall") {
      val refs = List(
        refLine("0 = 1"),
        refBox("m = m", "m + 1 = m + 1", "m")
      )
      val f = parse("forall x x = x")
      checker.check(Induction(), f, refs) shouldBe List(
        Ambiguous(MetaFormula(Formulas.Phi), List(
          (Conclusion, Location.formulaInsideQuantifier),
          (Premise(0), Location.root),
          (Premise(1), Location.assumption),
          (Premise(1), Location.conclusion)
        ))
      )
    }

    it("should be fine if first ref matches formula but there is no occ. of x") {
      val refs = List(
        refLine("0 = 0"),
        refBox("0 = 0", "0 = 0", "m")
      )
      val f = parse("forall x 0 = 0")
      checker.check(Induction(), f, refs) shouldBe Nil
    }

    it("should reject if assumption is not formula with x replaced by n") {
      val refs = List(
        refLine("0 = 0"),
        refBox("m = m", "n + 1 = n + 1", "n")
      )
      val f = parse("forall x x = x")
      checker.check(Induction(), f, refs) shouldBe List(
        Ambiguous(MetaFormula(Formulas.Phi), List(
          (Conclusion, Location.formulaInsideQuantifier),
          (Premise(0), Location.root),
          (Premise(1), Location.assumption),
          (Premise(1), Location.conclusion)
        ))
      )
    }

    it("should reject if conclusion is not formula with x replaced by n + 1") {
      val refs = List(
        refLine("0 = 0"),
        refBox("n = n", "n + 1 = n", "n")
      )
      val f = parse("forall x x = x")
      checker.check(Induction(), f, refs) shouldBe List(
        Ambiguous(MetaFormula(Formulas.Phi), List(
          (Conclusion, Location.formulaInsideQuantifier),
          (Premise(0), Location.root),
          (Premise(1), Location.assumption),
          (Premise(1), Location.conclusion)
        ))
      )
    }

    it("should reject if conclusion is formula with n + 0 not n + 1") {
      val refs = List(
        refLine("0 = 0"),
        refBox("n = n", "n + 0 = n + 0", "n")
      )
      val f = parse("forall x x = x")
      checker.check(Induction(), f, refs) shouldBe List(
        Ambiguous(MetaFormula(Formulas.Phi), List(
          (Conclusion, Location.formulaInsideQuantifier),
          (Premise(0), Location.root),
          (Premise(1), Location.assumption),
          (Premise(1), Location.conclusion)
        ))
      )
    }

    it("should fail if assumption of box is not a line") {
      val refs = List(
        refLine("0 = 0"),
        Box(None, Some(refLine("n + 1 = n + 1")), Some("n"))
      )
      val f = parse("forall x x = x")
      checker.check(Induction(), f, refs) should matchPattern {
        case List(Miscellaneous(Premise(1), _)) =>
      }
    }

    it("should fail if conclusion of box is not a line") {
      val refs = List(
        refLine("0 = 0"),
        Box(Some(refLine("n = n")), None, Some("n"))
      )
      val f = parse("forall x x = x")
      checker.check(Induction(), f, refs) should matchPattern {
        case List(Miscellaneous(Premise(1), _)) =>
      }
    }
  }
}
