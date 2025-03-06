package logicbox.proof

import logicbox.framework.{ Proof }
import logicbox.framework.Proof._

class StandardStepStrategy[F, R, B, Id](
  val emptyLine: Proof.Line[F, R, Id],
  val emptyBox: Proof.Box[B, Id]
) extends ProofStepStrategy[F, R, B, Id] {

  override def createEmptyLine: Line[F, R, Id] = emptyLine
  override def createEmptyBox: Box[B, Id] = emptyBox

  override def updateRefs(line: Line[F, R, Id], refs: Seq[Id]): Line[F, R, Id] = 
    ProofLineImpl(line.formula, line.rule, refs)

  override def updateFormula(line: Line[F, R, Id], formula: F): Line[F, R, Id] =
    ProofLineImpl(formula, line.rule, line.refs)

  override def updateRule(line: Line[F, R, Id], rule: R): Line[F, R, Id] =
    ProofLineImpl(line.formula, rule, line.refs)

  override def updateBoxInfo(box: Box[B, Id], info: B): Box[B, Id] = 
    ProofBoxImpl(info, box.steps)

  override def updateBoxSteps(box: Box[B, Id], steps: Seq[Id]): Box[B, Id] =
    ProofBoxImpl(box.info, steps)
}
