package logicbox.server.format

import logicbox.framework.Error
import logicbox.framework.Error._

import spray.json.{JsonFormat, DefaultJsonProtocol, RootJsonFormat, JsValue, JsString, DeserializationException}
import spray.json.RootJsonReader
import spray.json.JsObject
import spray.json.JsonWriter
import logicbox.framework.ValidationResult
import logicbox.server.format.OutputError.Simple
import logicbox.server.format.OutputError.RefErr
import spray.json.JsNumber
import logicbox.server.format.OutputError.AmbiguityEntry
import spray.json.JsArray

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

  implicit object outputErrorWriter extends JsonWriter[OutputError]  {
    override def write(obj: OutputError): JsValue = obj match {
      case e @ Simple(uuid, errorType) => jsonFormat2(Simple.apply).write(e)
      case e @ RefErr(uuid, errorType, refIdx) => jsonFormat3(RefErr.apply).write(e)
      case OutputError.WrongNumberOfReferences(uuid, expected, actual) => JsObject(
        "uuid" -> JsString(uuid),
        "errorType" -> JsString(obj.errorType),
        "expected" -> JsNumber(expected),
        "actual" -> JsNumber(actual),
      )
      case OutputError.ShapeMismatch(uuid, rulePosition, expected, actual) => JsObject(
        "uuid" -> JsString(uuid),
        "errorType" -> JsString(obj.errorType),
        "rulePosition" -> JsString(rulePosition),
        "expected" -> JsString(expected),
        "actual" -> JsString(actual),
      )
      case OutputError.Ambiguous(uuid, subject, entries) => JsObject(
        "uuid" -> JsString(uuid),
        "errorType" -> JsString(obj.errorType),
        "subject" -> JsString(subject),
        "entries" -> JsArray(entries.map { jsonFormat3(OutputError.AmbiguityEntry.apply).write(_) })
      )
      case OutputError.Miscellaneous(uuid, rulePosition, explanation) => JsObject(
        "uuid" -> JsString(uuid),
        "errorType" -> JsString(obj.errorType),
        "rulePosition" -> JsString(rulePosition),
        "explanation" -> JsString(explanation)
      )
      case obj @ logicbox.server.format.OutputError.FreshVarEscaped(uuid, boxId, freshVar) => JsObject(
        "uuid" -> JsString(uuid),
        "errorType" -> JsString(obj.errorType),
        "boxId" -> JsString(boxId),
        "freshVar" -> JsString(freshVar)
      )
    }
  }
}
