package logicbox.marshal

import spray.json._

case class JustificationWriter[R, Id](
  ruleWriter: JsonWriter[R], idWriter: JsonWriter[Id]
) extends JsonWriter[Justification[R, Id]] {
  override def write(just: Justification[R, Id]): JsValue = just match {
    case Justification(rule, refs) => JsObject(
      "rule" -> ruleWriter.write(rule),
      "refs" -> JsArray(refs.map(idWriter.write).toList)
    )
  }
}
