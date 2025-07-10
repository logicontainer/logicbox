package logicbox.server

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Mockito
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.*

import logicbox.framework._
import logicbox.framework.RulePart._
import logicbox.server.format.OutputError
import logicbox.framework.RulePosition.Premise
import logicbox.proof.ProofNavigator
import logicbox.rule.RulePartNavigator
import logicbox.server.format.OutputError.AmbiguityEntry
import logicbox.framework.RulePart

class ErrorConverterImplTest extends AnyFunSpec with MockitoSugar {
  import logicbox.ProofStubs._

  def fix(pf: Proof[F, R, B, String]) = {
    def expToString(f: Int): String = f.toString
    def rulePartToLaTeX(r: RulePart): String = s"--${r.toString}"
    
    val pnav = mock[Navigator[(Proof[F, R, B, String], String), Int]]
    val rnav = mock[Navigator[InfRule, RulePart]]
    val getInfRule = mock[R => Option[InfRule]]

    (ErrorConverterImpl[F, R, B, Int](pnav, rnav, getInfRule, expToString, rulePartToLaTeX, pf): ErrorConverter, pnav, rnav, getInfRule)
  }

  describe("convert ShapeMismatch") {
    it("should convert shape mismatch on conclusion") {
      val pf = StubProof(
        rootSteps = Seq("l1"),
        map = Map(
          "l1" -> StubLine(StubFormula(), Good())
        )
      )

      val (cvtr, pnav, rnav, getInfRule) = fix(pf)

      val concl = MetaFormula(Formulas.Phi)
      val ir = InfRule(Nil, concl)

      val conclLoc = Location.conclusion.root
      when(getInfRule(Good())).thenReturn(Some(ir))
      when(pnav.get((pf, "l1"), conclLoc)).thenReturn(Some(2))
      when(rnav.get(ir, conclLoc)).thenReturn(Some(concl))

      cvtr.convert("l1", Error.ShapeMismatch(conclLoc)) shouldBe Some(OutputError.ShapeMismatch(
        uuid = "l1",
        rulePosition = "conclusion",
        expected = s"--${concl.toString}",
        actual = "2"
      ))
    }
    
    it("should convert shape mismatch on premises 0, 1") {
      val pf = StubProof(
        rootSteps = Seq("l2"),
        map = Map(
          "ref" -> StubLine(),
          "l2" -> StubLine(StubFormula(1), Bad(), Seq("ref", "ref"))
        )
      )

      val (cvtr, pnav, rnav, getInfRule) = fix(pf)

      val prem0 = MetaFormula(Formulas.Chi)
      val prem1 = MetaFormula(Formulas.Psi)
      val ir = InfRule(List(prem0, prem1), MetaFormula(Formulas.Phi))

      val prem0Loc = Location.premise(0).root
      val prem1Loc = Location.premise(1).root
      when(getInfRule(Bad())).thenReturn(Some(ir))
      when(pnav.get((pf, "l2"), prem0Loc)).thenReturn(Some(4))
      when(pnav.get((pf, "l2"), prem1Loc)).thenReturn(Some(5))
      when(rnav.get(ir, prem0Loc)).thenReturn(Some(prem0))
      when(rnav.get(ir, prem1Loc)).thenReturn(Some(prem1))

      cvtr.convert("l2", Error.ShapeMismatch(prem0Loc)) shouldBe Some(OutputError.ShapeMismatch(
        uuid = "l2",
        rulePosition = "premise 0",
        expected = s"--${prem0.toString}",
        actual = "4"
      ))

      cvtr.convert("l2", Error.ShapeMismatch(prem1Loc)) shouldBe Some(OutputError.ShapeMismatch(
        uuid = "l2",
        rulePosition = "premise 1",
        expected = s"--${prem1.toString}",
        actual = "5"
      ))
    }

    it("should fail when location doesn't point to concl or premises") {
      val pf = StubProof(
        rootSteps = Seq("l1"),
        map = Map(
          "l1" -> StubLine(StubFormula(), Good())
        )
      )

      val (cvtr, pnav, rnav, getInfRule) = fix(pf)

      val concl = MetaFormula(Formulas.Phi)
      val ir = InfRule(Nil, concl)

      val invalidLoc = Location.root
      when(getInfRule(Good())).thenReturn(Some(ir))

      // even if the navigators somehow accept!
      when(pnav.get((pf, "l1"), invalidLoc)).thenReturn(Some(2))
      when(rnav.get(ir, invalidLoc)).thenReturn(Some(concl))

      cvtr.convert("l1", Error.ShapeMismatch(invalidLoc)) shouldBe None
    }

    it("should return none when proof step is not defined") {
      val pf = StubProof()
      val (cvtr, pnav, rnav, getInfRule) = fix(pf)

      val concl = MetaFormula(Formulas.Phi)
      val ir = InfRule(Nil, concl)

      val conclLoc = Location.conclusion.root
      when(getInfRule(Good())).thenReturn(Some(ir))
      when(rnav.get(ir, conclLoc)).thenReturn(Some(concl))
      // even if proof nav poops out something
      when(pnav.get((pf, "invalid_id"), conclLoc)).thenReturn(Some(2))

      cvtr.convert("invalid_id", Error.ShapeMismatch(conclLoc)) shouldBe None
    }

    it("should return none when proof step is not a line") {
      val pf = StubProof(rootSteps = Seq("box"), map = Map("box" -> StubBox()))
      val (cvtr, pnav, rnav, getInfRule) = fix(pf)

      val concl = MetaFormula(Formulas.Phi)
      val ir = InfRule(Nil, concl)

      val conclLoc = Location.conclusion.root
      when(getInfRule(Good())).thenReturn(Some(ir))
      when(rnav.get(ir, conclLoc)).thenReturn(Some(concl))
      when(pnav.get((pf, "invalid_id"), conclLoc)).thenReturn(Some(2)) // even if proof nav gives us something

      cvtr.convert("box", Error.ShapeMismatch(conclLoc)) shouldBe None
    }

    it("should return none if infrulenav fails") {
      val pf = StubProof(
        rootSteps = Seq("l1"),
        map = Map(
          "l1" -> StubLine(StubFormula(), Good())
        )
      )

      val (cvtr, pnav, rnav, getInfRule) = fix(pf)

      val ir = InfRule(List(MetaFormula(Formulas.Phi)), Contradiction())

      val prem0Loc = Location.premise(0).root
      when(getInfRule(Good())).thenReturn(Some(ir))
      when(pnav.get((pf, "l1"), prem0Loc)).thenReturn(Some(2)) 
      when(rnav.get(ir, prem0Loc)).thenReturn(None) // fail!

      cvtr.convert("l1", Error.ShapeMismatch(prem0Loc)) shouldBe None
    }

    it("should ret. none if proofnav fails") {
      val pf = StubProof(
        rootSteps = Seq("l1"),
        map = Map(
          "l3" -> StubLine(StubFormula(), Good())
        )
      )

      val (cvtr, pnav, rnav, getInfRule) = fix(pf)

      val prem0 = MetaFormula(Formulas.Phi)
      val ir = InfRule(List(prem0), Contradiction())

      val prem0Loc = Location.premise(0).root
      when(getInfRule(Good())).thenReturn(Some(ir))
      when(pnav.get((pf, "l3"), prem0Loc)).thenReturn(None) // fail!
      when(rnav.get(ir, prem0Loc)).thenReturn(Some(prem0)) 

      cvtr.convert("l3", Error.ShapeMismatch(prem0Loc)) shouldBe None
    }

    it("should ret. none if getInfRule fails") {
      val pf = StubProof(
        rootSteps = Seq("l1"),
        map = Map(
          "l3" -> StubLine(StubFormula(), Good())
        )
      )

      val (cvtr, pnav, rnav, getInfRule) = fix(pf)

      val prem0 = MetaFormula(Formulas.Phi)
      val ir = InfRule(List(prem0), Contradiction())

      val prem0Loc = Location.premise(0).root
      when(getInfRule(Good())).thenReturn(None)
      when(pnav.get((pf, "l3"), prem0Loc)).thenReturn(Some(2)) // fail!
      when(rnav.get(ir, prem0Loc)).thenReturn(Some(prem0)) 

      cvtr.convert("l3", Error.ShapeMismatch(prem0Loc)) shouldBe None
    }
  }

