package logicbox.proof

import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import org.scalatest.funspec.AnyFunSpec

import logicbox.rule.PropLogicRule
import logicbox.formula.PropLogicFormula
import logicbox.framework.Proof

import logicbox.rule.PropLogicRule._
import logicbox.framework.RuleViolation
import logicbox.framework.RuleViolation.ReferenceDoesntMatchRule
import logicbox.framework.Diagnostic.RuleViolationAtStep

class PropLogicBoxAssumptionsProofCheckerTest extends AnyFunSpec {

  describe("check") {
    val checker = PropLogicBoxAssumptionsProofChecker[Unit, String]()
    it("should reject if ImplIntro doesn't have assumption on first line") {
      val proof = ProofImpl(
        map = Map(
          "box" -> ProofBoxImpl((), Seq("l1")),
          "l1" -> ProofLineImpl((), Premise(), Seq()),
          "l2" -> ProofLineImpl((), ImplicationIntro(), Seq("box"))
        ),
        rootSteps = Seq("box", "l2")
      )

      checker.check(proof) should matchPattern {
        case List(RuleViolationAtStep("l2", ReferenceDoesntMatchRule(0, _))) =>
      }
    }

    it("should not say anything for implication intro box is empty") {
      val proof = ProofImpl(
        map = Map(
          "box" -> ProofBoxImpl((), Seq()),
          "l" -> ProofLineImpl((), ImplicationIntro(), Seq("box"))
        ),
        rootSteps = Seq("box", "l")
      )
      
      checker.check(proof) shouldBe Nil
    }

    it("should say nothing for implication intro with no refs") {
      val proof = ProofImpl(
        map = Map(
          "l" -> ProofLineImpl((), ImplicationIntro(), Seq())
        ),
        rootSteps = Seq("l")
      )
      
      checker.check(proof) shouldBe Nil
    }

    it("should ignore other rules") {
      val proof = ProofImpl(
        map = Map(
          "m" -> ProofLineImpl((), Premise(), Seq()),
        ),
        rootSteps = Seq("m")
      )
      
      checker.check(proof) shouldBe Nil
    }

    it("should ignore malformed reference") {
      val proof = ProofImpl(
        map = Map(
          "m" -> ProofLineImpl((), ImplicationIntro(), Seq("asldjLAKsdj")),
        ),
        rootSteps = Seq("m")
      )
      
      checker.check(proof) shouldBe Nil
    
    }

    it("should say nothing for implication intro with ref to line") {
      val proof = ProofImpl(
        map = Map(
          "m" -> ProofLineImpl((), Premise(), Seq()),
          "l" -> ProofLineImpl((), ImplicationIntro(), Seq("m"))
        ),
        rootSteps = Seq("m", "l")
      )
      
      checker.check(proof) shouldBe Nil
    }

    it("should reject if NotIntro doesn't have assumption on first line") {
      val proof = ProofImpl(
        map = Map(
          "box" -> ProofBoxImpl((), Seq("l1")),
          "l1" -> ProofLineImpl((), Premise(), Seq()),
          "l2" -> ProofLineImpl((), NotIntro(), Seq("box"))
        ),
        rootSteps = Seq("box", "l2")
      )

      checker.check(proof) should matchPattern {
        case List(RuleViolationAtStep("l2", ReferenceDoesntMatchRule(0, _))) =>
      }
    }

    it("should reject if PBC doesn't have assumption on first line") {
      val proof = ProofImpl(
        map = Map(
          "box" -> ProofBoxImpl((), Seq("l1")),
          "l1" -> ProofLineImpl((), Premise(), Seq()),
          "l2" -> ProofLineImpl((), ProofByContradiction(), Seq("box"))
        ),
        rootSteps = Seq("box", "l2")
      )

      checker.check(proof) should matchPattern {
        case List(RuleViolationAtStep("l2", ReferenceDoesntMatchRule(0, _))) =>
      }
    }

    it("should reject if OrElim doesn't have assumption on first line of second ref") {
      val proof = ProofImpl(
        map = Map(
          "l1" -> ProofLineImpl((), Premise(), Seq()),
          "box1" -> ProofBoxImpl((), Seq("l1")),

          "l2" -> ProofLineImpl((), Assumption(), Seq()),
          "box2" -> ProofBoxImpl((), Seq("l2")),

          "l4" -> ProofLineImpl((), OrElim(), Seq("l3", "box1", "box2")),
          "l3" -> ProofLineImpl((), Premise(), Seq()),
        ),
        rootSteps = Seq("box1", "box2", "l3", "l4")
      )
      
      checker.check(proof) should matchPattern {
        case List(RuleViolationAtStep("l4", ReferenceDoesntMatchRule(1, _))) =>
      }
    }

    it("should reject if OrElim doesn't have assumption on first line of third ref") {
      val proof = ProofImpl(
        map = Map(
          "l1" -> ProofLineImpl((), Assumption(), Seq()),
          "box1" -> ProofBoxImpl((), Seq("l1")),

          "l2" -> ProofLineImpl((), Premise(), Seq()),
          "box2" -> ProofBoxImpl((), Seq("l2")),

          "l4" -> ProofLineImpl((), OrElim(), Seq("l3", "box1", "box2")),
          "l3" -> ProofLineImpl((), Premise(), Seq()),
        ),
        rootSteps = Seq("box1", "box2", "l3", "l4")
      )
      
      checker.check(proof) should matchPattern {
        case List(RuleViolationAtStep("l4", ReferenceDoesntMatchRule(2, _))) =>
      }
    }
  }
}
