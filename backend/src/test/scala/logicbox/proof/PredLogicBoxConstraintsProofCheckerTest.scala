package logicbox.proof

import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import org.scalatest.funspec.AnyFunSpec

import logicbox.formula.PropLogicFormula
import logicbox.framework.Proof

import logicbox.framework.Error

import logicbox.rule.PredLogicRule
import logicbox.rule.PredLogicRule._
import logicbox.framework.RulePosition
import logicbox.framework.Error.Miscellaneous
import logicbox.framework.Location

class PredLogicBoxContraintsProofCheckerTest extends AnyFunSpec {
  describe("check") {
    case class Ass()
    case class OtherRule()

    type Rule = PredLogicRule | Ass | OtherRule
    val checker = PredLogicBoxConstraintsProofChecker[Rule, String](Ass())

    it("should reject if ExistsElim doesn't have assumption on first line") {
      val proof = ProofImpl(
        map = Map(
          "box" -> ProofBoxImpl((), Seq("l1")),
          "l1" -> ProofLineImpl((), OtherRule(), Seq()), // should be assumption
          "l2" -> ProofLineImpl((), OtherRule(), Seq()),
          "l3" -> ProofLineImpl((), ExistsElim(), Seq("l2", "box"))
        ),
        rootSteps = Seq("box", "l2", "l3")
      )

      checker.check(proof) should matchPattern {
        case List(("l3", Error.Miscellaneous(Location(1 :: Nil), _))) =>
      }
    }

    it("should reject ForAllIntro if first line is assumption") {
      val proof = ProofImpl(
        map = Map(
          "box" -> ProofBoxImpl((), Seq("l1")),
          "l1" -> ProofLineImpl((), Ass(), Seq()), // should be something else
          "l2" -> ProofLineImpl((), ForAllIntro(), Seq("box"))
        ),
        rootSteps = Seq("box", "l2")
      )

      checker.check(proof) should matchPattern {
        case List(("l2", Miscellaneous(Location(0 :: Nil), _))) =>
      }
    }

    it("should not do anything in ForAll when ref is not assumption") {
      val proof = ProofImpl(
        map = Map(
          "box" -> ProofBoxImpl((), Seq("l1")),
          "l1" -> ProofLineImpl((), OtherRule(), Seq()), // should be something else
          "l2" -> ProofLineImpl((), ForAllIntro(), Seq("box"))
        ),
        rootSteps = Seq("box", "l2")
      )

      checker.check(proof) shouldBe Nil
    }

    it("should not do anything in ForAll when first step of box is box") {
      val proof = ProofImpl(
        map = Map(
          "innerbox" -> ProofBoxImpl((), Seq()),
          "box" -> ProofBoxImpl((), Seq("innerbox")),
          "l2" -> ProofLineImpl((), ForAllIntro(), Seq("box"))
        ),
        rootSteps = Seq("box", "l2")
      )

      checker.check(proof) shouldBe Nil
    }
  }
}
