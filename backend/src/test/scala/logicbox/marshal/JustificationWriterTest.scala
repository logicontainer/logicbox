import logicbox.marshal

import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors
import org.scalatest.funspec.AnyFunSpec

import logicbox.marshal.Justification
import logicbox.marshal.JustificationWriter
import spray.json._

class JustificationWriterTest extends AnyFunSpec {
  case class StubRule(str: String)
  private type Id = String

  describe("JustificationWriter::write") {
    it("should use delegates") {
      val writer: JsonWriter[Justification[StubRule, Id]] = JustificationWriter(
        JsonWriter.func2Writer { case StubRule(str) => JsString(s"rule $str") },
        JsonWriter.func2Writer { case id: Id => JsString(s"ID($id)") }
      )

      writer.write(Justification(StubRule("thing"), Seq("ref1", "ref2"))) shouldBe JsObject(
        "rule" -> JsString("rule thing"),
        "refs" -> JsArray(List("ID(ref1)", "ID(ref2)").map(JsString.apply))
      )
    }
  }
}
