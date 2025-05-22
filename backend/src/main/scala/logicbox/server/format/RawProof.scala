package logicbox.server.format

// the fields `ascii` and `latex` are ignored on requests
case class RawFormula(
  userInput: String,
  ascii: Option[String],
  latex: Option[String],
)

case class RawJustification(
  refs: List[String],
  rule: Option[String]
)

case class RawBoxInfo(
  freshVar: Option[String]
)

sealed trait RawProofStep {
  def uuid: String
  def stepType: String
}

final case class RawProofLine(
  uuid: String,
  stepType: String,
  formula: RawFormula,
  justification: RawJustification,
) extends RawProofStep

final case class RawProofBox(
  uuid: String,
  stepType: String,
  boxInfo: RawBoxInfo,
  proof: List[RawProofStep]
) extends RawProofStep

type RawProof = List[RawProofStep]
