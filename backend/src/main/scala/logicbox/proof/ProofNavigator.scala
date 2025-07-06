package logicbox.proof

import logicbox.framework.Navigator
import logicbox.framework.Proof
import logicbox.framework.Location

class ProofNavigator[F, B, Id, O](
  formulaNavigator: Navigator[F, O],
  boxInfoNavigator: Navigator[B, O],
) extends Navigator[Proof[F, ?, B, Id], O] {
  override def get(proof: Proof[F, ?, B, Id], loc: Location): Option[O] = {
    ???
  }
}
