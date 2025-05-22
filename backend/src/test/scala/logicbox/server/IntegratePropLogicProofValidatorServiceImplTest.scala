package logicbox.server

import org.scalatest.funspec.AnyFunSpec

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import spray.json._


class IntegratePropLogicProofValidatorServiceImplTest extends AnyFunSpec {

  describe("validateProof") {
    it("should work fine on a small example") {
      val service = PropLogicProofValidatorService()

      val json = JsonParser("""[{
        |  "formula": {
        |    "userInput": ""
        |  },
        |  "justification": {
        |    "refs": [],
        |    "rule": "premise"
        |  },
        |  "stepType": "line",
        |  "uuid": "1"
        |}, {
        |  "formula": {
        |    "userInput": "q -> s"
        |  },
        |  "justification": {
        |    "refs": [],
        |    "rule": null
        |  },
        |  "stepType": "line",
        |  "uuid": "2"
        |}, {
        |  "proof": [{
        |    "formula": {
        |      "userInput": ""
        |    },
        |    "justification": {
        |      "refs": [],
        |      "rule": "assumption"
        |    },
        |    "stepType": "line",
        |    "uuid": "3"
        |  }, {
        |    "formula": {
        |      "userInput": "q"
        |    },
        |    "justification": {
        |      "refs": ["3", "1"],
        |      "rule": null
        |    },
        |    "stepType": "line",
        |    "uuid": "4"
        |  }, {
        |    "formula": {
        |      "userInput": "s"
        |    },
        |    "justification": {
        |      "refs": ["4", "3"],
        |      "rule": "implies_elim"
        |    },
        |    "stepType": "line",
        |    "uuid": "5"
        |  }],
        |  "stepType": "box",
        |  "uuid": "box"
        |}, {
        |  "formula": {
        |    "userInput": "s or r"
        |  },
        |  "justification": {
        |    "refs": ["5"],
        |    "rule": "or_intro_1"
        |  },
        |  "stepType": "line",
        |  "uuid": "6"
        |}, {
        |  "formula": {
        |    "userInput": "p implies  s"
        |  },
        |  "justification": {
        |    "refs": ["box"],
        |    "rule": "implies_intro"
        |  },
        |  "stepType": "line",
        |  "uuid": "7"
        |}]""".stripMargin
      )

      // you may manually look at output here:
      // println(service.validateProof(json).getOrElse(???).prettyPrint)
    }
  }
}
