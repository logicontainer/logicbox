package logicbox.server.format

import logicbox.server.format.RawProof
import logicbox.framework.Proof

trait RawProofConverter[Pf] {
  def convertToRaw(proof: Pf): RawProof
  def convertFromRaw(rawProof: RawProof): Pf
}
