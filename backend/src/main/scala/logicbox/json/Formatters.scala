package logicbox.json

import spray.json.{JsonFormat, DefaultJsonProtocol, RootJsonFormat, JsValue, JsString, DeserializationException}
import spray.json.RootJsonReader

object SprayFormatters extends DefaultJsonProtocol {
  implicit val formulaFormat: JsonFormat[JsonFormula] = jsonFormat3(JsonFormula.apply)
  implicit val justificationFormat: JsonFormat[JsonJustification] = jsonFormat2(JsonJustification.apply)
  implicit val proofLineFormat: JsonFormat[JsonProofLine] = jsonFormat4(JsonProofLine.apply)
  implicit val proofBoxFormat: JsonFormat[JsonProofBox] = lazyFormat(jsonFormat3(JsonProofBox.apply))

  implicit object proofStepFormat extends RootJsonFormat[JsonProofStep] {
    def write(obj: JsonProofStep): JsValue = obj match {
      case line: JsonProofLine => proofLineFormat.write(line)
      case box: JsonProofBox => proofBoxFormat.write(box)
    }

    def read(json: JsValue): JsonProofStep = {
      val jsObject = json.asJsObject
      jsObject.fields("stepType") match {
        case JsString("line") => json.convertTo[JsonProofLine]
        case JsString("box") => json.convertTo[JsonProofBox]
        case other => throw DeserializationException(s"Unknown stepType: $other")
      }
    }
  }

  implicit val proofFormat: RootJsonFormat[JsonProof] = listFormat
}
