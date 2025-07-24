package logicbox.server

import org.scalatest.funspec.AnyFunSpec

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import spray.json._
import logicbox.server.format._


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

    it("should report error when var occurs") {
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
  }
}
