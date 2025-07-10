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

  val NAV_FAIL = 512502

  trait RulePartGetter {
    def getRulePart(rule: StubRule, rulePos: Location): Option[RulePart]
  }

  def fix = {
    class StubFormulaNav extends Navigator[StubFormula, StubFormula] {
      override def get(subject: StubFormula, loc: Location): Option[StubFormula] = {
        if subject.i == NAV_FAIL then None else
        Some(StubFormula(subject.i))
      }
    }

    class BoxNav extends Navigator[StubBoxInfo, StubFormula] {
      override def get(subject: StubBoxInfo, loc: Location): Option[StubFormula] = ???
    }

  
    val rpg = mock[RulePartGetter]
    when(rpg.getRulePart(any(), any())).thenReturn(None)

    def stubFormulaToString(f: StubFormula): String = s"sf(${f.i.toString})"
    def rulePartToLaTeX(r: RulePart): String = s"--${r.toString}"
    def getRulePart(rule: StubRule, rulePos: Location): Option[RulePart] = rpg.getRulePart(rule, rulePos)

    val pnav = ProofNavigator[F, B, String, F](StubFormulaNav(), BoxNav())

    (ErrorConverterImpl[F, R, B](getRulePart, stubFormulaToString, rulePartToLaTeX, pnav, RulePartNavigator()), rpg)
  }

  describe("convert") {
    it("should convert shape mismatch on formula") {
      val (cvtr, rpg) = fix
      val pf = StubProof(
        rootSteps = Seq("l1"),
        map = Map(
          "l1" -> StubLine(StubFormula(2), Good())
        )
      )

      val part = RulePart.Equals(MetaVariable(Vars.X), MetaVariable(Vars.X))
      when(rpg.getRulePart(Good(), Location.conclusion)).thenReturn(Some(part))

      cvtr.convert(pf, "l1", Error.ShapeMismatch(Location.conclusion.root)) shouldBe Some(OutputError.ShapeMismatch(
        uuid = "l1",
        rulePosition = "conclusion",
        expected = s"--${part.toString}",
        actual = "sf(2)"
      ))
    }

    it("should convert shape mismatch on formula 2") {
      val (cvtr, rpg) = fix
      val pf = StubProof(
        rootSteps = Seq("l2"),
        map = Map(
          "l2" -> StubLine(StubFormula(3), Good())
        )
      )

      val part = RulePart.Equals(MetaTerm(Terms.T), MetaTerm(Terms.T))
      when(rpg.getRulePart(Good(), Location.conclusion)).thenReturn(Some(part))

      cvtr.convert(pf, "l2", Error.ShapeMismatch(Location.conclusion)) shouldBe Some(OutputError.ShapeMismatch(
        uuid = "l2",
        rulePosition = "conclusion",
        expected = s"--${part.toString}",
        actual = "sf(3)"
      ))
    }

    it("should convert shape mismatch on ref 0") {
      val (cvtr, rpg) = fix
      val pf = StubProof(
        rootSteps = Seq("l1", "ref"),
        map = Map(
          "ref" -> StubLine(StubFormula(1)),
          "l1" -> StubLine(StubFormula(2), Good(), Seq("ref"))
        )
      )

      val part = RulePart.Equals(MetaTerm(Terms.T), MetaTerm(Terms.T))
      when(rpg.getRulePart(Good(), Location.premise(0))).thenReturn(Some(part))

      cvtr.convert(pf, "l1", Error.ShapeMismatch(Location.premise(0))) shouldBe Some(OutputError.ShapeMismatch(
        uuid = "l1",
        rulePosition = "premise 0",
        expected = s"--${part.toString}",
        actual = "sf(1)"
      ))
    }

    it("should convert shape mismatch on ref 1") {
      val (cvtr, rpg) = fix
      val pf = StubProof(
        rootSteps = Seq("l1", "ref"),
        map = Map(
          "r0" -> StubLine(StubFormula(1)),
          "r1" -> StubLine(StubFormula(2)),
          "l1" -> StubLine(StubFormula(2), Good(), Seq("r0", "r1"))
        )
      )

      val part = RulePart.Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T1))
      when(rpg.getRulePart(Good(), Location.premise(1))).thenReturn(Some(part))

      cvtr.convert(pf, "l1", Error.ShapeMismatch(Location.premise(1))) shouldBe Some(OutputError.ShapeMismatch(
        uuid = "l1",
        rulePosition = "premise 1",
        expected = s"--${part.toString}",
        actual = "sf(2)"
      ))
    }
    
    it("should convert shape mismatch on assumption of box") {
      val (cvtr, rpg) = fix
      val pf = StubProof(
        rootSteps = Seq("box"),
        map = Map(
          "ass" -> StubLine(StubFormula(123)),
          "concl" -> StubLine(StubFormula(456)),
          "box" -> StubBox(StubBoxInfo(), Seq("ass", "concl")),
          "line" -> StubLine(StubFormula(), Good(), Seq("box"))
        )
      )

      val assRulePart = RulePart.Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T2))
      val part = TemplateBox(ass = Some(assRulePart), concl = None, freshVar = None)
      when(rpg.getRulePart(Good(), Location.premise(0))).thenReturn(Some(part))

      cvtr.convert(pf, "line", Error.ShapeMismatch(Location.premise(0).firstLine)) shouldBe Some(OutputError.ShapeMismatch(
        uuid = "line",
        rulePosition = "premise 0",
        expected = s"--${assRulePart.toString}",
        actual = "sf(123)"
      ))
    }

    it("should return none when rule part location is not valid") {
      val (cvtr, rpg) = fix
      val pf = StubProof(
        rootSteps = Seq("box"),
        map = Map(
          "ass" -> StubLine(StubFormula(123)),
          "concl" -> StubLine(StubFormula(456)),
          "box" -> StubBox(StubBoxInfo(), Seq("ass", "concl")),
          "line" -> StubLine(StubFormula(), Good(), Seq("box"))
        )
      )

      // rule is t1 = t1
      val part = RulePart.Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T1))
      when(rpg.getRulePart(Good(), Location.premise(0))).thenReturn(Some(part))

      // but we ask for operand 3
      cvtr.convert(pf, "line", Error.ShapeMismatch(Location.premise(0).operand(3))) shouldBe None
    }

    it("should return none when formula nav fails") {
      val (cvtr, rpg) = fix
      val pf = StubProof(
        rootSteps = Seq("line"),
        map = Map(
          "line" -> StubLine(StubFormula(NAV_FAIL))
        )
      )

      val part = RulePart.Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T1))
      when(rpg.getRulePart(Good(), Location.conclusion)).thenReturn(Some(part))

      cvtr.convert(pf, "line", Error.ShapeMismatch(Location.conclusion.root)) shouldBe None
    }
  
    it("should return none if id is invalid") {
      val (cvtr, rpg) = fix
      val pf = StubProof()

      val part = RulePart.Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T1))
      when(rpg.getRulePart(Good(), Location.premise(1))).thenReturn(Some(part))

      cvtr.convert(pf, "invalid_id", Error.ShapeMismatch(Location.conclusion.root)) shouldBe None
    }

    it("should return none when try to get ref of box") {
      val (cvtr, rpg) = fix
      val pf = StubProof(
        Seq("box"), Map("box" -> StubBox())
      )

      val part = RulePart.Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T1))
      when(rpg.getRulePart(Good(), Location.premise(0))).thenReturn(Some(part))

      cvtr.convert(pf, "box", Error.ShapeMismatch(Location.premise(0).root)) shouldBe None
    }

    it("should convert ambiguous error with no entries") {
      val (cvtr, rpg) = fix
      val pf = StubProof(
        rootSteps = Seq("l1", "l2"),
        map = Map("l1" -> StubLine(), "l2" -> StubLine())
      )

      val what = MetaFormula(Formulas.Phi)

      cvtr.convert(pf, "l1", Error.Ambiguous(what, Nil)) shouldBe Some(
        OutputError.Ambiguous(
          uuid = "l1", 
          subject = s"--${what.toString}",
          entries = Nil
        )
      )

      cvtr.convert(pf, "l2", Error.Ambiguous(what, Nil)) shouldBe Some(
        OutputError.Ambiguous(
          uuid = "l2", 
          subject = s"--${what.toString}",
          entries = Nil
        )
      )
    }

    it("should convert a single entry") {
      val (cvtr, rpg) = fix
      val pf = StubProof(
        rootSteps = Seq("line"),
        map = Map("line" -> StubLine(StubFormula(144), Good()))
      )

      val what = MetaFormula(Formulas.Chi)
      val err = Error.Ambiguous(what, List(
        Location.conclusion.root
      ))


      val conclusionRulePart = RulePart.Implies(MetaFormula(Formulas.Phi), MetaFormula(Formulas.Chi))
      when(rpg.getRulePart(Good(), Location.conclusion)).thenReturn(Some(conclusionRulePart))

      cvtr.convert(pf, "line", err) shouldBe Some(OutputError.Ambiguous(
        uuid = "line",
        subject = s"--${what.toString}",
        entries = List(AmbiguityEntry(
          rulePosition = "conclusion",
          meta = s"--${conclusionRulePart.toString}",
          actual = "sf(144)"
        ))
      ))
    }

    it("should convert multiple entries with refs") {
      val (cvtr, rpg) = fix
      val pf = StubProof(
        rootSteps = Seq("l", "r0", "r1"),
        map = Map(
          "r0" -> StubLine(StubFormula(10)),
          "r1" -> StubLine(StubFormula(11)),
          "l" -> StubLine(StubFormula(144), Good(), Seq("r0", "r1"))
        )
      )

      val what = MetaFormula(Formulas.Phi)
      val err = Error.Ambiguous(what, List(
        Location.premise(0).root,
        Location.premise(1).root
      ))

      val premise0RulePart = Substitution(MetaFormula(Formulas.Phi), MetaTerm(Terms.T), MetaVariable(Vars.X))
      val premise1RulePart = MetaFormula(Formulas.Phi)
      when(rpg.getRulePart(Good(), Location.premise(0))).thenReturn(Some(premise0RulePart))
      when(rpg.getRulePart(Good(), Location.premise(1))).thenReturn(Some(premise1RulePart))

      cvtr.convert(pf, "l", err) shouldBe Some(OutputError.Ambiguous(
        uuid = "l",
        subject = s"--${what.toString}",
        entries = List(
          AmbiguityEntry(
            rulePosition = "premise 0",
            meta = s"--${premise0RulePart.toString}",
            actual = "sf(10)"
          ),
          AmbiguityEntry(
            rulePosition = "premise 1",
            meta = s"--${premise1RulePart.toString}",
            actual = "sf(11)"
          )
        )
      ))
    }

    it("should work with locations in entries") {
      val (cvtr, rpg) = fix
      val pf = StubProof(
        rootSteps = Seq("line"),
        map = Map("line" -> StubLine(StubFormula(123), Good()))
      )

      val what = MetaFormula(Formulas.Chi)
      val err = Error.Ambiguous(what, List(
        Location.conclusion.rhs // RIGHT HAND SIDE!!
      ))


      val conclusionRulePart = RulePart.Implies(MetaFormula(Formulas.Phi), MetaFormula(Formulas.Chi))
      val thePartThatShouldBeHighlighted = conclusionRulePart.psi
      when(rpg.getRulePart(Good(), Location.conclusion)).thenReturn(Some(conclusionRulePart))

      cvtr.convert(pf, "line", err) shouldBe Some(OutputError.Ambiguous(
        uuid = "line",
        subject = s"--${what.toString}",
        entries = List(AmbiguityEntry(
          rulePosition = "conclusion",
          meta = s"--${thePartThatShouldBeHighlighted.toString}",
          actual = "sf(124)" // FHS WILL BE + 1!!
        ))
      ))
    }

    it("should not convert ambiguous error when uuid is invalid") {
      val (cvtr, rpg) = fix
      val pf = StubProof()
      cvtr.convert(pf, "invalid_id", Error.Ambiguous(MetaFormula(Formulas.Phi), Nil)) shouldBe None
    }

    it("should be none when rule part is not specified") {
      val (cvtr, rpg) = fix
      val pf = StubProof(
        rootSteps = Seq("line"),
        map = Map("line" -> StubLine(StubFormula(12), Good()))
      )

      val what = MetaFormula(Formulas.Chi)
      val err = Error.Ambiguous(what, List(
        Location.conclusion.root
      ))

      // rpg not set up -> getRulePart will be None

      cvtr.convert(pf, "line", err) shouldBe None
    }

    it("should be none when location in rule part doesn't make sense") {
      val (cvtr, rpg) = fix
      val pf = StubProof(
        rootSteps = Seq("line"),
        map = Map("line" -> StubLine(StubFormula(12), Good()))
      )

      val what = MetaFormula(Formulas.Chi)
      val err = Error.Ambiguous(what, List(
        Location.premise(0).lhs
      ))
    
      val part = what // there is no LHS of chi!
      when(rpg.getRulePart(Good(), Location.premise(0))).thenReturn(Some(part))

      cvtr.convert(pf, "line", err) shouldBe None
    }

    it("should be none when refers to invalid id") {
      val (cvtr, rpg) = fix
      val pf = StubProof(
        rootSteps = Seq("line"),
        map = Map("line" -> StubLine(StubFormula(12), Good(), Seq("invalid_ref")))
      )

      val what = MetaFormula(Formulas.Chi)
      val err = Error.Ambiguous(what, List(
        Location.premise(0).root
      ))
    
      val part = what
      when(rpg.getRulePart(Good(), Location.premise(0))).thenReturn(Some(part))

      cvtr.convert(pf, "line", err) shouldBe None
    }

    it("should be none when try to get ref 1 when there are no refs") {
      val (cvtr, rpg) = fix
      val pf = StubProof(
        rootSteps = Seq("line"),
        map = Map("line" -> StubLine(StubFormula(12), Good())) // NO REFS
      )

      val what = MetaFormula(Formulas.Chi)
      val err = Error.Ambiguous(what, List(
        Location.premise(1).root // REF 1???
      ))
    
      val part = what
      when(rpg.getRulePart(Good(), Location.premise(1))).thenReturn(Some(part))

      cvtr.convert(pf, "line", err) shouldBe None
    }

    it("should fail if just a single entry is invalid") {
      val (cvtr, rpg) = fix
      val pf = StubProof(
        rootSteps = Seq("l", "r1"),
        map = Map(
          "r0" -> StubLine(StubFormula(11)),
          "l" -> StubLine(StubFormula(144), Good(), Seq("r0", "r1")) // r1 DOESNT EXIST!
        )
      )

      val what = MetaFormula(Formulas.Phi)
      val err = Error.Ambiguous(what, List(
        Location.premise(0).root, 
        Location.premise(1).root // REFER TO r0
      ))

      val premise0RulePart = Substitution(MetaFormula(Formulas.Phi), MetaTerm(Terms.T), MetaVariable(Vars.X))
      val premise1RulePart = MetaFormula(Formulas.Phi)
      when(rpg.getRulePart(Good(), Location.premise(0))).thenReturn(Some(premise0RulePart))
      when(rpg.getRulePart(Good(), Location.premise(1))).thenReturn(Some(premise1RulePart))

      cvtr.convert(pf, "l", err) shouldBe None
    }
  }

  
}
