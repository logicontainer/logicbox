package logicbox.demarshal

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors
import logicbox.framework.ModifyProofCommand

import spray.json._
import logicbox.framework._
import logicbox.framework.ModifiableProof._

class ProofJsonReaderTest extends AnyFunSpec {
  import logicbox.proof.ProofStubs._

  def formulaParser(str: String): StubFormula = StubFormula(str.charAt(0) - '0')
  def ruleParser(str: String): StubRule = str match {
    case "good" => Good()
    case _ => Bad()
  }
  def idParser(str: String) = s"HEY $str"
  
  val reader: JsonReader[Either[ProofJsonReader.Err, List[ModifyProofCommand[F, R, Id]]]] = 
    ProofJsonReader(formulaParser, ruleParser, idParser)

  describe("read") {
    it("should turn empty proof into empty command list") {
      val json = JsArray()
      reader.read(json) shouldBe Right(Nil)
    }

    it("should turn single empty line step into single command") {
      val json = JsArray(JsObject(
        "stepType" -> JsString("line"),
        "uuid" -> JsString("some_id"),
        "formula" -> JsObject(
          "userInput" -> JsNull // empty formula
        ),
        "justification" -> JsObject(
          "rule" -> JsNull,     // empty rule
          "refs" -> JsArray()   // empty reference
        )
      ))

      reader.read(json) shouldBe Right(List(
        AddLine("HEY some_id", ProofTop)
      ))
    }

    it("should have empty line and add formula steps from single step w/ formula") {
      val json = JsArray(JsObject(
        "stepType" -> JsString("line"),
        "uuid" -> JsString("some_id"),
        "formula" -> JsObject(
          "userInput" -> JsString("2")
        ),
        "justification" -> JsObject(
          "rule" -> JsNull,
          "refs" -> JsArray()
        )
      ))

      reader.read(json) shouldBe Right(List(
        AddLine("HEY some_id", ProofTop),
        UpdateFormula("HEY some_id", StubFormula(2))
      ))
    }

    it("should have empty line and add rule from single step w/ rule") {
      val json = JsArray(JsObject(
        "stepType" -> JsString("line"),
        "uuid" -> JsString("some_id"),
        "formula" -> JsObject(
          "userInput" -> JsNull,
        ),
        "justification" -> JsObject(
          "rule" -> JsString("good"),
          "refs" -> JsArray()
        )
      ))

      reader.read(json) shouldBe Right(List(
        AddLine("HEY some_id", ProofTop),
        UpdateRule("HEY some_id", Good())
      ))
    }

    it("should have empty line and add refs from single step w/ refs") {
      val json = JsArray(JsObject(
        "stepType" -> JsString("line"),
        "uuid" -> JsString("some_id"),
        "formula" -> JsObject(
          "userInput" -> JsNull,
        ),
        "justification" -> JsObject(
          "rule" -> JsNull,
          "refs" -> JsArray(JsString("ref"))
        )
      ))

      reader.read(json) shouldBe Right(List(
        AddLine("HEY some_id", ProofTop),
        UpdateReferences("HEY some_id", Seq("HEY ref"))
      ))
    }

    it("should turn empty box into single cmd") {
      val json = JsArray(JsObject(
        "stepType" -> JsString("box"),
        "uuid" -> JsString("box_id"),
        "proof" -> JsArray()
      ))

      reader.read(json) shouldBe Right(List(
        AddBox("HEY box_id", ProofTop)
      ))
    }

    it("should have correct add positions when inserting two lines") {
      val json = JsArray(
        JsObject(
          "stepType" -> JsString("line"),
          "uuid" -> JsString("line_1_id"),
          "formula" -> JsObject(
            "userInput" -> JsNull,
          ),
          "justification" -> JsObject(
            "rule" -> JsNull,
            "refs" -> JsArray(),
          )
        ),
        JsObject(
          "stepType" -> JsString("line"),
          "uuid" -> JsString("line_2_id"),
          "formula" -> JsObject(
            "userInput" -> JsNull,
          ),
          "justification" -> JsObject(
            "rule" -> JsNull,
            "refs" -> JsArray(),
          )
        )
      )

      reader.read(json) shouldBe Right(List(
        AddLine("HEY line_1_id", ProofTop),
        AddLine("HEY line_2_id", AtLine("HEY line_1_id", Direction.Below))
      ))
    }
  }
}
