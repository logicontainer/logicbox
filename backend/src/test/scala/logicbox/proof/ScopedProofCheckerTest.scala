package logicbox.proof

import logicbox.framework._
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import org.scalatest.funspec.AnyFunSpec
import logicbox.framework.Proof.Step

class ScopedProofCheckerTest extends AnyFunSpec {
  import ProofStubs._
  import ScopedProofChecker._

  describe("ScopedProofCheckerTest::check") {
    val checker = ScopedProofChecker[Id]()
    it("should allow empty proof") {
      val proof = StubProof()
      checker.check(proof) should be (Nil)
    }

    it("should diallow references to lines after current line") {
      val proof = StubProof(
        rootSteps = Seq("1", "2"),
        map = Map(
          "1" -> StubLine(refs = Seq("2")),
          "2" -> StubLine()
        )
      )

      checker.check(proof) shouldBe List(ReferenceToLaterStep("1", 0, "2"))
    }

    it("should diallow references to lines after current line (where step is not first step)") {
      val proof = StubProof(
        rootSteps = Seq("3", "1", "2"),
        map = Map(
          "1" -> StubLine(refs = Seq("2")),
          "2" -> StubLine(),
          "3" -> StubLine()
        )
      )

      checker.check(proof) shouldBe List(ReferenceToLaterStep("1", 0, "2"))
    }

    it("should report later reference used if happens in box") {
      val proof = StubProof(
        rootSteps = Seq("box", "l2"),
        map = Map(
          "box" -> StubBox(steps = Seq("l1")),
          "l1" -> StubLine(refs = Seq("l2")), // downward reference
          "l2" -> StubLine()
        )
      )

      Inspectors.forAtLeast(1, checker.check(proof)) {
        _ should matchPattern {
          case ReferenceToLaterStep("l1", 0, "l2") => 
        }
      }
    }

    it("should not stop checking after box") {
      val proof = StubProof(
        rootSteps = Seq("box", "line", "down"),
        map = Map(
          "box" -> StubBox(),
          "line" -> StubLine(refs = Seq("box", "down")),
          "down" -> StubLine()
        )
      )

      checker.check(proof) shouldBe List(ReferenceToLaterStep("line", 1, "down"))
    }

    it("should not allow reference to box which has not yet been closed") {
      println("hi")
      val proof = StubProof(
        rootSteps = Seq("box"),
        map = Map(
          "box" -> StubBox(steps = Seq("line")),
          "line" -> StubLine(refs = Seq("box"))
        )
      )

      checker.check(proof) shouldBe List(ReferenceToUnclosedBox("line", 0, "box"))
    }

    it("should not notice reference which simply doesn't have a scope (not a part of proof)") {
      val proof = StubProof(
        rootSteps = Seq("1"),
        map = Map(
          "1" -> StubLine(refs = Seq("2")),
          "2" -> StubLine() // two is 'nowhere'
        )
      )

      checker.check(proof) should be (Nil)
    }

    it("should disallow reading from closed box") {
      val proof = StubProof(
        rootSteps = Seq("b1", "b2"),
        map = Map(
          "b1" -> StubBox(steps = Seq("line1")),
          "b2" -> StubBox(steps = Seq("line2")),
          "line1" -> StubLine(),
          "line2" -> StubLine(refs = Seq("line1")) // refers to inside of other box
        )
      )

      checker.check(proof) shouldBe List(ScopeViolation("line2", "b2", 0, "line1", "b1"))
    }

    it("should report ref to later step when referring to later box") {
      val proof = StubProof(
        rootSteps = Seq("1", "box"),
        map = Map(
          "1" -> StubLine(refs = Seq("box")),
          "box" -> StubBox(steps = Seq("2")),
          "2" -> StubLine()
          )
      )
      
      checker.check(proof) shouldBe List(ReferenceToLaterStep("1", 0, "box"))
    }
  }
}
