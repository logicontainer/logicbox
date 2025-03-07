package logicbox.proof

import logicbox.framework._
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import org.scalatest.funspec.AnyFunSpec
import logicbox.framework.Proof.Step

class RuleBasedProofCheckerTest extends AnyFunSpec {
  import ProofStubs._

  def proofChecker(ruleChecker: StubRuleChecker = StubRuleChecker()): ProofChecker[F, R, B, Id, RuledBasedProofChecker.Diagnostic[Id, V]] = 
    RuledBasedProofChecker(ruleChecker)

  describe("RuledBasedProofChecker::check") {
    import RuledBasedProofChecker._

    it("should be fine with empty proof") {  
      val checker = proofChecker()
      val proof = StubProof()
      checker.check(proof) shouldBe Nil
    }

    it("should not allow proof when single id points to nothing") {
      val checker = proofChecker()
      val proof = StubProof(Seq("id"), Map.empty)
      val result = checker.check(proof)

      Inspectors.forAtLeast(1, result) {
        _ should matchPattern { case StepNotFound("id", _) => }
      }
    }

    it("should disallow when reference is to unknown id") {
      val checker = proofChecker()
      val proof = StubProof(Seq("id"), Map(
        "id" -> StubLine(refs = Seq("something that doesn't exist"))
      ))
      val result = checker.check(proof)

      Inspectors.forAtLeast(1, result) {
        _ should matchPattern {
          case ReferenceIdNotFound("id", 0, "something that doesn't exist", _) => 
        }
      }
    }

    it("should not of rule violation on first line") {
      val checker = proofChecker()
      val proof = StubProof(Seq("id1"), Map(
        "id1" -> StubLine(rule = Bad())
      ))
      val result = checker.check(proof)
      Inspectors.forAtLeast(1, result) {
        _ should matchPattern {
          case RuleViolation("id1", StubViolation()) =>
        }
      }
    }

    it("should call rule with correct refs") {
      val rc = StubRuleChecker()
      val checker = proofChecker(rc)

      val proof = StubProof(
        rootSteps = Seq("r0", "r1", "line"),
        map = Map(
          "line" -> StubLine(refs = Seq("r0", "r1")),
          "r0" -> StubLine(formula = StubFormula(38)),
          "r1" -> StubBox(info = StubBoxInfo("some info"), steps = Seq("ass", "ccl")),
          "ass" -> StubLine(formula = StubFormula(11)),
          "ccl" -> StubLine(formula = StubFormula(12))
        )
      )

      checker.check(proof)
      rc.refsCalledWith shouldBe Some(List(
        ReferenceLineImpl(formula = StubFormula(38)),
        ReferenceBoxImpl(
          info = StubBoxInfo("some info"), 
          assumption = StubFormula(11), 
          conclusion = StubFormula(12)
        )
      ))
    }

    it("should verify subproof (in box)") {
      val checker = proofChecker()

      val proof = StubProof(
        rootSteps = Seq("box"),
        map = Map(
          "box" -> StubBox(info = StubBoxInfo("some info"), steps = Seq("ass", "ccl")),
          "ass" -> StubLine(formula = StubFormula(11), Bad()),
          "ccl" -> StubLine(formula = StubFormula(12), Good())
        )
      )

      Inspectors.forAtLeast(1, checker.check(proof)) {
        _ should matchPattern {
          case RuleViolation("ass", StubViolation()) =>
        }
      }
    }

    it("should not allow box as ref when first line in box is box") {
      val checker = proofChecker()
      val proof = StubProof(
        rootSteps = Seq("box", "line"),
        map = Map(
          "line" -> StubLine(refs = Seq("box")),
          "box" -> StubBox(steps = Seq("assBox", "concl")),
          "assBox" -> StubBox(steps = Seq()),
          "concl" -> StubLine()
        )
      )

      val result = checker.check(proof)
      Inspectors.forAtLeast(1, result) {
        case MalformedReference("line", 0, "box", expl) => 
      }
    }

    it("should not allow box with undefined reference (in concl)") {
      val checker = proofChecker()
      val proof = StubProof(
        rootSteps = Seq("box", "line"),
        map = Map(
          "line" -> StubLine(refs = Seq("box")),
          "box" -> StubBox(steps = Seq("ass", "concl")),
          "ass" -> StubLine(),
        )
      )

      val result = checker.check(proof)
      Inspectors.forAtLeast(1, result) {
        case MalformedReference("line", 0, "box", expl) => 
          expl should include("conclusion")
      }
    }

    it("should not allow box with undefined reference (in assumption)") {
      val checker = proofChecker()
      val proof = StubProof(
        rootSteps = Seq("box", "line"),
        map = Map(
          "line" -> StubLine(refs = Seq("box")),
          "box" -> StubBox(steps = Seq("ass", "concl")),
          "concl" -> StubLine(),
        )
      )

      val result = checker.check(proof)
      Inspectors.forAtLeast(1, result) {
        case MalformedReference("line", 0, "box", expl) => 
          expl should include("assumption")
      }
    }

    it("should not have duplicate errors when box has one wrong line") {
      val checker = proofChecker()
      val proof = StubProof(
        rootSteps = Seq("box", "line"),
        map = Map(
          "line" -> StubLine(refs = Seq("box")),
          "box" -> StubBox(steps = Seq("ass")) // no ref
        )
      )
      
      val result = checker.check(proof)
      Inspectors.forAtMost(1, result) {
        case MalformedReference("line", 0, "box", _) =>
      }
    }
  }
}
