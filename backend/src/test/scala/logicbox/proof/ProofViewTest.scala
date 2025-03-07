package logicbox.proof

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import logicbox.framework.Proof

class ProofViewTest extends AnyFunSpec {
  import ProofStubs._

  private case class F2(f: StubFormula)
  private case class R2(r: StubRule)
  private case class B2(i: StubBoxInfo)
  private type Id = String

  case class Line[F, R, Id](formula: F, rule: R, refs: Seq[Id]) extends Proof.Line[F, R, Id]
  case class Box[B, Id](info: B, steps: Seq[Id]) extends Proof.Box[B, Id]

  private def transformation(id: Id, step: Proof.Step[F, R, B, Id]): Proof.Step[F2, R2, B2, Id] = step match {
    case Proof.Line(f: F, r: R, refs: Seq[Id] @unchecked) => Line(F2(f), R2(r), refs)
    case Proof.Box(i: B, steps: Seq[Id] @unchecked) => Box(B2(i), steps)
    case _ => ???
  }
  
  describe("ProofView::getStep") {
    it("should apply transformation to inner proof") {
      val proof = StubProof(
        rootSteps = Seq("line", "box"),
        map = Map(
          "box" -> Box(StubBoxInfo("something"), Seq()),
          "line" -> Line(StubFormula(200), Bad(), Seq("box"))
        )
      )

      val view = ProofView(proof, transformation)

      view.getStep("box") should matchPattern {
        case Right(Proof.Box(B2(StubBoxInfo("something")), Seq())) =>
      }
      
      view.getStep("line") should matchPattern {
        case Right(Proof.Line(F2(StubFormula(200)), R2(Bad()), Seq("box"))) =>
      }
    }
  }
}
