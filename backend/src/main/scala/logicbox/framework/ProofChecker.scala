package logicbox.framework

trait ProofChecker[-F, -R, -B, Id] {
  def check(proof: Proof[F, R, B, Id]): List[(Id, Error)]
}