  describe("convert Ambiguous") {
    it("should convert error with no entries") {
      val pf = StubProof(
        rootSteps = Seq("ll", "l1"),
        map = Map(
          "ll" -> StubLine(StubFormula()),
          "l1" -> StubLine(StubFormula())
        )
      )

      val (cvtr, pnav, rnav, getInfRule) = fix(pf)
      val what1 = MetaTerm(Terms.T)
      val what2 = MetaTerm(Terms.T2)
      cvtr.convert("ll", Error.Ambiguous(what1, Nil)) shouldBe Some(OutputError.Ambiguous(
        uuid = "ll",
        subject = s"--${what1.toString}",
        entries = Nil
      ))

      cvtr.convert("l1", Error.Ambiguous(what2, Nil)) shouldBe Some(OutputError.Ambiguous(
        uuid = "l1",
        subject = s"--${what2.toString}",
        entries = Nil
      ))
    }

    it("should convert error with single entry in conclusion") {
      val pf = StubProof(
        rootSteps = Seq("ll", "l1"),
        map = Map(
          "ll" -> StubLine(StubFormula(), Good())
        )
      )

      val (cvtr, pnav, rnav, getInfRule) = fix(pf)
      val what = MetaTerm(Terms.T2)

      val concl = MetaFormula(Formulas.Psi)
      val ir = InfRule(Nil, concl)

      val conclLoc = Location.conclusion.root
      val entries = List(conclLoc)

      when(getInfRule(Good())).thenReturn(Some(ir))
      when(pnav.get((pf, "ll"), conclLoc)).thenReturn(Some(10))
      when(rnav.get(ir, conclLoc)).thenReturn(Some(concl))

      cvtr.convert("ll", Error.Ambiguous(what, entries)) shouldBe Some(OutputError.Ambiguous(
        uuid = "ll",
        subject = s"--${what.toString}",
        entries = List(AmbiguityEntry(
          rulePosition = "conclusion",
          meta = s"--${concl.toString}",
          actual = "10"
        ))
      ))
    }

    it("should convert error with single entry in premise 0") {
      val pf = StubProof(
        rootSteps = Seq("id"),
        map = Map(
          "id" -> StubLine(StubFormula(), Bad())
        )
      )

      val (cvtr, pnav, rnav, getInfRule) = fix(pf)
      val what = MetaVariable(Vars.X)

      val prem0 = And(MetaFormula(Formulas.Chi), MetaFormula(Formulas.Psi))
      val ir = InfRule(List(prem0), Contradiction())

      val prem0Loc = Location.premise(0).lhs
      val entries = List(prem0Loc)

      when(getInfRule(Bad())).thenReturn(Some(ir))
      when(pnav.get((pf, "id"), prem0Loc)).thenReturn(Some(5))
      when(rnav.get(ir, prem0Loc)).thenReturn(Some(prem0.phi))

      cvtr.convert("id", Error.Ambiguous(what, entries)) shouldBe Some(OutputError.Ambiguous(
        uuid = "id",
        subject = s"--${what.toString}",
        entries = List(AmbiguityEntry(
          rulePosition = "premise 0",
          meta = s"--${prem0.phi.toString}",
          actual = "5"
        ))
      ))
    }

    it("should convert error with multiple entries") {
      val pf = StubProof(
        rootSteps = Seq("id"),
        map = Map(
          "id" -> StubLine(StubFormula(), Bad())
        )
      )

      val (cvtr, pnav, rnav, getInfRule) = fix(pf)
      val what = MetaVariable(Vars.X)

      val prem0 = MetaFormula(Formulas.Chi)
      val concl = And(Contradiction(), MetaFormula(Formulas.Psi))
      val ir = InfRule(List(prem0), concl)

      val prem0Loc = Location.premise(0).root
      val conclLoc = Location.conclusion.rhs
      val entries = List(conclLoc, prem0Loc)

      when(getInfRule(Bad())).thenReturn(Some(ir))
      when(pnav.get((pf, "id"), prem0Loc)).thenReturn(Some(6))
      when(pnav.get((pf, "id"), conclLoc)).thenReturn(Some(7))
      when(rnav.get(ir, prem0Loc)).thenReturn(Some(prem0))
      when(rnav.get(ir, conclLoc)).thenReturn(Some(concl.psi))

      cvtr.convert("id", Error.Ambiguous(what, entries)) shouldBe Some(OutputError.Ambiguous(
        uuid = "id",
        subject = s"--${what.toString}",
        entries = List(AmbiguityEntry(
          rulePosition = "conclusion",
          meta = s"--${concl.psi.toString}",
          actual = "7"
        ), AmbiguityEntry(
          rulePosition = "premise 0",
          meta = s"--${prem0.toString}",
          actual = "6"
        ))
      ))
    }

    it("should return none when there is not first step") {
      val pf = StubProof(
        rootSteps = Seq("ll"),
        map = Map(
          "ll" -> StubLine(StubFormula(), Good())
        )
      )

      val (cvtr, pnav, rnav, getInfRule) = fix(pf)
      val what = MetaFormula(Formulas.Psi)

      val concl = MetaFormula(Formulas.Psi)
      val ir = InfRule(Nil, concl)

      val conclLoc = Location.root // LOCATION EMPTY
      val entries = List(conclLoc)

      when(getInfRule(Good())).thenReturn(Some(ir))
      when(pnav.get((pf, "ll"), conclLoc)).thenReturn(Some(10))
      when(rnav.get(ir, conclLoc)).thenReturn(Some(concl))

      cvtr.convert("ll", Error.Ambiguous(what, entries)) shouldBe None
    }

    it("should return none when first step is not a valid rule pos") {
      val pf = StubProof(
        rootSteps = Seq("ll", "l1"),
        map = Map(
          "ll" -> StubLine(StubFormula(), Good())
        )
      )

      val (cvtr, pnav, rnav, getInfRule) = fix(pf)
      val what = MetaTerm(Terms.T2)

      val concl = MetaFormula(Formulas.Psi)
      val ir = InfRule(Nil, concl)

      val conclLoc = Location.operand(4).root // invalid rule pos!
      val entries = List(conclLoc)

      when(getInfRule(Good())).thenReturn(Some(ir))
      when(pnav.get((pf, "ll"), conclLoc)).thenReturn(Some(10))
      when(rnav.get(ir, conclLoc)).thenReturn(Some(concl))

      cvtr.convert("ll", Error.Ambiguous(what, entries)) shouldBe None
    }

    it("should fail when id doesn't refers to box or id is invalid") {
      val pf = StubProof(
        rootSteps = Seq("box"),
        map = Map(
          "box" -> StubBox()
        )
      )

      val (cvtr, pnav, rnav, getInfRule) = fix(pf)
      val what = MetaTerm(Terms.T2)

      val concl = MetaFormula(Formulas.Psi)
      val ir = InfRule(Nil, concl)

      val conclLoc = Location.conclusion.root
      val entries = List(conclLoc)

      when(getInfRule(Good())).thenReturn(Some(ir))
      when(pnav.get((pf, "box"), conclLoc)).thenReturn(Some(10))
      when(rnav.get(ir, conclLoc)).thenReturn(Some(concl))

      cvtr.convert("box", Error.Ambiguous(what, entries)) shouldBe None
      cvtr.convert("invalid_id", Error.Ambiguous(what, entries)) shouldBe None
    }

    it("should fail when getInfRule fails") {
      val pf = StubProof(
        rootSteps = Seq("line"),
        map = Map(
          "line" -> StubLine(StubFormula(), Bad())
        )
      )

      val (cvtr, pnav, rnav, getInfRule) = fix(pf)
      val what = MetaVariable(Vars.X)

      val prem0 = And(MetaFormula(Formulas.Chi), MetaFormula(Formulas.Psi))
      val ir = InfRule(List(prem0), Contradiction())

      val prem0Loc = Location.premise(0).lhs
      val entries = List(prem0Loc)

      when(getInfRule(Bad())).thenReturn(None) // FAILS
      when(pnav.get((pf, "line"), prem0Loc)).thenReturn(Some(5))
      when(rnav.get(ir, prem0Loc)).thenReturn(Some(prem0.phi))

      cvtr.convert("line", Error.Ambiguous(what, entries)) shouldBe None
    }

    it("should fail when proof nav fails") {
      val pf = StubProof(
        rootSteps = Seq("line"),
        map = Map(
          "line" -> StubLine(StubFormula(), Bad())
        )
      )

      val (cvtr, pnav, rnav, getInfRule) = fix(pf)
      val what = MetaVariable(Vars.X)

      val prem0 = And(MetaFormula(Formulas.Chi), MetaFormula(Formulas.Psi))
      val ir = InfRule(List(prem0), Contradiction())

      val prem0Loc = Location.premise(0).lhs
      val entries = List(prem0Loc)

      when(getInfRule(Bad())).thenReturn(Some(ir))
      when(pnav.get((pf, "line"), prem0Loc)).thenReturn(None) // FAILS
      when(rnav.get(ir, prem0Loc)).thenReturn(Some(prem0.phi))

      cvtr.convert("line", Error.Ambiguous(what, entries)) shouldBe None
    }

    it("should fail when rule part nav fails") {
      val pf = StubProof(
        rootSteps = Seq("line"),
        map = Map(
          "line" -> StubLine(StubFormula(), Bad())
        )
      )

      val (cvtr, pnav, rnav, getInfRule) = fix(pf)
      val what = MetaVariable(Vars.X)

      val prem0 = And(MetaFormula(Formulas.Chi), MetaFormula(Formulas.Psi))
      val ir = InfRule(List(prem0), Contradiction())

      val prem0Loc = Location.premise(0).lhs
      val entries = List(prem0Loc)

      when(getInfRule(Bad())).thenReturn(Some(ir))
      when(pnav.get((pf, "line"), prem0Loc)).thenReturn(Some(124)) // FAILS
      when(rnav.get(ir, prem0Loc)).thenReturn(None)

      cvtr.convert("line", Error.Ambiguous(what, entries)) shouldBe None
    }

    it("should fail when only second entry fails") {
      val pf = StubProof(
        rootSteps = Seq("id"),
        map = Map(
          "id" -> StubLine(StubFormula(), Bad())
        )
      )

      val (cvtr, pnav, rnav, getInfRule) = fix(pf)
      val what = MetaVariable(Vars.X)

      val prem0 = MetaFormula(Formulas.Chi)
      val concl = And(Contradiction(), MetaFormula(Formulas.Psi))
      val ir = InfRule(List(prem0), concl)

      val prem0Loc = Location.premise(0).root
      val conclLoc = Location.conclusion.rhs
      val entries = List(prem0Loc, conclLoc) // concl is second location in entries

      when(getInfRule(Bad())).thenReturn(Some(ir))
      when(pnav.get((pf, "id"), prem0Loc)).thenReturn(Some(6))
      when(pnav.get((pf, "id"), conclLoc)).thenReturn(None) // concl will fail!
      when(rnav.get(ir, prem0Loc)).thenReturn(Some(prem0))
      when(rnav.get(ir, conclLoc)).thenReturn(Some(concl.psi))

      cvtr.convert("id", Error.Ambiguous(what, entries)) shouldBe None 
    }
  }

