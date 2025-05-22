package logicbox.framework

case class IncompleteFormula[F](
  userInput: String, optFormula: Option[F]
)

type IncompleteProof[F, R, B, Id] = Proof[IncompleteFormula[F], Option[R], B, Id]
