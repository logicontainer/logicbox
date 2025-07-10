package logicbox.proof

import logicbox.framework.{Proof, Error, RulePosition}
import logicbox.framework.Location

object ProofCheckUtil {
  def getFirstLine[F, R, Id](proof: Proof[F, R, ?, Id], box: Proof.Box[?, Id]): Option[Proof.Line[F, R, Id]] = box match {
    case Proof.Box(_, lines) => for {
      firstStepId <- lines.headOption
      firstStep <- proof.getStep(firstStepId).toOption
      line <- firstStep match {
        case l: Proof.Line[F, R, Id] => Some(l)
        case _ => None
      }
    } yield line
  }

  def checkFirstLineOfBoxRef[F, R, Id](
    proof: Proof[F, R, ?, Id], 
    stepId: Id, 
    line: Proof.Line[F, R, Id], 
    refIdx: Int,
    check: (Id, Proof.Line[F, R, Id]) => List[(Id, Error)]
  ): List[(Id, Error)] = {
    val firstLine = line.refs.drop(refIdx).headOption.flatMap {
      case id => proof.getStep(id).toOption.map((id, _))
    }.collect { 
      case (id, box: Proof.Box[Any, Id]) => getFirstLine(proof, box).map((id, _))
    }.flatten

    firstLine.map((id, line) => check(id, line)).getOrElse(Nil)
  }

  def checkRefHasAssumptionOnFirstLine[F, R, Id](
    proof: Proof[F, R, ?, Id], 
    stepId: Id, 
    line: Proof.Line[F, R, Id], 
    refIdx: Int,
    assumptionRule: R
  ): List[(Id, Error)] = {
    checkFirstLineOfBoxRef(proof, stepId, line, refIdx, {
      case (id, Proof.Line(_, rule, _)) if rule != assumptionRule => List(
        (stepId, Error.Miscellaneous(Location.premise(refIdx).firstLine, "first line in box must be assumption"))
      )

      case _ => Nil
    })
  }



  def checkForEveryLine[F, R, B, Id](
    pf: Proof[F, R, B, Id], 
    f: (Id, Proof.Line[F, R, Id]) => List[(Id, Error)]
  ): List[(Id, Error)] = {
    type Pf = Proof[F, R, B, Id]

    def checkSteps(steps: Seq[Id]): List[(Id, Error)] = for {
      stepId <- steps.toList
      diag <- pf.getStep(stepId) match {
        case Right(l: Proof.Line[F, R, Id]) => f(stepId, l)
        case Right(b: Proof.Box[B, Id]) => checkSteps(b.steps)
        case _ => Nil
      }
    } yield diag

    checkSteps(pf.rootSteps)
  }
}