  describe("convert Miscellaneous") {
    it("should convert with conclusion") {
      val pf = StubProof(Seq("line"), Map("line" -> StubLine()))
      val (cvtr, _, _, _) = fix(pf)

      cvtr.convert("line", Error.Miscellaneous(Location.conclusion, "msg")) shouldBe Some(
        OutputError.Miscellaneous("line", "conclusion", "msg")
      )
    }

    it("should convert with premises") {
      val pf = StubProof(Seq("line"), Map("line" -> StubLine(StubFormula(), Good(), Seq("0", "1", "2"))))
      val (cvtr, _, _, _) = fix(pf)

      cvtr.convert("line", Error.Miscellaneous(Location.premise(0), "msg 010")) shouldBe Some(
        OutputError.Miscellaneous("line", "premise 0", "msg 010")
      )

      cvtr.convert("line", Error.Miscellaneous(Location.premise(2), "msg afsljk")) shouldBe Some(
        OutputError.Miscellaneous("line", "premise 2", "msg afsljk")
      )
    }

    it("should return none when loc is empty") {
      val pf = StubProof(Seq("line"), Map("line" -> StubLine(StubFormula(), Good(), Seq("0", "1", "2"))))
      val (cvtr, _, _, _) = fix(pf)

      cvtr.convert("line", Error.Miscellaneous(Location.root, "msg 010")) shouldBe None
    }

    it("should return none when loc is invalid") {
      val pf = StubProof(Seq("line"), Map("line" -> StubLine(StubFormula(), Good(), Seq("0", "1", "2"))))
      val (cvtr, _, _, _) = fix(pf)

      cvtr.convert("line", Error.Miscellaneous(Location.firstLine, "msg 010")) shouldBe None
    }
  }
  
