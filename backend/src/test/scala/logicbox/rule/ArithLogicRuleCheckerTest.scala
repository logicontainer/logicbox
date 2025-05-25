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
import logicbox.framework.RuleViolation._

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
    it("should fail if has ref") {
      val refs = List(refLine("x = x"))
      val f = parse("n + 0 = n")
      checker.check(Peano1(), f, refs) should matchPattern {
        case List(WrongNumberOfReferences(0, 1, _)) =>
      }
    }

    it("should fail if formula is not equality") {
      val f = parse("0 = 0 and n + 0 = n")
      checker.check(Peano1(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }
    
    it("should fail if rhs is not 0") {
      val f = parse("n + 1 = n")
      checker.check(Peano1(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if lhs is not addition") {
      val f = parse("n * 0 = n")
      checker.check(Peano1(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if lhs of addition and rhs of equality don't match") {
      val f = parse("n + 0 = m")
      checker.check(Peano1(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should be happy with correct usage") {
      val f = parse("n + 0 = n")
      checker.check(Peano1(), f, Nil) shouldBe Nil
    }
  }

  describe("Peano2") {
    it("should fail if has ref") {
      val refs = List(refLine("x = x"))
      val f = parse("n + (m + 1) = (n + m) + 1")
      checker.check(Peano2(), f, refs) should matchPattern {
        case List(WrongNumberOfReferences(0, 1, _)) =>
      }
    }

    it("should fail if not equality") {
      val f = parse("0 = 0 and (n + (m + 1) = (n + m) + 1)")
      checker.check(Peano2(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if lhs is not addition") {
      val f = parse("n * (m + 1) = (n + m) + 1")
      checker.check(Peano2(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if rhs is not addition") {
      val f = parse("n + (m + 1) = (n + m) * 1")
      checker.check(Peano2(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if lhs of lhs addition is not addition") {
      val f = parse("n + (m * 1) = (n + m) + 1")
      checker.check(Peano2(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if lhs of rhs addition is not addition") {
      val f = parse("n + (m + 1) = (n * m) + 1")
      checker.check(Peano2(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if lhs doesn't not have correct 1") {
      val f = parse("n + (m + 0) = (n + m) + 1")
      checker.check(Peano2(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if rhs doesn't not have correct 1") {
      val f = parse("n + (m + 1) = (n + m) + 0")
      checker.check(Peano2(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if t1's are not equal") {
      val f = parse("n + (m + 1) = (k + m) + 1")
      checker.check(Peano2(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if t2's are not equal") {
      val f = parse("n + (m + 1) = (n + k) + 1")
      checker.check(Peano2(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should allow correct usage") {
      val f = parse("n + (m + 1) = (n + m) + 1")
      checker.check(Peano2(), f, Nil) shouldBe Nil
    }
  }

  describe("Peano3") {
    it("should fail if has ref") {
      val refs = List(refLine("x = x"))
      val f = parse("n * 0 = 0")
      checker.check(Peano3(), f, refs) should matchPattern {
        case List(WrongNumberOfReferences(0, 1, _)) =>
      }
    }

    it("should fail if not equality") {
      val f = parse("0 = 0 and n * 0 = 0")
      checker.check(Peano3(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if not multiplication") {
      val f = parse("n + 0 = 0")
      checker.check(Peano3(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if not 0 on lhs") {
      val f = parse("n * 1 = 0")
      checker.check(Peano3(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if not 0 on rhs") {
      val f = parse("n * 0 = 1")
      checker.check(Peano3(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }
    
    it("should allow correct usage") {
      val f = parse("n * 0 = 0")
      checker.check(Peano3(), f, Nil) shouldBe Nil
    }
  }

  describe("Peano4") {
    it("should fail if has ref") {
      val refs = List(refLine("x = x"))
      val f = parse("n * (m + 1) = (n * m) + n")
      checker.check(Peano4(), f, refs) should matchPattern {
        case List(WrongNumberOfReferences(0, 1, _)) =>
      }
    }

    it("should fail if not equality") {
      val f = parse("0 = 0 and (n * (m * 1) = (n * m) + m)")
      checker.check(Peano4(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if lhs is not mult") {
      val f = parse("n + (m + 1) = (n * m) + n")
      checker.check(Peano4(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if rhs is not addition") {
      val f = parse("n * (m + 1) = (n * m) * n")
      checker.check(Peano4(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if rhs of lhs mult is not addition") {
      val f = parse("n * (m * 1) = (n * m) + n")
      checker.check(Peano4(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if lhs of rhs addition is not mult") {
      val f = parse("n * (m + 1) = (n + m) + n")
      checker.check(Peano4(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if lhs doesn't not have correct 1") {
      val f = parse("n * (m + 0) = (n * m) + n")
      checker.check(Peano4(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if t1's are not equal") {
      val f1 = parse("n * (m + 1) = (k * m) + n")
      checker.check(Peano4(), f1, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }

      val f2 = parse("n * (m + 1) = (n * m) + k")
      checker.check(Peano4(), f2, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if t2's are not equal") {
      val f = parse("n * (m + 1) = (n * k) + n")

      checker.check(Peano4(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
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
      checker.check(Peano5(), f, refs) should matchPattern {
        case List(WrongNumberOfReferences(0, 1, _)) =>
      }
    }

    it("should reject if not negation") {
      val f = parse("0 = 0 and not (0 = t + 1)")
      checker.check(Peano5(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should reject if inner is not equality") {
      val f = parse("not (not 0 = t + 1)")
      checker.check(Peano5(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should reject when lhs is not 0") {
      val f = parse("not (1 = t + 1)")
      checker.check(Peano5(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }
    
    it("should reject when rhs is not addition") {
      val f = parse("not (0 = t * 1)")
      checker.check(Peano5(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should reject ift rhs of addition is not 1") {
      val f = parse("not (0 = t + 0)") 
      checker.check(Peano5(), f, Nil) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should allow correct usage") {
      val f = parse("not (0 = t + 1)") 
      checker.check(Peano5(), f, Nil) shouldBe Nil
    }
  }

  describe("Peano6") {
    it("should fail if no ref") {
      val f = parse("m = n")
      checker.check(Peano6(), f, Nil) should matchPattern {
        case List(WrongNumberOfReferences(1, 0, _)) =>
      }
    }

    it("should fail if not eq") {
      val refs = List(refLine("m + 1 = n + 1"))
      val f = parse("m = n and m = n")
      checker.check(Peano6(), f, refs) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should fail if ref is not eq") {
      val refs = List(refLine("m + 1 = n + 1 and top"))
      val f = parse("m = n")
      checker.check(Peano6(), f, refs) should matchPattern {
        case List(ReferenceDoesntMatchRule(0, _)) =>
      }
    }

    it("should fail if lhs of ref is not addition") {
      val refs = List(refLine("m * 1 = n + 1"))
      val f = parse("m = n")
      checker.check(Peano6(), f, refs) should matchPattern {
        case List(ReferenceDoesntMatchRule(0, _)) =>
      }
    }

    it("should fail if rhs of ref is not addition") {
      val refs = List(refLine("m + 1 = n * 1"))
      val f = parse("m = n")
      checker.check(Peano6(), f, refs) should matchPattern {
        case List(ReferenceDoesntMatchRule(0, _)) =>
      }
    }

    it("should fail if rhs of lhs of ref is not one") {
      val refs = List(refLine("m + 0 = n + 1"))
      val f = parse("m = n")
      checker.check(Peano6(), f, refs) should matchPattern {
        case List(ReferenceDoesntMatchRule(0, _)) =>
      }
    }

    it("should fail if rhs of rhs of ref is not one") {
      val refs = List(refLine("m + 1 = n + 0"))
      val f = parse("m = n")
      checker.check(Peano6(), f, refs) should matchPattern {
        case List(ReferenceDoesntMatchRule(0, _)) =>
      }
    }

    it("should fail if t1's mismatch") {
      val refs = List(refLine("m + 1 = n + 1"))
      val f = parse("k = n")
      checker.check(Peano6(), f, refs) should matchPattern {
        case List(FormulaDoesntMatchReference(0, _)) =>
      }
    }

    it("should fail if t2's mismatch") {
      val refs = List(refLine("m + 1 = n + 1"))
      val f = parse("m = k")
      checker.check(Peano6(), f, refs) should matchPattern {
        case List(FormulaDoesntMatchReference(0, _)) =>
      }
    }
  }

  describe("Inducation") {
    it("should fail if there are no refs") {
      val f = parse("forall x 1 = 1")
      checker.check(Induction(), f, Nil) should matchPattern {
        case List(WrongNumberOfReferences(2, 0, _)) =>
      }
    }

    it("should fail if only lines as refs") {
      val refs = List(
        refLine("1 = 1"),
        refLine("1 = 1 -> 1 = 1")
      )
      val f = parse("forall x 1 = 1")
      checker.check(Induction(), f, refs) should matchPattern {
        case List(ReferenceShouldBeBox(1, _)) => 
      }
    }

    it("should fail if box has no fresh") {
      val refs = List(
        refLine("0 = 0"),
        refBox("n = n", "n + 1 = n + 1") // no fresh
      )
      val f = parse("forall x x = x")
      checker.check(Induction(), f, refs) should matchPattern {
        case List(ReferenceDoesntMatchRule(1, _)) => 
      }
    }

    it("should match if formula is not forall") {
      val refs = List(
        refLine("0 = 0"),
        refBox("n = n", "n + 1 = n + 1", "n")
      )
      val f = parse("x = x")
      checker.check(Induction(), f, refs) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) => 
      }
    }

    it("should match if first ref doesn't match inside of forall") {
      val refs = List(
        refLine("0 = 1"),
        refBox("m = m", "m + 1 = m + 1", "m")
      )
      val f = parse("forall x x = x")
      checker.check(Induction(), f, refs) should matchPattern {
        case List(FormulaDoesntMatchReference(0, _)) => 
      }
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
      checker.check(Induction(), f, refs) should matchPattern {
        case List(FormulaDoesntMatchReference(1, _)) =>
      }
    }

    it("should reject if conclusion is not formula with x replaced by n + 1") {
      val refs = List(
        refLine("0 = 0"),
        refBox("n = n", "n + 1 = n", "n")
      )
      val f = parse("forall x x = x")
      checker.check(Induction(), f, refs) should matchPattern {
        case List(FormulaDoesntMatchReference(1, _)) =>
      }
    }

    it("should reject if conclusion is formula with n + 0 not n + 1") {
      val refs = List(
        refLine("0 = 0"),
        refBox("n = n", "n + 0 = n + 0", "n")
      )
      val f = parse("forall x x = x")
      checker.check(Induction(), f, refs) should matchPattern {
        case List(FormulaDoesntMatchReference(1, _)) =>
      }
    }

    it("should fail if assumption of box is not a line") {
      val refs = List(
        refLine("0 = 0"),
        Box(None, Some(refLine("n + 1 = n + 1")), Some("n"))
      )
      val f = parse("forall x x = x")
      checker.check(Induction(), f, refs) should matchPattern {
        case List(ReferenceDoesntMatchRule(1, _)) =>
      }
    }

    it("should fail if conclusion of box is not a line") {
      val refs = List(
        refLine("0 = 0"),
        Box(Some(refLine("n = n")), None, Some("n"))
      )
      val f = parse("forall x x = x")
      checker.check(Induction(), f, refs) should matchPattern {
        case List(ReferenceDoesntMatchRule(1, _)) =>
      }
    }
  }
}
