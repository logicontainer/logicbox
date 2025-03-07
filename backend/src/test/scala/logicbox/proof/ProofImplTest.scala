package logicbox.proof

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import logicbox.framework.ModifiableProof
import logicbox.framework.Proof
import logicbox.framework.Proof.Box
import logicbox.framework.Proof.Line

class ProofImplTest extends AnyFunSpec {
  import ProofStubs._
  import ModifiableProof._

  private type Pf = ModifiableProof[F, R, B, Id]

  case object StubStrategy extends ProofStepStrategy[F, R, B, Id] {
    override def createEmptyLine: Line[F, R, Id] = StubLine()
    override def createEmptyBox: Box[B, Id] = StubBox()
    override def updateRefs(line: Line[F, R, Id], refs: Seq[Id]): Line[F, R, Id] = 
      StubLine(line.formula, line.rule, refs)
    override def updateRule(line: Line[F, R, Id], rule: R): Line[F, R, Id] = 
      StubLine(line.formula, rule, line.refs)
    override def updateFormula(line: Line[F, R, Id], formula: F): Line[F, R, Id] =
      StubLine(formula, line.rule, line.refs)
    override def updateBoxInfo(box: Box[B, Id], info: B): Box[B, Id] = 
      StubBox(info, box.steps)
    override def updateBoxSteps(box: Box[B, Id], steps: Seq[Id]): Box[B, Id] = 
      StubBox(box.info, steps)
  }

  describe("ProofImpl::addLine") {
    it("should add line to empty proof") {
      val proof: Pf = ProofImpl.empty(StubStrategy)

      proof.addLine("id", ProofTop) match {
        case Right(proof1) => 
          proof1.getStep("id") should matchPattern {
            case Right(Proof.Line(StubFormula(0), Good(), Seq())) =>
          }
          proof1.rootSteps should contain("id")
        case _ => ???
      }
    }

    it("should allow adding below existing line") {
      var proof: Pf = ProofImpl.empty(StubStrategy)

      proof = proof.addLine("1", ProofTop).getOrElse(???)
      proof = proof.addLine("2", AtLine("1", Direction.Below)).getOrElse(???)

      proof.getStep("2") should matchPattern {
        case Right(Proof.Line(_, _, _)) =>
      }
      proof.rootSteps shouldBe Seq("1", "2")
    }

    it("should allow adding above existing line") {
      var proof: Pf = ProofImpl.empty(StubStrategy)

      proof = proof.addLine("1", ProofTop).getOrElse(???)
      proof = proof.addLine("2", AtLine("1", Direction.Above)).getOrElse(???)

      proof.getStep("2") should matchPattern {
        case Right(Proof.Line(_, _, _)) =>
      }
      proof.rootSteps shouldBe Seq("2", "1")

      proof = proof.addLine("3", AtLine("1", Direction.Above)).getOrElse(???)
      proof.getStep("3") should matchPattern {
        case Right(Proof.Line(_, _, _)) =>
      }
      proof.rootSteps shouldBe Seq("2", "3", "1")
    }

    it("should allow adding line inside box by prepending to existing line") {
      var proof: Pf = ProofImpl.empty(StubStrategy)

      proof = proof.addBox("box", ProofTop).getOrElse(???)
      proof = proof.addLine("l1", BoxTop("box")).getOrElse(???)
      proof = proof.addLine("l2", AtLine("l1", Direction.Above)).getOrElse(???)
      
      val Right(step) = proof.getStep("box"): @unchecked
      step.isInstanceOf[Proof.Box[?, ?]] shouldBe true
      val box = step.asInstanceOf[Proof.Box[B, Id]]

      box.steps shouldBe Seq("l2", "l1")
    }

    it("should report error when adding to nonexistent line") {
      var proof: Pf = ProofImpl.empty(StubStrategy)
      proof = proof.addLine("l1", ProofTop).getOrElse(???)
      proof.addLine("l2", AtLine("l3", Direction.Above)) should matchPattern {
        case Left(InvalidPosition(AtLine("l3", Direction.Above), _)) =>
      }
      proof.addLine("l2", AtLine("l3", Direction.Below)) should matchPattern {
        case Left(InvalidPosition(AtLine("l3", Direction.Below), _)) =>
      }
    }

    it("should report error when adding to top of box nonexistent") {
      var proof: Pf = ProofImpl.empty(StubStrategy)
      proof = proof.addBox("box", ProofTop).getOrElse(???)
      proof.addLine("l1", BoxTop("bex")) should matchPattern {
        case Left(InvalidPosition(BoxTop("bex"), _)) => 
      }
    }

    it("should report error when adding to top of box, but id is line") {
      var proof: Pf = ProofImpl.empty(StubStrategy)
      proof = proof.addLine("line", ProofTop).getOrElse(???)
      proof.addLine("l1", BoxTop("line")) should matchPattern {
        case Left(InvalidPosition(BoxTop("line"), _)) => 
      }
    }

    it("should report error when adding existing line") {
      var proof: Pf = ProofImpl.empty(StubStrategy)
      proof = proof.addLine("line", ProofTop).getOrElse(???)
      proof.addLine("line", ProofTop) should matchPattern {
        case Left(IdAlreadyInUse("line")) =>
      }
    }
  }

