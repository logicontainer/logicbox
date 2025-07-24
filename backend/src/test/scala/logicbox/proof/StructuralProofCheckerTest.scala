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
    val checker = StructuralProofChecker[ProofStubs.StubRule, String](Premise(), Assumption())
    it("should not allow premise inside box") {
      val pf = StubProof(
        rootSteps = Seq("box"),
        map = Map(
          "box" -> StubBox(StubBoxInfo(), Seq("line")),
          "line" -> StubLine(StubFormula(), Premise())
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
          "line" -> StubLine(StubFormula(), Premise())
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
          "line" -> StubLine(StubFormula(), Premise())
        )
      )

      checker.check(pf) shouldBe Nil
    }

    it("should not allow assumption in root scope") {
      val pf = StubProof(
        rootSteps = Seq("line"),
        map = Map(
          "line" -> StubLine(StubFormula(), Assumption())
        )
      )

      checker.check(pf) shouldBe List(("line", InvalidAssumption()))
    }

    it("should not allow assumption if it is second line of box") {
      val pf = StubProof(
        rootSteps = Seq("box"),
        map = Map(
          "box" -> StubBox(StubBoxInfo(), Seq("l1", "l2")),
          "l1" -> StubLine(StubFormula(), Assumption()), // fine
          "l2" -> StubLine(StubFormula(), Assumption()), // not fine
        )
      )

      checker.check(pf) shouldBe List(
        ("l2", InvalidAssumption())
      )
    }
  }
}
