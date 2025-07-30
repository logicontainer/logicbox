package logicbox.server.format

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import io.circe._, io.circe.syntax._, io.circe.generic.semiauto._, io.circe.parser._

class JSONStuff extends AnyFunSpec {
  describe("how circe decodes a list?") {
    import JsonFormatters._
    it("should not be dumb") { 
      // println(List(
      //   RawProofBox(
      //     uuid = "alsdjlAKSJd",
      //     stepType = "box",
      //     boxInfo = RawBoxInfo(Some("x_0")),
      //     proof = List(
      //       RawProofLine(
      //         uuid = "ajsdlakjdlk",
      //         stepType = "line",
      //         formula = RawFormula(
      //           userInput = "yess",
      //           latex = None,
      //           ascii = None
      //         ),
      //         justification = RawJustification(
      //           refs = List("a", "b"),
      //           rule = Some("implies_elim")
      //         )
      //       )
      //     )
      //   )
      // ).asJson)
    }

    it("should decode") {
      val proofString = ("""[
      |  {
      |    "uuid": "l1", 
      |    "stepType": "line",
      |    "formula": {
      |      "userInput": "p -> q"
      |    },
      |    "justification": {
      |      "rule": "premise",
      |      "refs": []
      |    }
      |  }, {
      |    "uuid": "l2", 
      |    "stepType": "line",
      |    "formula": {
      |      "userInput": "not not p"
      |    },
      |    "justification": {
      |      "rule": "premise",
      |      "refs": []
      |    }
      |  }, {
      |    "uuid": "l3", 
      |    "stepType": "line",
      |    "formula": {
      |      "userInput": "p"
      |    },
      |    "justification": {
      |      "rule": "not_not_elim",
      |      "refs": ["l2"]
      |    }
      |  }, {
      |    "uuid": "l4", 
      |    "stepType": "line",
      |    "formula": {
      |      "userInput": "q"
      |    },
      |    "justification": {
      |      "rule": "implies_elim",
      |      "refs": ["l3", "l1"]
      |    }
      |  }, {
      |    "uuid": "l5", 
      |    "stepType": "line",
      |    "formula": {
      |      "userInput": "not not q"
      |    },
      |    "justification": {
      |      "rule": "not_not_intro",
      |      "refs": ["l3"]
      |    }
      |  }
      |]
      """.stripMargin)

      // println(decode[RawProof](proofString))
      // println((OutputError.ShapeMismatch(
      //   uuid = "asldjkasdkl",
      //   rulePosition = "premise 0",
      //   expected = "\\psi",
      //   actual = "p",
      // ): OutputError).asJson)
    }
  }
}
