package logicbox.server

import logicbox.framework.ProofValidatorService
import logicbox.framework.ProofChecker

import spray.json._
import logicbox.framework.Proof
import logicbox.framework.Diagnostic
import scala.util.Try
import spray.json.JsonParser.ParsingException

private type SprayErr = DeserializationException | SerializationException | ParsingException

class ProofValidatorServiceImpl[F, R, B, Id](
  val proofFormat: JsonFormat[Proof[F, R, B, Id]],
  val proofChecker: ProofChecker[F, R, B, Id],
  val diagnosticWriter: JsonWriter[Diagnostic[Id]]
) extends ProofValidatorService[SprayErr] {
  private def safeSpray[T](f: => T): Either[SprayErr, T] = try {
    Right(f)
  } catch {
    case e: SprayErr => Left(e)
  }

  override def validateProof(proofJson: JsValue): Either[SprayErr, JsValue] = for {
    proof <- safeSpray { proofFormat.read(proofJson) }
    diagnostics = proofChecker.check(proof).map(diagnosticWriter.write)
    cleanedProof <- safeSpray { proofFormat.write(proof) }
  } yield JsObject(
    "proof" -> cleanedProof,
    "diagnostics" -> JsArray(diagnostics)
  )
}