  describe("convert rest") {
    it("should convert rest fine") {
      val pf = StubProof(Seq("id", "id2"), Map("id" -> StubLine(), "id2" -> StubBox()))
      val (cvtr, _, _, _) = fix(pf)

      cvtr.convert("id", Error.ReferenceOutOfScope(0)) shouldBe Some(OutputError.RefErr("id", "ReferenceOutOfScope", 0))
      cvtr.convert("id", Error.ReferenceToLaterStep(0)) shouldBe Some(OutputError.RefErr("id", "ReferenceToLaterStep", 0))
      cvtr.convert("id", Error.ReferenceToUnclosedBox(1)) shouldBe Some(OutputError.RefErr("id", "ReferenceToUnclosedBox", 1))
      cvtr.convert("id", Error.ReferenceBoxMissingFreshVar(0)) shouldBe Some(OutputError.RefErr("id", "ReferenceBoxMissingFreshVar", 0))
      cvtr.convert("id", Error.ReferenceShouldBeBox(0)) shouldBe Some(OutputError.RefErr("id", "ReferenceShouldBeBox", 0))
      cvtr.convert("id", Error.ReferenceShouldBeLine(0)) shouldBe Some(OutputError.RefErr("id", "ReferenceShouldBeLine", 0))
      cvtr.convert("id", Error.MissingRef(4)) shouldBe Some(OutputError.RefErr("id", "MissingRef", 4))
      cvtr.convert("id", Error.MissingFormula()) shouldBe Some(OutputError.Simple("id", "MissingFormula"))
      cvtr.convert("id", Error.MissingRule()) shouldBe Some(OutputError.Simple("id", "MissingRule"))

      cvtr.convert("id", Error.WrongNumberOfReferences(14, 15)) shouldBe Some(
        OutputError.WrongNumberOfReferences("id", 14, 15)
      )
    }
  }
}
