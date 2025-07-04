package logicbox.rule

import logicbox.framework._
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import org.scalatest.funspec.AnyFunSpec
import logicbox.ProofStubs
import logicbox.rule.{ReferenceBoxImpl, ReferenceLineImpl}
import logicbox.framework.Error._

class OptionRuleCheckerTest extends AnyFunSpec {
  import logicbox.ProofStubs._
  import logicbox.rule.OptionRuleChecker
  import logicbox.rule.OptionRuleChecker._

  describe("OptionRuleChecker::check") {
    val baseChecker = StubRuleChecker()
    val checker = OptionRuleChecker(baseChecker)

    it("should validate rule by calling delegate when all refs are Some's") {
      val formula = Some(StubFormula(1))
      val goodRule = Some(Good())
      val badRule = Some(Bad())
      val refs = List(
        ReferenceLineImpl(Some(StubFormula(2))),
        ReferenceBoxImpl(
          info = Some(StubBoxInfo("hello")), 
          first = Some(ReferenceLineImpl(Some(StubFormula(3)))), 
          last = Some(ReferenceLineImpl(Some(StubFormula(4))))
        )
      )

      checker.check(goodRule, formula, refs) shouldBe Nil
      baseChecker.refsCalledWith shouldBe Some(List(
        ReferenceLineImpl(StubFormula(2)),
        ReferenceBoxImpl(
          info = StubBoxInfo("hello"),
          first = Some(ReferenceLineImpl(StubFormula(3))),
          last = Some(ReferenceLineImpl(StubFormula(4)))
        )
      ))

      baseChecker.refsCalledWith = None
        
      val badResult = checker.check(badRule, formula, refs)
      badResult.length shouldBe 1
      badResult shouldBe List(ProofStubs.stubError)
    }

    it("should report missing formula") {
      val formula = None
      val rule = Some(Good())
      val refs = List(ReferenceLineImpl(Some(StubFormula(2))))

      checker.check(rule, formula, refs) shouldBe List(MissingFormula())
    }

    it("should report missing formula and rule") {
      val formula = None
      val rule = None
      val refs = List(ReferenceLineImpl(Some(StubFormula())))

      checker.check(rule, formula, refs) should contain theSameElementsAs List(
        MissingFormula(), MissingRule()
      )
    }
    
    it("should report missing rule and missing formula in ref 1") {
      val formula = Some(StubFormula())
      val rule = None
      val refs = List(
        ReferenceLineImpl(Some(StubFormula(1))),
        ReferenceLineImpl(None)
      )

      val result = checker.check(rule, formula, refs)
      result.length shouldBe 2
      result should contain (MissingRule())
      Inspectors.forExactly(1, result) {
        _ should matchPattern {
          case Miscellaneous(RulePosition.Premise(1), _) =>
        }
      }
    }

    it("should report missing info in box") {
      val formula = Some(StubFormula())
      val rule = Some(Good())
      val refs = List(
        ReferenceLineImpl(Some(StubFormula(1))),
        ReferenceBoxImpl(
          info = None,
          first = Some(ReferenceLineImpl(Some(StubFormula()))),
          last = Some(ReferenceLineImpl(Some(StubFormula()))),
        )
      )

      checker.check(rule, formula, refs) should matchPattern {
        case List(Miscellaneous(RulePosition.Premise(1), expl)) if expl.contains("box") =>
      }
    }
  }
}