  describe("ProofImpl::addBox") {
    it("should allow adding empty box to proof") {
      var proof: Pf = ProofImpl.empty(StubStrategy)

      proof = proof.addBox("box", ProofTop).getOrElse(???)
      
      proof.getStep("box") should matchPattern {
        case Right(Proof.Box(StubBoxInfo(_), Seq())) => 
      }
      proof.rootSteps shouldBe Seq("box")
    }

    it("should allow adding box above another box") {
      var proof: Pf = ProofImpl.empty(StubStrategy)
      proof = proof.addBox("b1", ProofTop).getOrElse(???)
      
      proof.getStep("b1") should matchPattern {
        case Right(Proof.Box(_, _)) => 
      }
      proof.rootSteps shouldBe Seq("b1")

      proof = proof.addBox("b2", AtLine("b1", Direction.Above)).getOrElse(???)
      
      proof.getStep("b2") should matchPattern {
        case Right(Proof.Box(_, _)) => 
      }

      proof.rootSteps shouldBe Seq("b2", "b1")
    }

    it("should allow adding box as only element of box") {
      var proof: Pf = ProofImpl.empty(StubStrategy)
      proof = proof.addBox("b1", ProofTop).getOrElse(???)
      
      proof.getStep("b1") should matchPattern {
        case Right(Proof.Box(_, _)) => 
      }
      proof.rootSteps shouldBe Seq("b1")

      proof = proof.addBox("b2", BoxTop("b1")).getOrElse(???)
      
      proof.getStep("b2") should matchPattern {
        case Right(Proof.Box(_, _)) => 
      }
      proof.rootSteps shouldBe Seq("b1")
      
      val Right(step) = proof.getStep("b1"): @unchecked
      step.isInstanceOf[Proof.Box[?, ?]] shouldBe true
      val box = step.asInstanceOf[Proof.Box[B, Id]]
      box.steps shouldBe Seq("b2")
    }

    it("should report error when adding existing box") {
      var proof: Pf = ProofImpl.empty(StubStrategy)
      proof = proof.addBox("box", ProofTop).getOrElse(???)
      proof.addBox("box", ProofTop) should matchPattern {
        case Left(IdAlreadyInUse("box")) =>
      }
    }
  }

  describe("OptionalProof::updateFormula") {
    it("should update formula correctly") {
      var proof: Pf = ProofImpl.empty(StubStrategy)
      proof = proof.addLine("line", ProofTop).getOrElse(???)
      proof = proof.updateFormula("line", StubFormula(2)).getOrElse(???)

      proof.getStep("line") should matchPattern {
        case Right(Proof.Line(StubFormula(2), Good(), Seq())) =>
      }
    }

    it("should report when updating nonexistant line") {
      val proof: Pf = ProofImpl.empty(StubStrategy)
      proof.updateFormula("line", StubFormula(2)) should matchPattern {
        case Left(CannotUpdateStep("line", _)) =>
      }
    }

    it("should report when updating box") {
      var proof: Pf = ProofImpl.empty(StubStrategy)
      proof = proof.addBox("box", ProofTop).getOrElse(???)
      proof.updateFormula("box", StubFormula(2)) should matchPattern {
        case Left(CannotUpdateStep("box", _)) =>
      }
    }
  }

