package logicbox.server

import org.scalatest.funspec.AnyFunSpec

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import logicbox.server.format._
import logicbox.framework.ValidationResult

class IntegratePredLogicProofValidatorServiceImplTest extends AnyFunSpec {
  describe("a bug i found") {
    it("should handle unknown rules") {
      val req = List(
        RawProofLine(
          uuid = "id",
          stepType = "line",
          formula = RawFormula(
            userInput = "forall y Q(y)",
            ascii = None,
            latex = None
          ),
          justification = RawJustification(
            rule = Some("SOME_INVALID_RULE_NAME"), // burh
            refs = List()
          )
        )
      )

      PredLogicProofValidatorService().validateProof(req) should matchPattern {
        case Right(_) =>
      }
    }

    it("should report error when fresh var thing occurs") {
      val req = List(
        RawProofBox("boxid", "box", boxInfo = RawBoxInfo(freshVar = Some("x_0")), proof = Nil),
        RawProofLine(
          uuid = "id",
          stepType = "line",
          formula = RawFormula(
            userInput = "P(x_0)", // uses x_0!
            ascii = None,
            latex = None
          ),
          justification = RawJustification(
            rule = Some("premise"),
            refs = List()
          )
        )
      )

      PredLogicProofValidatorService().validateProof(req) should matchPattern {
        case Right(ValidationResult(_, List(OutputError.FreshVarEscaped("id", "boxid", "x_0")))) =>
      }
    }
  }
}
