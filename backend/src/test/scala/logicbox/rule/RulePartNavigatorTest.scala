package logicbox.rule

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors
import logicbox.framework.Location
import logicbox.rule.RulePart._

class RulePartNavigatorTest extends AnyFunSpec {
  describe("get") {
    val nav = RulePartNavigator()
    it("should correctly obtain root") {
      nav.get(MetaTerm(1420), Location.root) shouldBe Some(MetaTerm(1420))
    }

    it("should obtain lhs of binary connectives") {
      nav.get(And(MetaFormula(1), MetaFormula(2)), Location.lhs) shouldBe Some(MetaFormula(1))
      nav.get(Or(MetaFormula(1), MetaFormula(2)), Location.lhs) shouldBe Some(MetaFormula(1))
      nav.get(Implies(MetaFormula(1), MetaFormula(2)), Location.lhs) shouldBe Some(MetaFormula(1))
    }

    it("should obtain rhs of binary connectives") {
      nav.get(And(MetaFormula(1), MetaFormula(2)), Location.rhs) shouldBe Some(MetaFormula(2))
      nav.get(Or(MetaFormula(1), MetaFormula(2)), Location.rhs) shouldBe Some(MetaFormula(2))
      nav.get(Implies(MetaFormula(1), MetaFormula(2)), Location.rhs) shouldBe Some(MetaFormula(2))
    }

    it("should obtain inside of negation") {
      nav.get(Not(MetaFormula(0)), Location.negated) shouldBe Some(MetaFormula(0))
    }
    
    it("should obtain inside of forall/exists") {
      nav.get(ForAll(MetaVariable(0), MetaFormula(0)), Location.formulaInsideQuantifier) shouldBe Some(MetaFormula(0))
      nav.get(Exists(MetaVariable(0), MetaFormula(0)), Location.formulaInsideQuantifier) shouldBe Some(MetaFormula(0))
    }

    it("should obtain operands of equal") {
      nav.get(Equals(MetaTerm(0), MetaTerm(1)), Location.lhs) shouldBe Some(MetaTerm(0))
      nav.get(Equals(MetaTerm(0), MetaTerm(1)), Location.rhs) shouldBe Some(MetaTerm(1))
    }

    it("should get assumption of box") {
      nav.get(TemplateBox(Some(MetaFormula(0)), None, None), Location.assumption) shouldBe Some(MetaFormula(0))
      nav.get(TemplateBox(None, None, None), Location.assumption) shouldBe None
    }

    it("should get conclusion of box") {
      nav.get(TemplateBox(None, Some(MetaFormula(1)), None), Location.conclusion) shouldBe Some(MetaFormula(1))
      nav.get(TemplateBox(None, None, None), Location.assumption) shouldBe None
    }

    it("should get operands of addition") {
      nav.get(Plus(MetaTerm(0), Zero()), Location.lhs) shouldBe Some(MetaTerm(0))
      nav.get(Plus(One(), MetaTerm(1)), Location.rhs) shouldBe Some(MetaTerm(1))
    }

    it("should get operands of multiplication") {
      nav.get(Mult(MetaTerm(0), Zero()), Location.lhs) shouldBe Some(MetaTerm(0))
      nav.get(Mult(One(), MetaTerm(1)), Location.rhs) shouldBe Some(MetaTerm(1))
    }

    it("should obtain fresh variable") {
      nav.get(TemplateBox(None, None, Some(MetaVariable(4))), Location.freshVar) shouldBe Some(MetaVariable(4))
      nav.get(TemplateBox(None, None, None), Location.freshVar) shouldBe None
    }

    it("should work with complex queries") {
      // not (phi -> (x = t1 + 1) or chi))
      val part = TemplateBox(
        Some(
          Not(Implies(MetaFormula(0), Or(Equals(MetaVariable(0), Plus(MetaTerm(0), One())), MetaFormula(2))))
        ),
        None, 
        None
      )
      nav.get(part, Location.assumption.negated.rhs.lhs.rhs.lhs) shouldBe Some(MetaTerm(0))
    }

    it("should reject nonsense") {
      nav.get(Not(MetaFormula(1)), Location.operand(2)) shouldBe None
      nav.get(Not(MetaFormula(1)), Location.rhs.formulaInsideQuantifier) shouldBe None
    }
  }
}
