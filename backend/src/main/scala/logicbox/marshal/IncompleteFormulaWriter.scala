package logicbox.marshal

import spray.json._

case class IncompleteFormula[F](
  userInput: String, optFormula: Option[F]
)

class IncompleteFormulaWriter[F](
  toLaTeX: F => String, toASCII: F => String
) extends JsonWriter[IncompleteFormula[F]] {
  override def write(formula: IncompleteFormula[F]): JsValue = JsObject(
    "userInput" -> JsString(formula.userInput),
    "ascii" -> formula.optFormula.map(toASCII).map(JsString.apply).getOrElse(JsNull),
    "latex" -> formula.optFormula.map(toLaTeX).map(JsString.apply).getOrElse(JsNull),
  )
}
