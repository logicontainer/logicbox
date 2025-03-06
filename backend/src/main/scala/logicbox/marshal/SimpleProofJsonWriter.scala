package logicbox.marshal

import logicbox.framework.Proof
import spray.json._
import logicbox.framework.Proof.Line
import logicbox.framework.Proof.Box

case class SimpleProofJsonWriter[F, R, Id](
  idWriter: JsonWriter[Id], formulaWriter: JsonWriter[F],
  justficationWriter: JsonWriter[Justification[R, Id]]
) extends RootJsonWriter[Proof[F, R, ?, Id]] {

  private def writeLine(id: Id, line: Proof.Line[F, R, Id]): JsValue = {
    JsObject(
      "stepType" -> JsString("line"),
      "uuid" -> idWriter.write(id),
      "formula" -> formulaWriter.write(line.formula),
      "justification" -> justficationWriter.write(Justification(line.rule, line.refs))
    )
  }

  private def writeBox(id: Id, box: Proof.Box[?, Id], proof: Proof[F, R, ?, Id]): JsValue = {
    JsObject(
      "stepType" -> JsString("box"),
      "uuid" -> idWriter.write(id),
      "proof" -> JsArray(
        box.steps.map(id => writeStep(id, proof)).toList
      )
    )
  }

  private def writeStep(id: Id, proof: Proof[F, R, ?, Id]): JsValue = {
    val step = proof.getStep(id)
    assert(step.isRight, s"attempt to marshal incomplete proof. on id: $id")
    val Right(s) = step: @unchecked
    s match {
      case l: Line[F, R, Id] => writeLine(id, l)
      case b: Box[?, Id] => writeBox(id, b, proof)
    }
  }

  override def write(proof: Proof[F, R, ?, Id]): JsValue = 
    JsArray(proof.rootSteps.map(id => writeStep(id, proof)).toList)
}
