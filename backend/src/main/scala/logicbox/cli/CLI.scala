package logicbox.cli

import scala.io.Source
import scala.util.Try
import logicbox.server.format._
import logicbox.server.PropLogicProofValidatorService
import spray.json.JsObject
import spray.json.JsArray
import logicbox.server.PredLogicProofValidatorService

object CLIMain {
  private def isAProofLine(line: String): Boolean = {
    line.count(_ == ':') == 2
  }

  private def isABoxStart(ident: Int)(line: String): Boolean = {
    line.count(_ == '-') > 4 && line.count(_ == ':') == 0 && line.takeWhile(_ == ' ').size == ident
  }

  private def isABoxStop(ident: Int)(line: String): Boolean = {
    line.trim.size > 2 && line.trim.forall(_ == '-') && line.takeWhile(_ == ' ').size == ident
  }

  private def parseRawProofLine(line: String): RawProofLine = {
    assert(isAProofLine(line))
    val (id, formula, rule, refs) = line.split(':').map(_.trim) match {
      case Array(id, formula, just) =>
        val (rule, refs) = just.split(' ').map(_.trim).toList match {
          case rule :: refsStr => (rule, refsStr.mkString(" ").split(',').map(_.trim).filter(_ != "").toList)
          case _ => ???
        }
        (id, formula, rule, refs)
      case _ => ???
    }
    RawProofLine(
      uuid = id,
      stepType = "line",
      formula = RawFormula(
        userInput = formula,
        ascii = None,
        latex = None
      ),
      justification = RawJustification(
        rule = Some(rule),
        refs = refs
      )
    )
  }

  private def splitOnFirst[T](ls: List[T], pred: T => Boolean): Option[(List[T], List[T])] = {
    val idx = ls.indexWhere(pred)
    if idx == -1 then None else Some(ls.splitAt(idx))
  }

  private def parseRawProof(lines: List[String], ident: Int = 0): RawProof = {
    splitOnFirst(lines, isABoxStart(ident)) match {
      case Some(begin, startLine :: aft) => splitOnFirst(aft, isABoxStop(ident)) match {
        case Some(mid, _ :: endd) => 
          val (id, freshVar) = startLine.takeWhile(_ != '-').trim.split(" ") match {
            case Array(id) => (id, None)
            case Array(id, freshVar) => (id, Some(freshVar))
            case _ => ???
          }
          val inner = parseRawProof(mid, ident + 2)
          begin.map(parseRawProofLine) ++ List(
            RawProofBox(
              uuid = id,
              stepType = "box",
              boxInfo = Some(RawBoxInfo(freshVar)),
              proof = inner
            )
          ) ++ parseRawProof(endd)

        case _ => assert(false)
      }
      case _ => lines.map(parseRawProofLine)
    }
  }

  def main(args: Array[String]): Unit = {
    import logicbox.server.format.SprayFormatters._

    val validator = args.headOption match {
      case Some("prop") => PropLogicProofValidatorService()
      case Some("pred") => PredLogicProofValidatorService()
      case _ => ???
    }

    for {
      inputFile <- args.tail.headOption
      _ = println(s"Running on $inputFile")
      lines <- Try { Source.fromFile(inputFile).getLines() }.toOption
      proof = parseRawProof(lines.toList)
      ds = validator.validateProof(rawProofFormat.write(proof)) match {
        case Left(value) => List(value.toString)
        case Right(value) => value.asInstanceOf[JsObject].getFields("diagnostics").head.asInstanceOf[JsArray].elements.toList.map(_.prettyPrint)
      }
    } yield ds.foreach(println)
  }
}
