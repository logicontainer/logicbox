package logicbox.demarshal

import logicbox.framework.ModifyProofCommand
import spray.json._

case class ProofJsonReader[F, R, B, Id]()
  extends RootJsonReader[List[ModifyProofCommand[F, R, Id]]] 
{
  override def read(json: JsValue): List[ModifyProofCommand[F, R, Id]] = ???
}
