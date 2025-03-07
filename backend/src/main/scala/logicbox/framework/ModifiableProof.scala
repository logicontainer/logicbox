package logicbox.framework

object ModifiableProof {
  sealed trait Error[+Id]
  case class InvalidPosition[Id](pos: Pos[Id], expl: String) extends Error[Id]
  case class CannotUpdateStep[Id](stepId: Id, expl: String) extends Error[Id]
  case class CannotRemoveStep[Id](stepId: Id, expl: String) extends Error[Id]
  case class IdAlreadyInUse[Id](id: Id) extends Error[Id]

  enum Direction { case Above; case Below }

  sealed trait Pos[+Id]
  case object ProofTop extends Pos[Nothing]
  case class BoxTop[+Id](boxId: Id) extends Pos[Id]
  case class AtLine[+Id](lineId: Id, dir: Direction) extends Pos[Id]
}

trait ModifiableProof[F, R, B, Id] extends Proof[F, R, B, Id] {
  import ModifiableProof._

  private type Pf = ModifiableProof[F, R, B, Id]
  private type E = Error[Id]

  def addLine(id: Id, where: Pos[Id]): Either[E, Pf]
  def addBox(id: Id, where: Pos[Id]): Either[E, Pf]

  def updateFormula(lineId: Id, formula: F): Either[E, Pf]
  def updateRule(lineId: Id, rule: R): Either[E, Pf]
  def updateReferences(lineId: Id, refs: Seq[Id]): Either[E, Pf]

  def removeStep(id: Id): Either[E, Pf]
}
