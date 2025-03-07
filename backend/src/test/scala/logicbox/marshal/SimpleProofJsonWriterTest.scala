package logicbox.marshal

import logicbox.framework.{Reference}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import logicbox.framework.ModifiableProof._
import logicbox.framework.Proof
import logicbox.framework.ModifiableProof

import spray.json._

class SimpleProofJsonWriterTest extends AnyFunSpec {
  import logicbox.proof.ProofStubs._

  private type W = JsonWriter[Proof[StubFormula, StubRule, ?, Id]]
  describe("SimpleProofJsonWriter::write") {
    it("should write single line using dumb delegates") {
      val writer: W = SimpleProofJsonWriter(
        JsonWriter.func2Writer(_ => JsString("id")),
        JsonWriter.func2Writer(_ => JsString("formula")),
        JsonWriter.func2Writer(_ => JsString("justification")),
      )

      val proof = StubProof(
        rootSteps = Seq("step"),
        map = Map("step" -> StubLine())
      )

      val stepJson = JsObject(
        "uuid" -> JsString("id"),
        "stepType" -> JsString("line"),
        "formula" -> JsString("formula"),
        "justification" -> JsString("justification"),
      )

      writer.write(proof) shouldBe JsArray(stepJson)
    }

    val writer: W = SimpleProofJsonWriter(
      JsonWriter.func2Writer { case id: Id => JsString(id) },
      JsonWriter.func2Writer { case f: F => JsNumber(f.i) },
      JsonWriter.func2Writer { case Justification(rule, refs) => JsObject(
        "rule" -> JsString(rule.toString),
        "refs" -> JsArray(refs.map(_.toString).map(JsString(_)).toList)
      )}
    )

    it("should write single box using not-dumb delegates") {
      val proof = StubProof(
        rootSteps = Seq("box"),
        map = Map(
          "box" -> StubBox()
        )
      )

      val stepJson = JsObject(
        "stepType" -> JsString("box"),
        "uuid" -> JsString("box"),
        "proof" -> JsArray()
      )

      writer.write(proof) shouldBe JsArray(stepJson)
    }

    it("should write nested proof") {
      val proof = StubProof(
        rootSteps = Seq("l0", "box"), 
        map = Map(
          "l0" -> StubLine(StubFormula(0)),
          "box" -> StubBox(
            steps = Seq("l1", "l2")
          ),
          "l1" -> StubLine(StubFormula(1), rule = Bad()),
          "l2" -> StubLine(formula = StubFormula(2), refs = Seq("l1"))
        )
      )

      val json = JsArray(
        JsObject(
          "uuid" -> JsString("l0"),
          "stepType" -> JsString("line"),
          "formula" -> JsNumber(0),
          "justification" -> JsObject(
            "rule" -> JsString("Good()"),
            "refs" -> JsArray()
          )
        ),
        JsObject(
          "stepType" -> JsString("box"),
          "uuid" -> JsString("box"),
          "proof" -> JsArray(
            JsObject(
              "uuid" -> JsString("l1"),
              "stepType" -> JsString("line"),
              "formula" -> JsNumber(1),
              "justification" -> JsObject(
                "rule" -> JsString("Bad()"),
                "refs" -> JsArray()
              )
            ),
            JsObject(
              "uuid" -> JsString("l2"),
              "stepType" -> JsString("line"),
              "formula" -> JsNumber(2),
              "justification" -> JsObject(
                "rule" -> JsString("Good()"),
                "refs" -> JsArray(JsString("l1"))
              )
            )
          )
        )
      )

      writer.write(proof) shouldBe json
    }
  }
}
