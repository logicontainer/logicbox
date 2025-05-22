package logicbox.server.format

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors
import logicbox.proof.ProofImpl
import logicbox.proof.ProofBoxImpl
import logicbox.framework.IncompleteProof
import logicbox.framework.IncompleteFormula
import logicbox.proof.ProofLineImpl
import logicbox.server.RawProofToIncompleteProofConverter
import logicbox.server.format.RawProofBox
import logicbox.server.format.RawProofLine
import logicbox.server.format.RawFormula
import logicbox.server.format.RawJustification

class RawProofToIncompleteProofConverterTest extends AnyFunSpec {
  describe("convertToRaw") {
    it("should work with boxes") {
      val proof: IncompleteProof[String, String, Unit, String] = ProofImpl(
        map = Map(
          "b" -> ProofBoxImpl((), Seq("l1", "l2")),
          "l1" -> ProofLineImpl(
            IncompleteFormula("p", Some("p")),
            Some("r1"),
            Seq()
          ),
          "l2" -> ProofLineImpl(
            IncompleteFormula("q", Some("q")),
            Some("r2"),
            Seq()
          ),
        ),
        rootSteps = Seq("b")
      )

      val conv = RawProofToIncompleteProofConverter[String, String](
        parseFormula = f => Some(f),
        parseRule = r => Some(r),
        formulaToAscii = s => s,
        formulaToLatex = s => s"${s}_LATEX",
        ruleToString = r => r
      )

      conv.convertToRaw(proof) shouldBe List(
        RawProofBox(
          uuid = "b",
          stepType = "box",
          proof = List(
            RawProofLine(
              uuid = "l1",
              stepType = "line",
              formula = RawFormula(
                userInput = "p",
                latex = Some("p_LATEX"),
                ascii = Some("p"),
              ),
              justification = RawJustification(
                rule = Some("r1"),
                refs = List()
              )
            ),

            RawProofLine(
              uuid = "l2",
              stepType = "line",
              formula = RawFormula(
                userInput = "q",
                latex = Some("q_LATEX"),
                ascii = Some("q"),
              ),
              justification = RawJustification(
                rule = Some("r2"),
                refs = List()
              )
            ),

          )
        )
      )
    }
  }

  describe("convertFromRaw") {
    it("should work with nested boxes") {
      val conv = RawProofToIncompleteProofConverter[String, String](
        parseFormula = f => Some(f),
        parseRule = r => Some(r),
        formulaToAscii = s => s,
        formulaToLatex = s => s"${s}_LATEX",
        ruleToString = r => r
      )

      val rawPf: RawProof = List(
        RawProofBox(
          uuid = "b",
          stepType = "box",
          proof = List(
            RawProofBox(
              uuid = "b1",
              stepType = "box",
              proof = List(
                RawProofLine(
                  uuid = "l2",
                  stepType = "line",
                  formula = RawFormula(
                    userInput = "q",
                    latex = Some("q_LATEX"),
                    ascii = Some("q"),
                  ),
                  justification = RawJustification(
                    rule = Some("r2"),
                    refs = List()
                  )
                ),
              )
            ),
          )
        )
      )

      conv.convertFromRaw(rawPf) shouldBe ProofImpl(
        map = Map(
          "b" -> ProofBoxImpl((), Seq("b1")),
          "b1" -> ProofBoxImpl((), Seq("l2")),
          "l2" -> ProofLineImpl(
            IncompleteFormula("q", Some("q")),
            Some("r2"),
            Seq()
          ),
        ),
        rootSteps = Seq("b")
      )
    }
  }
}
