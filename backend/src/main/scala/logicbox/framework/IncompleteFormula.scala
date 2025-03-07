package logicbox.framework

case class IncompleteFormula[F](
  userInput: String, optFormula: Option[F]
)
