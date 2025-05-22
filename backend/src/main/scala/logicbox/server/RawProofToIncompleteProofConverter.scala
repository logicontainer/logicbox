package logicbox.server

import logicbox.server.format.RawProofConverter
import logicbox.server.format.{RawProof, RawProofStep, RawProofLine, RawProofBox, RawFormula, RawJustification}
import logicbox.framework._
import logicbox.framework.Proof.Line
import logicbox.framework.Proof.Box
import logicbox.proof.ProofLineImpl
import logicbox.proof.ProofBoxImpl
import logicbox.proof.ProofImpl

class RawProofToIncompleteProofConverter[F, R](
  val parseFormula: String => Option[F], 
  val parseRule: String => Option[R],
  val formulaToAscii: F => String,
  val formulaToLatex: F => String,
  val ruleToString: R => String,
) extends RawProofConverter[IncompleteProof[F, R, Unit, String]] {

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

  private def convertBoxToRaw(id: String, box: Proof.Box[Unit, String], pf: IncompleteProof[F, R, Unit, String]): RawProofBox = {
    RawProofBox(
     uuid = id,
      stepType = "box",
      proof = convertStepsToRaw(pf, box.steps)
    )
  }

  private def convertStepsToRaw(pf: IncompleteProof[F, R, Unit, String], steps: Seq[String]): List[RawProofStep] = for {
    id <- steps.toList
    step <- pf.getStep(id).toOption.toList
    rawStep = step match {
      case l: Line[IncompleteFormula[F], Option[R], String] => convertLineToRaw(id, l)
      case b: Box[Unit, String] => convertBoxToRaw(id, b, pf)
    }
  } yield rawStep

  override def convertToRaw(proof: IncompleteProof[F, R, Unit, String]): RawProof = {
    convertStepsToRaw(proof, proof.rootSteps)
  }

  private def convertStepsFromRaw(steps: List[RawProofStep]): Map[String, Proof.Step[IncompleteFormula[F], Option[R], Unit, String]] = {
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
  
        case RawProofBox(uuid, _, innerProof) => {
          val ms = convertStepsFromRaw(innerProof)
          (uuid -> ProofBoxImpl((), innerProof.map(_.uuid).toSeq)) :: ms.toList
        }
      }
    } yield newMapping

    mappings.toMap
  }

  override def convertFromRaw(rawProof: RawProof): IncompleteProof[F, R, Unit, String] = {
    ProofImpl(
      map = convertStepsFromRaw(rawProof),
      rawProof.map(_.uuid)
    )
  }
}
