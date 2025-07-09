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
      nav.get(MetaTerm(Terms.T), Location.root) shouldBe Some(MetaTerm(Terms.T))
    }

    it("should obtain lhs of binary connectives") {
      nav.get(And(MetaFormula(Formulas.Psi), MetaFormula(Formulas.Chi)), Location.lhs) shouldBe Some(MetaFormula(Formulas.Psi))
      nav.get(Or(MetaFormula(Formulas.Psi), MetaFormula(Formulas.Chi)), Location.lhs) shouldBe Some(MetaFormula(Formulas.Psi))
      nav.get(Implies(MetaFormula(Formulas.Psi), MetaFormula(Formulas.Chi)), Location.lhs) shouldBe Some(MetaFormula(Formulas.Psi))
    }

    it("should obtain rhs of binary connectives") {
      nav.get(And(MetaFormula(Formulas.Psi), MetaFormula(Formulas.Chi)), Location.rhs) shouldBe Some(MetaFormula(Formulas.Chi))
      nav.get(Or(MetaFormula(Formulas.Psi), MetaFormula(Formulas.Chi)), Location.rhs) shouldBe Some(MetaFormula(Formulas.Chi))
      nav.get(Implies(MetaFormula(Formulas.Psi), MetaFormula(Formulas.Chi)), Location.rhs) shouldBe Some(MetaFormula(Formulas.Chi))
    }

    it("should obtain inside of negation") {
      nav.get(Not(MetaFormula(Formulas.Phi)), Location.negated) shouldBe Some(MetaFormula(Formulas.Phi))
    }
    
    it("should obtain inside of forall/exists") {
      nav.get(ForAll(MetaVariable(Vars.X), MetaFormula(Formulas.Phi)), Location.formulaInsideQuantifier) shouldBe Some(MetaFormula(Formulas.Phi))
      nav.get(Exists(MetaVariable(Vars.X), MetaFormula(Formulas.Phi)), Location.formulaInsideQuantifier) shouldBe Some(MetaFormula(Formulas.Phi))
    }

    it("should obtain operands of equal") {
      nav.get(Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T2)), Location.lhs) shouldBe Some(MetaTerm(Terms.T1))
      nav.get(Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T2)), Location.rhs) shouldBe Some(MetaTerm(Terms.T2))
    }

    it("should get assumption of box") {
      nav.get(TemplateBox(Some(MetaFormula(Formulas.Phi)), None, None), Location.firstLine) shouldBe Some(MetaFormula(Formulas.Phi))
      nav.get(TemplateBox(None, None, None), Location.firstLine) shouldBe None
    }

    it("should get conclusion of box") {
      nav.get(TemplateBox(None, Some(MetaFormula(Formulas.Psi)), None), Location.lastLine) shouldBe Some(MetaFormula(Formulas.Psi))
      nav.get(TemplateBox(None, None, None), Location.lastLine) shouldBe None
    }

    it("should get operands of addition") {
      nav.get(Plus(MetaTerm(Terms.T1), Zero()), Location.lhs) shouldBe Some(MetaTerm(Terms.T1))
      nav.get(Plus(One(), MetaTerm(Terms.T2)), Location.rhs) shouldBe Some(MetaTerm(Terms.T2))
    }

    it("should get operands of multiplication") {
      nav.get(Mult(MetaTerm(Terms.T1), Zero()), Location.lhs) shouldBe Some(MetaTerm(Terms.T1))
      nav.get(Mult(One(), MetaTerm(Terms.T2)), Location.rhs) shouldBe Some(MetaTerm(Terms.T2))
    }

    it("should obtain fresh variable") {
      nav.get(TemplateBox(None, None, Some(MetaVariable(Vars.N))), Location.freshVar) shouldBe Some(MetaVariable(Vars.N))
      nav.get(TemplateBox(None, None, None), Location.freshVar) shouldBe None
    }

    it("should work with complex queries") {
      // not (phi -> (x = t1 + 1) or chi))
      val part = TemplateBox(
        Some(
          Not(Implies(MetaFormula(Formulas.Phi), Or(Equals(MetaVariable(Vars.X), Plus(MetaTerm(Terms.T1), One())), MetaFormula(Formulas.Chi))))
        ),
        None, 
        None
      )
      nav.get(part, Location.firstLine.negated.rhs.lhs.rhs.lhs) shouldBe Some(MetaTerm(Terms.T1))
    }

    it("should reject nonsense") {
      nav.get(Not(MetaFormula(Formulas.Psi)), Location.operand(2)) shouldBe None
      nav.get(Not(MetaFormula(Formulas.Psi)), Location.rhs.formulaInsideQuantifier) shouldBe None
    }
  }
}
