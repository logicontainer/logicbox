package logicbox

import io.circe.parser._, io.circe._, io.circe.syntax._
import scala.scalajs.js.annotation.{JSExportTopLevel, JSExport}

import logicbox.server.JsonVerifier

@JSExportTopLevel("JSLogicboxVerifier")
object JSLogicboxVerifier {
  @JSExport("verify")
  def verify(input: String): String = {
    parse(input) match {
      case Right(json) => JsonVerifier.verify(json).noSpaces
      case Left(failure) => Json.obj("message" -> failure.toString.asJson).noSpaces
    }
  }
}
