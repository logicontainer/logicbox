package logicbox.server

import logicbox.framework.{Proof, Error}
import logicbox.server.format.OutputError

trait ErrorConverter[F, R, B] {
  def convert(proof: Proof[F, R, B, String], stepId: String, error: Error): Option[OutputError]
}
