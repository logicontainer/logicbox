package logicbox.proof

import logicbox.framework._
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import org.scalatest.funspec.AnyFunSpec
import logicbox.framework.Proof.Step
import logicbox.ProofStubs

import logicbox.framework.Error
import logicbox.framework.Error._

class StructuralProofCheckerTest extends AnyFunSpec {
  import ProofStubs._
  describe("check") {
    type Premise = Bad
    def prem: Premise = Bad()
    val checker = StructuralProofChecker[ProofStubs.StubRule, String](prem)
    it("should not allow premise inside box") {
      val pf = StubProof(
        rootSteps = Seq("box"),
        map = Map(
          "box" -> StubBox(StubBoxInfo(), Seq("line")),
          "line" -> StubLine(StubFormula(), prem)
        )
      )

      checker.check(pf) shouldBe List(
        ("line", PremiseInsideBox())
      )
    }

    it("should not allow premise inside box inside box") {
      val pf = StubProof(
        rootSteps = Seq("outer"),
        map = Map(
          "outer" -> StubBox(StubBoxInfo(), Seq("inner")),
          "inner" -> StubBox(StubBoxInfo(), Seq("line")),
          "line" -> StubLine(StubFormula(), prem)
        )
      )

      checker.check(pf) shouldBe List(
        ("line", PremiseInsideBox())
      )
    }

    it("should allow normal line inside box") {
      val pf = StubProof(
        rootSteps = Seq("box"),
        map = Map(
          "box" -> StubBox(StubBoxInfo(), Seq("line")),
          "line" -> StubLine(StubFormula(), Good()) // not a premise!
        )
      )

      checker.check(pf) shouldBe Nil
    }

    it("should allow premise line outside box") {
      val pf = StubProof(
        rootSteps = Seq("line"),
        map = Map(
          "line" -> StubLine(StubFormula(), prem)
        )
      )

      checker.check(pf) shouldBe Nil
    }
  }
}
