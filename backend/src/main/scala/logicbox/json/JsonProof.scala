package logicbox.json

case class JsonFormula(
  ascii: Option[String],
  latex: Option[String],
  userInput: Option[String],
)

case class JsonJustification(
  refs: List[String],
  rule: Option[String]
)

sealed trait JsonProofStep {
  def uuid: String
  def stepType: String
}

final case class JsonProofLine(
  uuid: String,
  stepType: String,
  formula: JsonFormula,
  justification: JsonJustification,
) extends JsonProofStep

final case class JsonProofBox(
  uuid: String,
  stepType: String,
  proof: List[JsonProofStep]
) extends JsonProofStep

type JsonProof = List[JsonProofStep]
