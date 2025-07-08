package logicbox.server

import org.scalatest.funspec.AnyFunSpec

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors
import logicbox.framework.Navigator
import logicbox.framework.Location
import logicbox.framework.Error
import logicbox.framework.RulePosition.Conclusion
import logicbox.rule.RulePart
import logicbox.rule.RulePart.MetaFormula
import logicbox.rule.RulePart.Formulas
import logicbox.server.format.OutputError
import logicbox.framework.RulePosition
import logicbox.framework.RulePosition.Premise
import logicbox.rule.RulePart.MetaTerm
import logicbox.rule.RulePart.Terms
import logicbox.rule.RulePart.MetaVariable
import logicbox.rule.RulePart.Vars
import logicbox.rule.RulePart.TemplateBox
import logicbox.framework.Location.freshVar
import logicbox.proof.ProofNavigator
import logicbox.rule.RulePartNavigator

class ErrorConverterImplTest extends AnyFunSpec {
  import logicbox.ProofStubs._

  val NAV_FAIL = 512502
  class StubFormulaNav extends Navigator[StubFormula, StubFormula] {
    override def get(subject: StubFormula, loc: Location): Option[StubFormula] = {
      if subject.i == NAV_FAIL then None else
      Some(StubFormula(subject.i + loc.steps.sum))
    }
  }

  class BoxNav extends Navigator[StubBoxInfo, StubFormula] {
    override def get(subject: StubBoxInfo, loc: Location): Option[StubFormula] = ???
  }

  def stubFormulaToString(f: StubFormula): String = s"sf(${f.i.toString})"
  def rulePartToLaTeX(r: RulePart): String = s"--${r.toString}"

  var ruleMap = Map[RulePosition, RulePart]()
  def stubGetRulePart(rule: StubRule, rulePos: RulePosition): Option[RulePart] = ruleMap.get(rulePos)

  describe("convert") {
    val cvtr = ErrorConverterImpl[F, R, B](
      stubGetRulePart, 
      stubFormulaToString, 
      rulePartToLaTeX,
      ProofNavigator(
        StubFormulaNav(),
        BoxNav()
      ),
      RulePartNavigator()
    )
    it("should convert shape mismatch on formula") {
      val pf = StubProof(
        rootSteps = Seq("l1"),
        map = Map(
          "l1" -> StubLine(StubFormula(2))
        )
      )

      val part = RulePart.Equals(MetaVariable(Vars.X), MetaVariable(Vars.X))
      ruleMap = ruleMap + (Conclusion -> part)

      cvtr.convert(pf, "l1", Error.ShapeMismatch(Conclusion, Location.root)) shouldBe Some(OutputError.ShapeMismatch(
        uuid = "l1",
        rulePosition = "conclusion",
        expected = s"--${part.toString}",
        actual = "sf(2)"
      ))
    }

    it("should convert shape mismatch on formula 2") {
      val pf = StubProof(
        rootSteps = Seq("l2"),
        map = Map(
          "l2" -> StubLine(StubFormula(3))
        )
      )

      val part = RulePart.Equals(MetaTerm(Terms.T), MetaTerm(Terms.T))
      ruleMap = ruleMap + (Conclusion -> part)

      cvtr.convert(pf, "l2", Error.ShapeMismatch(Conclusion, Location.root)) shouldBe Some(OutputError.ShapeMismatch(
        uuid = "l2",
        rulePosition = "conclusion",
        expected = s"--${part.toString}",
        actual = "sf(3)"
      ))
    }

    it("should convert shape mismatch on ref 0") {
      val pf = StubProof(
        rootSteps = Seq("l1", "ref"),
        map = Map(
          "ref" -> StubLine(StubFormula(1)),
          "l1" -> StubLine(StubFormula(2), Good(), Seq("ref"))
        )
      )

      val part = RulePart.Equals(MetaTerm(Terms.T), MetaTerm(Terms.T))
      ruleMap = ruleMap + (Premise(0) -> part)

      cvtr.convert(pf, "l1", Error.ShapeMismatch(Premise(0), Location.root)) shouldBe Some(OutputError.ShapeMismatch(
        uuid = "l1",
        rulePosition = "premise 0",
        expected = s"--${part.toString}",
        actual = "sf(1)"
      ))
    }

    it("should convert shape mismatch on ref 1") {
      val pf = StubProof(
        rootSteps = Seq("l1", "ref"),
        map = Map(
          "r0" -> StubLine(StubFormula(1)),
          "r1" -> StubLine(StubFormula(2)),
          "l1" -> StubLine(StubFormula(2), Good(), Seq("r0", "r1"))
        )
      )

      val part = RulePart.Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T1))
      ruleMap = ruleMap + (Premise(1) -> part)

