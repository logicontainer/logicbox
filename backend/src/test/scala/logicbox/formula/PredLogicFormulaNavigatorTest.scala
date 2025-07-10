package logicbox.formula

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors
import logicbox.framework.Location

class PredLogicFormulaNavigatorTest extends AnyFunSpec {
  describe("get") {
    val nav = PredLogicFormulaNavigator()
    def parse(str: String) = PredLogicParser().parseFormula(PredLogicLexer()(str))
    def tparse(str: String) = PredLogicParser().parseTerm(PredLogicLexer()(str))

    it("should properly obtain the root") {
      nav.get(parse("P(a)"), Location.root) shouldBe Some(parse("P(a)"))
    }

    it("should get inside of negation") {
      nav.get(parse("not Q(a)"), Location.negated) shouldBe Some(parse("Q(a)"))
    }

    it("shoud get inside of quantifiers") {
      nav.get(parse("forall x P(x)"), Location.formulaInsideQuantifier) shouldBe Some(parse("P(x)"))
      nav.get(parse("exists x P(x)"), Location.formulaInsideQuantifier) shouldBe Some(parse("P(x)"))
    }

    it("should get arguments of predicate") {
      nav.get(parse("P(x, y, z)"), Location.operand(0)) shouldBe Some(tparse("x"))
      nav.get(parse("P(x, y, z)"), Location.operand(1)) shouldBe Some(tparse("y"))
      nav.get(parse("P(x, y, z)"), Location.operand(2)) shouldBe Some(tparse("z"))
      nav.get(parse("P(x, y, z)"), Location.operand(-1)) shouldBe None
      nav.get(parse("P(x, y, z)"), Location.operand(3)) shouldBe None
    }

    it("should get arguments of function applicatoin") {
      nav.get(parse("P(f(x, y, z))"), Location.operand(0).operand(0)) shouldBe Some(tparse("x"))
      nav.get(parse("P(f(x, y, z))"), Location.operand(0).operand(1)) shouldBe Some(tparse("y"))
      nav.get(parse("P(f(x, y, z))"), Location.operand(0).operand(2)) shouldBe Some(tparse("z"))
      nav.get(parse("P(f(x, y, z))"), Location.operand(0).operand(0)) shouldBe Some(tparse("x"))
      nav.get(parse("P(f(x, y, z))"), Location.operand(0).operand(-1)) shouldBe None
      nav.get(parse("P(f(x, y, z))"), Location.operand(0).operand(3)) shouldBe None
    }

    it("should correct get terms of equality") {
      nav.get(parse("x = y"), Location.lhs) shouldBe Some(tparse("x"))
      nav.get(parse("x = y"), Location.rhs) shouldBe Some(tparse("y"))
    }

    it("should correctly get lhs of binary connectives") {
      nav.get(parse("P(a) and Q(a)"), Location.lhs) shouldBe Some(parse("P(a)"))
      nav.get(parse("X(a) or B(a)"), Location.lhs) shouldBe Some(parse("X(a)"))
      nav.get(parse("C(a) -> D(a)"), Location.lhs) shouldBe Some(parse("C(a)"))
    }

    it("should correctly get rhs of binary connectives") {
      nav.get(parse("P(a) and Q(a)"), Location.rhs) shouldBe Some(parse("Q(a)"))
      nav.get(parse("X(a) or B(a)"), Location.rhs) shouldBe Some(parse("B(a)"))
      nav.get(parse("C(a) -> D(a)"), Location.rhs) shouldBe Some(parse("D(a)"))
    }

    it("should correct get rhs of lhs") {
      nav.get(parse("(P(a) -> Q(a)) or R(a)"), Location.lhs.rhs) shouldBe Some(parse("Q(a)"))
    }

    it("should return none when location is invalid") {
      nav.get(parse("P(a) and Q(a)"), Location.lhs.operand(0).operand(0)) shouldBe None
      nav.get(parse("P(a) and Q(a)"), Location.operand(2).lhs) shouldBe None
    }
  }
}
