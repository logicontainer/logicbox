package logicbox.server.format

import io.circe._, io.circe.syntax._, io.circe.generic.semiauto._
import logicbox.server.format.OutputError.Simple
import logicbox.server.format.OutputError.RefErr
import logicbox.server.format.OutputError.WrongNumberOfReferences
import logicbox.server.format.OutputError.FreshVarEscaped
import logicbox.server.format.OutputError.ShapeMismatch
import logicbox.server.format.OutputError.Ambiguous
import logicbox.server.format.OutputError.Miscellaneous

object JsonFormatters {
  implicit def listEncoder[T: Encoder]: Encoder[List[T]] = Encoder.encodeList[T]
  implicit def listDecoder[T: Decoder]: Decoder[List[T]] = Decoder.decodeList[T]

  implicit val rawFormulaEncoder: Encoder[RawFormula] = deriveEncoder[RawFormula]
  implicit val rawJustificationEncoder: Encoder[RawJustification] = deriveEncoder[RawJustification]
  implicit val rawProofLineEncoder: Encoder[RawProofLine] = deriveEncoder[RawProofLine]
  implicit val rawBoxInfoEncoder: Encoder[RawBoxInfo] = deriveEncoder[RawBoxInfo]
  implicit val rawProofBoxEncoder: Encoder[RawProofBox] = deriveEncoder[RawProofBox]
  implicit val rawProofStepEncoder: Encoder[RawProofStep] = Encoder.instance {
    case box: RawProofBox => rawProofBoxEncoder(box)
    case line: RawProofLine => rawProofLineEncoder(line)
  }
  implicit val rawProofEncoder: Encoder[RawProof] = listEncoder[RawProofStep]

  implicit val rawFormulaDecoder: Decoder[RawFormula] = deriveDecoder[RawFormula]
  implicit val rawJustificationDecoder: Decoder[RawJustification] = deriveDecoder[RawJustification]
  implicit val rawBoxInfoDecoder: Decoder[RawBoxInfo] = deriveDecoder[RawBoxInfo]
  implicit val rawProofLineDecoder: Decoder[RawProofLine] = deriveDecoder[RawProofLine]
  implicit val rawProofBoxDecoder: Decoder[RawProofBox] = deriveDecoder[RawProofBox]
  implicit val rawProofStepDecoder: Decoder[RawProofStep] = Decoder.instance { cursor =>
    cursor.as[RawProofBox](rawProofBoxDecoder)
      .orElse(cursor.as[RawProofLine](rawProofLineDecoder))
      .left.map { errors =>
        DecodingFailure(s"Couldn't decode RawProofStep as either Box or Line. Errors: ${errors.message}", cursor.history)
      }
  }
  implicit val rawProofDecoder: Decoder[RawProof] = listDecoder[RawProofStep]

  implicit val ambiguityEntryEncoder: Encoder[OutputError.AmbiguityEntry] = deriveEncoder[OutputError.AmbiguityEntry]
  implicit val outputErrorEncoder: Encoder[OutputError] = Encoder.instance {
    case err: Simple => deriveEncoder[Simple](err)
    case err: RefErr => deriveEncoder[RefErr](err)
    case obj @ OutputError.WrongNumberOfReferences(uuid, expected, actual) => Json.obj(
      "uuid" -> uuid.asJson,
      "errorType" -> obj.errorType.asJson,
      "expected" -> expected.asJson,
      "actual" -> actual.asJson,
    )
    case obj @ OutputError.ShapeMismatch(uuid, rulePosition, expected, actual) => Json.obj(
      "uuid" -> uuid.asJson,
      "errorType" -> obj.errorType.asJson,
      "rulePosition" -> rulePosition.asJson,
      "expected" -> expected.asJson,
      "actual" -> actual.asJson,
    )
    case obj @ OutputError.Ambiguous(uuid, subject, entries) => Json.obj(
      "uuid" -> uuid.asJson,
      "errorType" -> obj.errorType.asJson,
      "subject" -> subject.asJson,
      "entries" -> entries.asJson
    )
    case obj @ OutputError.Miscellaneous(uuid, rulePosition, explanation) => Json.obj(
      "uuid" -> uuid.asJson,
      "errorType" -> obj.errorType.asJson,
      "rulePosition" -> rulePosition.asJson,
      "explanation" -> explanation.asJson
    )
    case obj @ OutputError.FreshVarEscaped(uuid, boxId, freshVar) => Json.obj(
      "uuid" -> uuid.asJson,
      "errorType" -> obj.errorType.asJson,
      "boxId" -> boxId.asJson,
      "freshVar" -> freshVar.asJson
    )
    case obj @ OutputError.RedefinitionOfFreshVar(uuid, originalUuid, freshVar) => Json.obj(
      "uuid" -> uuid.asJson,
      "errorType" -> obj.errorType.asJson,
      "originalUuid" -> originalUuid.asJson,
      "freshVar" -> freshVar.asJson
    )
  }
}
