package logicbox.server.format

import logicbox.framework.Error
import logicbox.framework.Error._

import spray.json.{JsonFormat, DefaultJsonProtocol, RootJsonFormat, JsValue, JsString, DeserializationException}
import spray.json.RootJsonReader
import logicbox.framework.Scope
import logicbox.framework.Root
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

  private def getViolationType(diag: Error): String = diag match {
    case _ => ???
    // case RuleViolationAtStep(stepId, violation: RuleViolation) => violation match {
    //   case MissingFormula => "missingFormula"
    //   case MissingRule => "missingRule"
    //   case MissingDetailInReference(_, _) => "missingDetailInReference"
    //   case WrongNumberOfReferences(_, _, _) => "wrongNumberOfReferences"
    //   case ReferenceShouldBeBox(_, _) => "referenceShouldBeBox"
    //   case ReferenceShouldBeLine(_, _) => "referenceShouldBeLine"
    //   case ReferenceDoesntMatchRule(_, _) => "referenceDoesntMatchRule"
    //   case ReferencesMismatch(_, _) => "referencesMismatch"
    //   case FormulaDoesntMatchReference(_, _) => "formulaDoesntMatchReference"
    //   case FormulaDoesntMatchRule(_) => "formulaDoesntMatchRule"
    //   case MiscellaneousViolation(_) => "miscellaneousViolation"
    // }
    // case StepNotFound(_) => "stepNotFound"
    // case ReferenceIdNotFound(_, _, _) => "referenceIdNotFound"
    // case MalformedReference(_, _, _, _) => "malformedReference"
    // case ReferenceToLaterStep(_, _, _) => "referenceToLaterStep" 
    // case ScopeViolation(_, _, _, _, _) => "scopeViolation"
    // case ReferenceToUnclosedBox(_, _, _) => "referenceToUnclosedBox"
  }


  implicit object scopeWriter extends JsonFormat[Scope[String]] {
    override def read(json: JsValue): Scope[String] = json match {
      case JsString("root") => Root
      case JsString(id) => id
      case _ => throw DeserializationException(s"${json.prettyPrint} is not a valid scope")
    }

    override def write(obj: Scope[String]): JsValue = obj match {
      case scope: String => JsString(scope)
      case Root => JsString("root")
    }
  }

  def writeDiagnostic(diag: (String, Error)): JsValue = {
    ???
  //   val json = diag match {
  //     case RuleViolationAtStep(stepString, violation) => violation match {
  //       case MissingFormula | MissingRule => JsObject()
  //       case v: MissingDetailInReference => jsonFormat2(MissingDetailInReference.apply).write(v)
  //       case v: WrongNumberOfReferences => jsonFormat3(WrongNumberOfReferences.apply).write(v)
  //       case v: ReferenceShouldBeBox => jsonFormat2(ReferenceShouldBeBox.apply).write(v)
  //       case v: ReferenceShouldBeLine => jsonFormat2(ReferenceShouldBeLine.apply).write(v)
  //       case v: ReferenceDoesntMatchRule => jsonFormat2(ReferenceDoesntMatchRule.apply).write(v)
  //       case v: ReferencesMismatch => jsonFormat2(ReferencesMismatch.apply).write(v)
  //       case v: FormulaDoesntMatchReference => jsonFormat2(FormulaDoesntMatchReference.apply).write(v)
  //       case v: FormulaDoesntMatchRule => jsonFormat1(FormulaDoesntMatchRule.apply).write(v)
  //       case v: MiscellaneousViolation => jsonFormat1(MiscellaneousViolation.apply).write(v)
  //     }
  //     case d: StepNotFound[String] => jsonFormat1(StepNotFound[String].apply).write(d)
  //     case d: ReferenceIdNotFound[String] => jsonFormat3(ReferenceIdNotFound[String].apply).write(d)
  //     case d: MalformedReference[String] => jsonFormat4(MalformedReference[String].apply).write(d)
  //     case d: ReferenceToLaterStep[String] => jsonFormat3(ReferenceToLaterStep[String].apply).write(d)
  //     case d: ScopeViolation[String] => jsonFormat5(ScopeViolation[String].apply).write(d)
  //     case d: ReferenceToUnclosedBox[String] => jsonFormat3(ReferenceToUnclosedBox[String].apply).write(d)
  //   }
  //   JsObject(
  //     "uuid" -> JsString(diag.stepId),
  //     "violation" -> json,
  //     "violationType" -> JsString(getViolationType(diag))
  //   )
  }
}
