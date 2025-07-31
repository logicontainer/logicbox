package logicbox.proof

import logicbox.framework.Proof

case class ProofLineImpl[F, R, Id](formula: F, rule: R, refs: Seq[Id]) extends Proof.Line[F, R, Id]
case class ProofBoxImpl[B, Id](info: B, steps: Seq[Id]) extends Proof.Box[B, Id]
