package logicbox.server.format

import logicbox.framework.Error
import logicbox.framework.Error._

import spray.json.{JsonFormat, DefaultJsonProtocol, RootJsonFormat, JsValue, JsString, DeserializationException}
import spray.json.RootJsonReader
import spray.json.JsObject
import spray.json.JsonWriter

object SprayFormatters extends DefaultJsonProtocol {
  implicit val rawFormulaFormat: JsonFormat[RawFormula] = jsonFormat3(RawFormula.apply)
  implicit val rawJustificationFormat: JsonFormat[RawJustification] = jsonFormat2(RawJustification.apply)
  implicit val rawProofLineFormat: JsonFormat[RawProofLine] = jsonFormat4(RawProofLine.apply)
  implicit val rawBoxInfoFormat: JsonFormat[RawBoxInfo] = jsonFormat1(RawBoxInfo.apply)

  implicit object rawProofBoxFormat extends JsonFormat[RawProofBox] {
    private def asString(js: JsValue): Option[String] = js match {
      case JsString(s) => Some(s)
      case _ => None
    }
    override def read(json: JsValue): RawProofBox = (json match {
      case JsObject(fields) => for {
        uuid <- fields.get("uuid").flatMap(asString)
        stepType <- fields.get("stepType").flatMap(asString)
        proof <- fields.get("proof")
        boxInfo = fields.get("boxInfo").map(rawBoxInfoFormat.read)
      } yield RawProofBox(
        uuid = uuid,
        stepType = stepType,
        proof = rawProofFormat.read(proof),
        boxInfo = boxInfo.getOrElse(RawBoxInfo(None))
      )
      case _ => None
    }).getOrElse(throw DeserializationException(s"${json.prettyPrint} is not a valid proof box"))

    override def write(obj: RawProofBox): JsValue = jsonFormat4(RawProofBox.apply).write(obj)
  }

  implicit object rawProofStepFormat extends RootJsonFormat[RawProofStep] {
    def write(obj: RawProofStep): JsValue = obj match {
      case line: RawProofLine => rawProofLineFormat.write(line)
      case box: RawProofBox => rawProofBoxFormat.write(box)
    }

    def read(json: JsValue): RawProofStep = {
      val jsObject = json.asJsObject
      jsObject.fields("stepType") match {
        case JsString("line") => json.convertTo[RawProofLine]
        case JsString("box") => json.convertTo[RawProofBox]
        case other => throw DeserializationException(s"Unknown stepType: $other")
      }
    }
  }

  implicit val rawProofFormat: RootJsonFormat[RawProof] = listFormat

}
