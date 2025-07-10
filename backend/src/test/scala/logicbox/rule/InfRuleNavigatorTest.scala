package logicbox.rule

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Mockito
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.*

import logicbox.framework.InfRule
import logicbox.framework.RulePart
import logicbox.framework.RulePart._
import logicbox.framework.Location

import logicbox.rule.RulePartNavigator
import org.scalatestplus.mockito.MockitoSugar
import logicbox.framework.Navigator

class InfRuleNavigatorTest extends AnyFunSpec with MockitoSugar {
  describe("get") {

    def fix = {
      val pnav = mock[Navigator[RulePart, RulePart]]
      when(pnav.get(any(), any())).thenReturn(None)
      (InfRuleNavigator(pnav), pnav)
    }

    it("should obtain the conclusion") {
      val (nav, pnav) = fix
      val f = MetaFormula(Formulas.Phi)
      val ir = InfRule(Nil, f)

      when(pnav.get(f, Location.root)).thenReturn(Some(f))

      nav.get(ir, Location.conclusion) shouldBe Some(MetaFormula(Formulas.Phi))
    }

    it("should obtain the first premise") {
      val (nav, pnav) = fix
      val f = MetaFormula(Formulas.Phi)
      val ir = InfRule(List(f), MetaFormula(Formulas.Chi))

      when(pnav.get(f, Location.root)).thenReturn(Some(f))

      nav.get(ir, Location.premise(0)) shouldBe Some(f)
    }

    it("should obtain the third premise") {
      val (nav, pnav) = fix
      val f = MetaFormula(Formulas.Phi)
      val g = MetaFormula(Formulas.Chi)
      val ir = InfRule(List(g, g, f), g)

      when(pnav.get(f, Location.root)).thenReturn(Some(f))

      nav.get(ir, Location.premise(2)) shouldBe Some(f)
    }

    it("should propagate rest of location to the rule nav in concl") {
      val (nav, pnav) = fix
      val f = And(MetaFormula(Formulas.Phi), MetaFormula(Formulas.Psi))
      val ir = InfRule(Nil, f)

      when(pnav.get(f, Location.rhs)).thenReturn(Some(f.psi))

      nav.get(ir, Location.conclusion.rhs) shouldBe Some(f.psi)
    }

    it("should propagate rest of location to the rule nav in premise") {
      val (nav, pnav) = fix
      val f = And(MetaFormula(Formulas.Phi), MetaFormula(Formulas.Psi))
      val g = MetaFormula(Formulas.Chi)
      val ir = InfRule(List(f), g)

      when(pnav.get(f, Location.rhs)).thenReturn(Some(f.psi))

      nav.get(ir, Location.premise(0).rhs) shouldBe Some(f.psi)
    }

    it("should fail in concl when part nav fails") {
      val (nav, pnav) = fix
      val f = MetaFormula(Formulas.Phi)
      val ir = InfRule(Nil, f)
      when(pnav.get(f, Location.root)).thenReturn(None)
      nav.get(ir, Location.conclusion) shouldBe None
    }

    it("should fail when index ob") {
      val (nav, pnav) = fix
      val f = MetaFormula(Formulas.Phi)
      val ir = InfRule(Nil, f)
      when(pnav.get(f, Location.root)).thenReturn(None)
      nav.get(ir, Location.premise(0)) shouldBe None
    }

    it("should fail in premise when part nav fails") {
      val (nav, pnav) = fix
      val f = MetaFormula(Formulas.Phi)
      val ir = InfRule(List(f), f)
      when(pnav.get(f, Location.root)).thenReturn(None)
      nav.get(ir, Location.premise(0)) shouldBe None
    }
    
    it("should return none when requesting root of inf rule") {
      val (nav, pnav) = fix
      val ir = InfRule(Nil, MetaFormula(Formulas.Phi))
      nav.get(ir, Location.root) shouldBe None
    }

    it("should return none when location is invalid") {
      val (nav, pnav) = fix
      val ir = InfRule(Nil, MetaFormula(Formulas.Phi))
      nav.get(ir, Location.operand(123)) shouldBe None
    }
  }
}
