package logicbox.marshal

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import spray.json._

class IncompleteFormulaWriterTest extends AnyFunSpec {
  case class StubFormula(i: Int)
  private type F = StubFormula
  private type W = JsonWriter[IncompleteFormula[F]]
  describe("IncompleteFormulaWriter::write") {

    val writer: W = IncompleteFormulaWriter(
      { case StubFormula(i) => s"LATEX$i"},
      { case StubFormula(i) => s"ASCII$i"},
    )

    it("should give user input and nulls when formula is none") {
      writer.write(IncompleteFormula("USER INPUT", None)) shouldBe JsObject(
        "userInput" -> JsString("USER INPUT"),
        "ascii" -> JsNull,
        "latex" -> JsNull
      )
    }

    it("should give correct formulas when formula is some") {
      writer.write(IncompleteFormula("USER INPUTTTT", Some(StubFormula(5)))) shouldBe JsObject(
        "userInput" -> JsString("USER INPUTTTT"),
        "ascii" -> JsString("ASCII5"),
        "latex" -> JsString("LATEX5"),
      )
    }
  }
}
