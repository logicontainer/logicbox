package logicbox.server

import logicbox.framework.{Proof, Error}
import logicbox.server.format.OutputError

trait ErrorConverter {
  def convert(stepId: String, error: Error): Option[OutputError]
}
