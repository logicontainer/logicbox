package logicbox.proof

import logicbox.framework.Proof
import logicbox.framework.Proof._

case class ProofImpl[F, R, B, Id](
  private val map: Map[Id, Step[F, R, B, Id]],
  val rootSteps: Seq[Id]
) extends Proof[F, R, B, Id] {
  def getStep(id: Id): Option[Step[F, R, B, Id]] = map.get(id)
}
