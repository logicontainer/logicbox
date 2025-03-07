package logicbox.proof

import logicbox.framework.Proof

case class ProofView[F1, F2, R1, R2, B1, B2, Id](
  val inner: Proof[F1, R1, B1, Id],
  val transformation: (Id, Proof.Step[F1, R1, B1, Id]) => Proof.Step[F2, R2, B2, Id]
) extends Proof[F2, R2, B2, Id]
{
  def rootSteps = inner.rootSteps
  def getStep(id: Id): Either[Proof.StepNotFound[Id], Proof.Step[F2, R2, B2, Id]] = for {
    step <- inner.getStep(id)
  } yield transformation(id, step)
}
