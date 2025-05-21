package logicbox.server

import logicbox.framework.ProofValidatorService
import logicbox.framework.ProofChecker

import spray.json._
import logicbox.framework.Proof
import logicbox.framework.Diagnostic
import scala.util.Try
import spray.json.JsonParser.ParsingException

import logicbox.server.format.RawProofConverter
import logicbox.server.format.SprayFormatters

private type SprayErr = DeserializationException | SerializationException | ParsingException

class ProofValidatorServiceImpl[F, R, B](
  val rawProofConverter: RawProofConverter[Proof[F, R, B, String]],
  val proofChecker: ProofChecker[F, R, B, String],
) extends ProofValidatorService[SprayErr] {

  import logicbox.server.format.SprayFormatters._

  private def safeSpray[T](f: => T): Either[SprayErr, T] = try {
    Right(f)
  } catch {
    case e: SprayErr => Left(e)
  }

  override def validateProof(proofJson: JsValue): Either[SprayErr, JsValue] = for {
    rawProof <- safeSpray { rawProofFormat.read(proofJson) }
    proof = rawProofConverter.convertFromRaw(rawProof)
    diagnostics = proofChecker.check(proof).map(writeDiagnostic(_))
    outputRawProof <- safeSpray { rawProofFormat.write(rawProofConverter.convertToRaw(proof)) }
  } yield JsObject(
    "proof" -> outputRawProof,
    "diagnostics" -> JsArray(diagnostics)
  )
}
