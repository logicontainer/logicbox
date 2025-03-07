package logicbox.demarshal

import logicbox.marshal.Justification // todo: bad, probably package restructure
import spray.json._
import logicbox.framework._
import logicbox.framework.ModifiableProof._

object ProofJsonReader {
  case class Err(expl: String)
}

case class ProofJsonReader[F, R, Id](
  formulaParser: String => F,
  ruleParser: String => R,
  idParser: String => Id
) extends RootJsonReader[Either[ProofJsonReader.Err, List[ModifyProofCommand[F, R, Id]]]] {
  import ProofJsonReader.Err

  private def asArray(value: JsValue): Either[ProofJsonReader.Err, JsArray] = value match {
    case arr: JsArray => Right(arr)
    case _ => ??? // Left(Err(s"not an array: ${value.prettyPrint}"))
  }

  private def transform[T, U](elms: Seq[T], f: T => Either[Err, U]): Either[Err, List[U]] =
    elms.foldRight(Right(Nil): Either[Err, List[U]]) {
      case (_, s @ Left(_)) => s
      case (elm, Right(acc)) => f(elm).map(_ :: acc)
    }

  private def asStringArray(value: JsValue): Either[Err, Seq[String]] = value match {
    case JsArray(elements) => transform(elements, asString)
    case _ => ???
  }

  private def asString(value: JsValue): Either[ProofJsonReader.Err, String] = value match {
    case JsString(str) => Right(str)
    case _ => ??? // Left(Err(s"not a string: ${value.prettyPrint}"))
  }

  private def asStringOrNull(value: JsValue): Either[Err, Option[String]] = value match {
    case JsString(str) => Right(Some(str))
    case JsNull => Right(None)
    case _ => ??? // Left(Err(s"neither a string nor null: ${value.prettyPrint}"))
  }

  private def asObject(value: JsValue): Either[ProofJsonReader.Err, JsObject] = value match {
    case obj: JsObject => Right(obj)
    case _ => Left(???)
  }

  private def getField(value: JsObject, field: String): Either[ProofJsonReader.Err, JsValue] = 
    value.fields.get(field).toRight(???) // Err(s"field $field not found in object ${value.prettyPrint}"))

  private def readLine(obj: JsObject, pos: Pos[Id]): Either[Err, List[ModifyProofCommand[F, R, Id]]] = for {
    uuid <- getField(obj, "uuid")
      .flatMap(asString)
      .map(idParser)

    formula <- for {
      fobj <- getField(obj, "formula").flatMap(asObject)
      inp <- getField(fobj, "userInput")
      str <- asStringOrNull(inp)
    } yield str.map(formulaParser)

    (rule, refs) <- for {
      justobj <- getField(obj, "justification").flatMap(asObject)
      ruleInp <- getField(justobj, "rule")
      refsInp <- getField(justobj, "refs")

      rule <- asStringOrNull(ruleInp)
      refs <- asStringArray(refsInp)
    } yield (rule.map(ruleParser), refs.map(idParser))

  } yield List(AddLine(uuid, pos)) ++ (formula match {
    case None => Nil
    case Some(f) => List(UpdateFormula(uuid, f))
  }) ++ (rule match {
    case None => Nil
    case Some(r) => List(UpdateRule(uuid, r))
  }) ++ (refs match {
    case Seq() => Nil
    case refs => List(UpdateReferences(uuid, refs))
  })

  private def readBox(obj: JsObject, pos: Pos[Id]): Either[Err, List[ModifyProofCommand[F, R, Id]]] = for {
    uuid <- getField(obj, "uuid")
      .flatMap(asString)
      .map(idParser)
    steps <- for {
      pf <- getField(obj, "proof")
      steps <- asArray(pf)
    } yield steps
    cmds <- readElms(steps.elements, BoxTop(uuid))
  } yield AddBox(uuid, pos) :: cmds // fake

  private def readElm(json: JsValue, pos: Pos[Id]): Either[ProofJsonReader.Err, List[ModifyProofCommand[F, R, Id]]] = for {
    obj <- asObject(json)
    tpe <- getField(obj, "stepType").flatMap(asString)
    res <- tpe match {
      case "line" => readLine(obj, pos)
      case "box" => readBox(obj, pos)
      case _ => Left(???)
    }
  } yield res

  private def readElms(elms: Vector[JsValue], top: Pos[Id]): Either[Err, List[ModifyProofCommand[F, R, Id]]] = for {
    ids <- transform(elms, elm => for {
      obj <- asObject(elm)
      idstr <- getField(obj, "uuid").flatMap(asString)
    } yield idParser(idstr))
    
    positions = top :: ids.map(id => AtLine(id, Direction.Below)).dropRight(1)

    res <- transform(elms.zip(positions), {
      case (elm, pos) => readElm(elm, pos)
    })

  } yield res.flatten

  override def read(json: JsValue): Either[ProofJsonReader.Err, List[ModifyProofCommand[F, R, Id]]] = for {
    arr <- asArray(json)
    res <- readElms(arr.elements, ProofTop)
  } yield res
}
