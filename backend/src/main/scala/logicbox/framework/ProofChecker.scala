package logicbox.framework

trait ProofChecker[F, R, B, Id, D] {
  def check(proof: Proof[F, R, B, Id]): List[D]
}