  describe("ProofImpl::updateRule") {
    it("should update rule correctly") {
      var proof: Pf = ProofImpl.empty(StubStrategy)
      proof = proof.addLine("line", ProofTop).getOrElse(???)
      proof = proof.updateRule("line", Good()).getOrElse(???)

      proof.getStep("line") should matchPattern {
        case Right(Proof.Line(StubFormula(_), Good(), Seq())) =>
      }
    }

    it("should report when updating nonexistant line") {
      val proof: Pf = ProofImpl.empty(StubStrategy)
      proof.updateRule("line", Bad()) should matchPattern {
        case Left(CannotUpdateStep("line", _)) =>
      }
    }

    it("should report when attempting to update box") {
      var proof: Pf = ProofImpl.empty(StubStrategy)
      proof = proof.addBox("box", ProofTop).getOrElse(???)
      proof.updateRule("box", Good()) should matchPattern {
        case Left(CannotUpdateStep("box", _)) =>
      }
    }
  }

  describe("ProofImpl::updateReferences") {
    it("should update references correctly") {
      var proof: Pf = ProofImpl.empty(StubStrategy)
      proof = proof.addLine("line", ProofTop).getOrElse(???)
      proof = proof.updateReferences("line", List("r1", "r2")).getOrElse(???)

      proof.getStep("line") should matchPattern {
        case Right(Proof.Line(StubFormula(_), Good(), Seq("r1", "r2"))) =>
      }
    }

    it("should report when updating nonexistant line") {
      val proof: Pf = ProofImpl.empty(StubStrategy)
      proof.updateReferences("line", List("r1", "r2")) should matchPattern {
        case Left(CannotUpdateStep("line", _)) =>
      }
    }

    it("should report when attempting to update box") {
      var proof: Pf = ProofImpl.empty(StubStrategy)
      proof = proof.addBox("box", ProofTop).getOrElse(???)
      proof.updateReferences("box", List("r1", "r2")) should matchPattern {
        case Left(CannotUpdateStep("box", _)) =>
      }
    }
  }
  describe("ProofImpl::removeStep") {
    it("should reject when removing line that doesn't exist") {
      var proof: Pf = ProofImpl.empty(StubStrategy)
      proof.removeStep("id") should matchPattern {
        case Left(CannotRemoveStep("id", _)) =>
      }
    }

    it("should correctly remove added line") {
      var proof: Pf = ProofImpl.empty(StubStrategy)
      proof = proof.addLine("line", ProofTop).getOrElse(???)
      proof = proof.removeStep("line").getOrElse(???)
      proof.getStep("line") should matchPattern {
        case Left(Proof.StepNotFound("line", _)) =>
      }
    }

    it("should correctly remove box within box") {
      var proof: Pf = ProofImpl.empty(StubStrategy)
      proof = proof.addBox("b1", ProofTop).getOrElse(???)
      proof = proof.addBox("b2", BoxTop("b1")).getOrElse(???)
      proof = proof.removeStep("b2").getOrElse(???)

      proof.getStep("b2") should matchPattern {
        case Left(Proof.StepNotFound("b2", _)) =>
      }
    }

    it("should delete inner line when box is destroyed") {
      var proof: Pf = ProofImpl.empty(StubStrategy)
      proof = proof.addBox("box", ProofTop).getOrElse(???)
      proof = proof.addLine("line", BoxTop("box")).getOrElse(???)
      proof = proof.removeStep("box").getOrElse(???)

      proof.getStep("line") should matchPattern {
        case Left(Proof.StepNotFound("line", _)) =>
      }
    }
  }

  // TODO: think some more about cycles, maybe generally
  //   - shouldn't be possible here, since position is absolute, can never nest in reverse
  //   - careful tho!
}
