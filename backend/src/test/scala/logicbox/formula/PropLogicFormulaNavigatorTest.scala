package logicbox.formula

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors
import logicbox.framework.Location

class PropLogicFormulaNavigatorTest extends AnyFunSpec {
  describe("get") {
    val nav = FormulaNavigator[FormulaKind.Prop]()
    def parse(str: String) = PropLogicParser()(PropLogicLexer()(str))

    it("should properly obtain the root") {
      nav.get(parse("p"), Location.root) shouldBe Some(parse("p"))
    }

    it("should get inside of negation") {
      nav.get(parse("not q"), Location.negated) shouldBe Some(parse("q"))
    }

    it("should correctly get lhs of binary connectives") {
      nav.get(parse("p and q"), Location.lhs) shouldBe Some(parse("p"))
      nav.get(parse("a or b"), Location.lhs) shouldBe Some(parse("a"))
      nav.get(parse("c -> d"), Location.lhs) shouldBe Some(parse("c"))
    }

    it("should correctly get rhs of binary connectives") {
      nav.get(parse("p and q"), Location.rhs) shouldBe Some(parse("q"))
      nav.get(parse("a or b"), Location.rhs) shouldBe Some(parse("b"))
      nav.get(parse("c -> d"), Location.rhs) shouldBe Some(parse("d"))
    }

    it("should correct get rhs of lhs") {
      nav.get(parse("(p -> q) or r"), Location.lhs.rhs) shouldBe Some(parse("q"))
    }

    it("should return none when location is invalid") {
      nav.get(parse("p and q"), Location.lhs.lhs) shouldBe None
      nav.get(parse("p and q"), Location.operand(2).lhs) shouldBe None
    }
  }
}
