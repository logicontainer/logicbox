package logicbox.server

import logicbox.framework._
import logicbox.framework.Proof.Line
import logicbox.framework.Proof.Box
import logicbox.proof.ProofLineImpl
import logicbox.proof.ProofBoxImpl
import logicbox.proof.ProofImpl

import logicbox.server.format._

class RawProofToIncompleteProofConverter[F, R, B](
  val parseFormula: String => Option[F], 
  val parseRule: String => Option[R],
  val parseRawBoxInfo: RawBoxInfo => Option[B],
  val formulaToAscii: F => String,
  val formulaToLatex: F => String,
  val ruleToString: R => String,
  val boxInfoToRaw: B => RawBoxInfo,
) extends RawProofConverter[IncompleteProof[F, R, B, String]] {

  private def convertLineToRaw(id: String, line: Proof.Line[IncompleteFormula[F], Option[R], String]): RawProofLine = {
    RawProofLine(
      uuid = id,
      stepType = "line",
      formula = RawFormula(
        userInput = line.formula.userInput,
        ascii = line.formula.optFormula.map(formulaToAscii),
        latex = line.formula.optFormula.map(formulaToLatex)
      ),
      justification = RawJustification(
        rule = line.rule.map(ruleToString),
        refs = line.refs.toList
      )
    )
  }

  private def convertBoxToRaw(id: String, box: Proof.Box[Option[B], String], pf: IncompleteProof[F, R, B, String]): RawProofBox = {
    RawProofBox(
     uuid = id,
      stepType = "box",
      boxInfo = box.info.map(boxInfoToRaw).getOrElse(RawBoxInfo(None)),
      proof = convertStepsToRaw(pf, box.steps)
    )
  }

  private def convertStepsToRaw(pf: IncompleteProof[F, R, B, String], steps: Seq[String]): List[RawProofStep] = for {
    id <- steps.toList
    step <- pf.getStep(id).toList
    rawStep = step match {
      case l: Line[IncompleteFormula[F], Option[R], String] => convertLineToRaw(id, l)
      case b: Box[Option[B], String] => convertBoxToRaw(id, b, pf)
    }
  } yield rawStep

  override def convertToRaw(proof: IncompleteProof[F, R, B, String]): RawProof = {
    convertStepsToRaw(proof, proof.rootSteps)
  }

  private def convertStepsFromRaw(steps: List[RawProofStep]): Map[String, Proof.Step[IncompleteFormula[F], Option[R], Option[B], String]] = {
    val mappings = for {
      rawStep <- steps
      newMapping <- rawStep match {
        case RawProofLine(uuid, _, formula, justification) => List(
          uuid -> ProofLineImpl(
            formula = IncompleteFormula(
              userInput = formula.userInput,
              optFormula = parseFormula(formula.userInput)
            ),
            rule = justification.rule.flatMap(parseRule),
            refs = justification.refs
          )
        )
  
        case RawProofBox(uuid, _, info, innerProof) => {
          val ms = convertStepsFromRaw(innerProof)
          (uuid -> ProofBoxImpl((parseRawBoxInfo(info)), innerProof.map(_.uuid).toSeq)) :: ms.toList
        }
      }
    } yield newMapping

    mappings.toMap
  }

  override def convertFromRaw(rawProof: RawProof): IncompleteProof[F, R, B, String] = {
    ProofImpl(
      map = convertStepsFromRaw(rawProof),
      rawProof.map(_.uuid)
    )
  }
}
