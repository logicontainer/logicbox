package logicbox.formula

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors
import logicbox.framework.Location
import logicbox.framework.Navigator

class ArithLogicFormulaNavigatorTest extends AnyFunSpec {
  describe("get") {
    val nav: Navigator[ArithLogicFormula, ArithLogicTerm | ArithLogicFormula] = FormulaNavigator()
    def parse(str: String) = ArithLogicParser().parseFormula(ArithLogicLexer()(str))
    def tparse(str: String) = ArithLogicParser().parseTerm(ArithLogicLexer()(str))

    it("should properly obtain the root") {
      nav.get(parse("a = a"), Location.root) shouldBe Some(parse("a = a"))
    }

    it("should get inside of negation") {
      nav.get(parse("not (a = a)"), Location.negated) shouldBe Some(parse("a = a"))
    }

    it("shoud get inside of quantifiers") {
      nav.get(parse("forall x x = x"), Location.formulaInsideQuantifier) shouldBe Some(parse("x = x"))
      nav.get(parse("exists x x = x"), Location.formulaInsideQuantifier) shouldBe Some(parse("x = x"))
    }

    it("should correct get terms of equality") {
      nav.get(parse("x = y"), Location.lhs) shouldBe Some(tparse("x"))
      nav.get(parse("x = y"), Location.rhs) shouldBe Some(tparse("y"))
    }

    it("should correctly get lhs of binary connectives") {
      nav.get(parse("a = a and b = b"), Location.lhs) shouldBe Some(parse("a = a"))
      nav.get(parse("a = a or b = b"), Location.lhs) shouldBe Some(parse("a = a"))
      nav.get(parse("a = a -> b = b"), Location.lhs) shouldBe Some(parse("a = a"))
    }

    it("should correctly get rhs of binary connectives") {
      nav.get(parse("a = a and b = b"), Location.rhs) shouldBe Some(parse("b = b"))
      nav.get(parse("a = a or b = b"), Location.rhs) shouldBe Some(parse("b = b"))
      nav.get(parse("a = a -> b = b"), Location.rhs) shouldBe Some(parse("b = b"))
    }

    it("should correct get rhs of lhs") {
      nav.get(parse("(a = a -> a = a) or a = a"), Location.lhs.rhs) shouldBe Some(parse("a = a"))
    }

    it("should correctly get operands of addition") {
      nav.get(parse("0 + 1 = a"), Location.lhs.lhs) shouldBe Some(tparse("0"))
      nav.get(parse("0 + 1 = a"), Location.lhs.rhs) shouldBe Some(tparse("1"))
    }

    it("should correct get operands of multiplication") {
      nav.get(parse("0 * 1 = a"), Location.lhs.lhs) shouldBe Some(tparse("0"))
      nav.get(parse("0 * 1 = a"), Location.lhs.rhs) shouldBe Some(tparse("1"))
    }

    it("should return none when location is invalid") {
      nav.get(parse("a = a and a = a"), Location.operand(2).rhs) shouldBe None
      nav.get(parse("a = a and a = a"), Location.operand(4)) shouldBe None
    }
  }
}