      cvtr.convert(pf, "l1", Error.ShapeMismatch(Premise(1), Location.root)) shouldBe Some(OutputError.ShapeMismatch(
        uuid = "l1",
        rulePosition = "premise 1",
        expected = s"--${part.toString}",
        actual = "sf(2)"
      ))
    }

    it("should convert shape mismatch on assumption of box") {
      val pf = StubProof(
        rootSteps = Seq("box"),
        map = Map(
          "ass" -> StubLine(StubFormula(123)),
          "concl" -> StubLine(StubFormula(456)),
          "box" -> StubBox(StubBoxInfo(), Seq("ass", "concl")),
          "line" -> StubLine(StubFormula(), Good(), Seq("box"))
        )
      )

      val assRulePart = RulePart.Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T2))
      val part = TemplateBox(ass = Some(assRulePart), concl = None, freshVar = None)
      ruleMap = ruleMap + (Premise(0) -> part)

      cvtr.convert(pf, "line", Error.ShapeMismatch(Premise(0), Location.assumption)) shouldBe Some(OutputError.ShapeMismatch(
        uuid = "line",
        rulePosition = "premise 0",
        expected = s"--${assRulePart.toString}",
        actual = "sf(123)"
      ))
    }

    it("should return none when rule part location is not valid") {
      val pf = StubProof(
        rootSteps = Seq("box"),
        map = Map(
          "ass" -> StubLine(StubFormula(123)),
          "concl" -> StubLine(StubFormula(456)),
          "box" -> StubBox(StubBoxInfo(), Seq("ass", "concl")),
          "line" -> StubLine(StubFormula(), Good(), Seq("box"))
        )
      )

      // rule is t1 = t1
      val part = RulePart.Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T1))
      ruleMap = ruleMap + (Conclusion -> part)

      // but we ask for operand 3
      cvtr.convert(pf, "line", Error.ShapeMismatch(Premise(0), Location.operand(3))) shouldBe None
    }

    it("should return none when formula nav fails") {
      val pf = StubProof(
        rootSteps = Seq("line"),
        map = Map(
          "line" -> StubLine(StubFormula(NAV_FAIL))
        )
      )

      val part = RulePart.Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T1))
      ruleMap = ruleMap + (Conclusion -> part)

      cvtr.convert(pf, "line", Error.ShapeMismatch(Conclusion, Location.root)) shouldBe None
    }
    
    it("should return none if id is invalid") {
      val pf = StubProof()

      val part = RulePart.Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T1))
      ruleMap = ruleMap + (Premise(0) -> part)

      cvtr.convert(pf, "invalid_id", Error.ShapeMismatch(Conclusion, Location.root)) shouldBe None
    }

    it("should return none when try to get ref of box") {
      val pf = StubProof(
        Seq("box"), Map("box" -> StubBox())
      )

      val part = RulePart.Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T1))
      ruleMap = ruleMap + (Premise(0) -> part)

      cvtr.convert(pf, "box", Error.ShapeMismatch(Premise(0), Location.root)) shouldBe None
    }
  }
}
