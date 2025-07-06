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

class ErrorConverterImplTest extends AnyFunSpec {
  import logicbox.ProofStubs._

  class StubFormulaNav extends Navigator[StubFormula, StubFormula] {
    override def get(subject: StubFormula, loc: Location): Option[StubFormula] = 
      Some(StubFormula(subject.i + loc.steps.sum))
  }

  def stubFormulaToString(f: StubFormula): String = s"sf(${f.i.toString})"
  def rulePartToLaTeX(r: RulePart): String = s"--${r.toString}"

  var ruleMap = Map[RulePosition, RulePart]()
  def stubGetRulePart(rule: StubRule, rulePos: RulePosition): Option[RulePart] = ruleMap.get(rulePos)

  describe("convert") {
    val cvtr = ErrorConverterImpl[F, R, B](stubGetRulePart, stubFormulaToString, rulePartToLaTeX)
    it("should convert shape mismatch on formula") {
      val pf = StubProof(
        rootSteps = Seq("l1"),
        map = Map(
          "l1" -> StubLine(StubFormula(2))
        )
      )

      val part = RulePart.Equals(MetaVariable(Vars.X), MetaVariable(Vars.X))
      ruleMap = ruleMap + (Conclusion -> part)

      cvtr.convert(pf, "l1", Error.ShapeMismatch(Conclusion, Location.root)) shouldBe OutputError.ShapeMismatch(
        uuid = "l1",
        rulePosition = "conclusion",
        expected = s"--${part.toString}",
        actual = "sf(2)"
      )
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

      cvtr.convert(pf, "l2", Error.ShapeMismatch(Conclusion, Location.root)) shouldBe OutputError.ShapeMismatch(
        uuid = "l2",
        rulePosition = "conclusion",
        expected = s"--${part.toString}",
        actual = "sf(3)"
      )
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
      ruleMap = ruleMap + (Conclusion -> part)

      cvtr.convert(pf, "l1", Error.ShapeMismatch(Premise(0), Location.root)) shouldBe OutputError.ShapeMismatch(
        uuid = "l1",
        rulePosition = "premise 0",
        expected = s"--${part.toString}",
        actual = "sf(1)"
      )
    }
  }
}
