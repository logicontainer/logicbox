package logicbox

import io.circe.parser._, io.circe._, io.circe.syntax._
import logicbox.server.JsonVerifier
import scala.scalajs.js.annotation.{JSExportTopLevel, JSExport}

@JSExportTopLevel("JSLogicboxVerifier")
object JSLogicboxVerifier {
  @JSExport("verify")
  def verify(input: String): Json = {
    parse(input) match {
      case Right(json) => JsonVerifier.verify(json)
      case Left(failure) => Json.obj("message" -> failure.toString.asJson)
    }
  }
}
