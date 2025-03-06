package logicbox.proof

import logicbox.framework.{ Proof, ModifiableProof }

object ProofImpl {
  def empty[F, R, B, Id](stepModifier: ProofStepStrategy[F, R, B, Id]): ProofImpl[F, R, B, Id] = 
    ProofImpl(stepModifier, steps = Map(), rootSteps = Seq())
}

trait ProofStepStrategy[F, R, B, Id] {
  def createEmptyLine: Proof.Line[F, R, Id]
  def createEmptyBox: Proof.Box[B, Id]

  def updateFormula(line: Proof.Line[F, R, Id], formula: F): Proof.Line[F, R, Id]
  def updateRule(line: Proof.Line[F, R, Id], rule: R): Proof.Line[F, R, Id]
  def updateRefs(line: Proof.Line[F, R, Id], refs: Seq[Id]): Proof.Line[F, R, Id]

  def updateBoxInfo(box: Proof.Box[B, Id], info: B): Proof.Box[B, Id]
  def updateBoxSteps(box: Proof.Box[B, Id], steps: Seq[Id]): Proof.Box[B, Id]
}

case class ProofImpl[F, R, B, Id](
  stepStrategy: ProofStepStrategy[F, R, B, Id],
  steps: Map[Id, Proof.Step[F, R, B, Id]],
  rootSteps: Seq[Id]
) extends ModifiableProof[F, R, B, Id] {
  private type Err = ModifiableProof.Error[Id]
  private type Pf = ModifiableProof[F, R, B, Id]
  private type Pos = ModifiableProof.Pos[Id]

  import ModifiableProof._

  private def proofButWith(newSteps: Map[Id, Proof.Step[F, R, B, Id]], newRootSteps: Seq[Id]): ProofImpl[F, R, B, Id] =
    ProofImpl(stepStrategy, newSteps, newRootSteps)

  private def getStepImpl(id: Id): Option[Proof.Step[F, R, B, Id]] = steps.get(id)

  def getStep(id: Id): Either[Proof.StepNotFound[Id], Proof.Step[F, R, B, Id]] = 
    getStepImpl(id).toRight(Proof.StepNotFound(id, s"no step with id: $id"))

  private def assertStepNotExists(id: Id): Either[Err, Unit] = for {
    _ <- getStepImpl(id) match {
      case Some(_) => Left(IdAlreadyInUse(id))
      case None => Right(())
    }
  } yield ()

  private case class InsertResult(
    newStepSeq: Seq[Id], 
    modifiedSteps: List[(Id, Proof.Step[F, R, B, Id])] = Nil
  )

  private def insertIdAtIdx(idToInsert: Id, idx: Int, dir: Direction, inList: Seq[Id]): InsertResult = {
    assert(inList.length > idx)
    val (before, elm :: after) = inList.splitAt(idx): @unchecked // unchecked because elm has index `idx`, so always suceeds
    dir match {
      case Direction.Above => InsertResult(
        newStepSeq = before ++ (idToInsert +: elm +: after)
      )
      case Direction.Below => InsertResult(
        newStepSeq = (before :+ elm) ++ (idToInsert :: after)
      )
    }
  }

  private def insertIdAtLine(idToInsert: Id, whereId: Id, dir: Direction, inList: Seq[Id]): Option[InsertResult] = 
    inList.indexOf(whereId) match {
      case idx if idx != -1 => Some(insertIdAtIdx(idToInsert, idx, dir, inList))

      case _ => inList.foldLeft(None) {
        case (Some(res), _) => Some(res) // when found, 'break'
        case (None, stepId) => steps(stepId) match {
          case Proof.Box(info: B @unchecked, steps: Seq[Id] @unchecked) => for {
            InsertResult(stepSeq, stepsToBeAdded) <- insertIdAtLine(idToInsert, whereId, dir, steps)
            b1 = stepStrategy.createEmptyBox
            b2 = stepStrategy.updateBoxInfo(b1, info)
            box = stepStrategy.updateBoxSteps(b2, stepSeq)
          } yield InsertResult(inList, (stepId -> box) :: stepsToBeAdded)
          case _ => None
        }
      }
    }
  
  private def insertId(id: Id, where: Pos): Either[Error[Id], InsertResult] = where match {
    case ProofTop => Right(InsertResult(newStepSeq = id +: rootSteps))

    case AtLine(whereId: Id, dir) => 
      insertIdAtLine(id, whereId, dir, rootSteps)
        .toRight(InvalidPosition(where, "no line with given id"))

    case BoxTop(boxId) => for {
      step <- getStepImpl(boxId).toRight(InvalidPosition(where, "no box with given id"))
      box <- step match {
        case b: Proof.Box[B, Id] => Right(b)
        case _ => Left(InvalidPosition(where, "id is a line, not a box"))
      }
      newBox = stepStrategy.updateBoxSteps(box, id +: box.steps)
      stepsToBeAdded = List(boxId -> newBox)
    } yield InsertResult(newStepSeq = rootSteps, stepsToBeAdded)
  }

  private def insertStep(
    id: Id, step: Proof.Step[F, R, B, Id], where: Pos
  ): Either[Error[Id], Pf] = for {
    InsertResult(newRootSteps, modifiedSteps) <- insertId(id, where)
    newSteps = steps + (id -> step) ++ modifiedSteps
  } yield proofButWith(newSteps, newRootSteps)

  override def addLine(id: Id, where: Pos): Either[Err, Pf] = for {
    _ <- assertStepNotExists(id)
    pf <- insertStep(id, stepStrategy.createEmptyLine, where)
  } yield pf

  override def addBox(id: Id, where: Pos): Either[Err, Pf] = for {
    _ <- assertStepNotExists(id)
    pf <- insertStep(id, stepStrategy.createEmptyBox, where)
  } yield pf

  private def getCurrentLine(lineId: Id): Either[Err, Proof.Line[F, R, Id]] = for {
    step <- getStepImpl(lineId).toRight(CannotUpdateStep(lineId, "no step with given id"))
    line <- step match {
      case line: Proof.Line[F, R, Id] => Right(line)
      case _ => Left(CannotUpdateStep(lineId, "step is not a line"))
    }
  } yield line

  private def updateStep(stepId: Id, newStep: Proof.Step[F, R, B, Id]): Pf =
    proofButWith(newRootSteps = rootSteps, newSteps = steps + (stepId -> newStep))

  override def updateFormula(lineId: Id, formula: F): Either[Err, Pf] = for {
    line <- getCurrentLine(lineId)
    newStep = stepStrategy.updateFormula(line, formula)
  } yield updateStep(lineId, newStep)

  def updateRule(lineId: Id, rule: R): Either[Err, Pf] = for {
    line <- getCurrentLine(lineId)
    newStep = stepStrategy.updateRule(line, rule)
  } yield updateStep(lineId, newStep)

  def updateReferences(lineId: Id, refs: Seq[Id]): Either[Err, Pf] = for {
    line <- getCurrentLine(lineId)
    newStep = stepStrategy.updateRefs(line, refs)
  } yield updateStep(lineId, newStep)

  private case class RemoveResult(
    newStepSeq: Seq[Id], 
    modifiedSteps: List[(Id, Proof.Step[F, R, B, Id])]
  )

  private def removeIdFromProofStructure(idToRemove: Id, inList: Seq[Id]): Option[RemoveResult] =
    inList.indexOf(idToRemove) match {
      case idx if idx != -1 => 
        val (bef, elm :: aft) = inList splitAt idx: @unchecked
        Some(RemoveResult(bef ++ aft, Nil))

      case _ => inList.foldLeft(None) {
        case (Some(res), _) => Some(res)
        case (None, stepId) => for {
          step <- getStepImpl(stepId)
          box <- step match {
            case b: Proof.Box[B, Id] => Some(b)
            case _ => None
          }
          RemoveResult(newStepSeq, modifiedSteps) <- removeIdFromProofStructure(idToRemove, box.steps)
          newBox = stepStrategy.updateBoxSteps(box, newStepSeq)
        } yield RemoveResult(newStepSeq, modifiedSteps :+ (stepId -> newBox))
      }
    }

  private def computeReachableNodes(steps: Seq[Id], newMap: Map[Id, Proof.Step[F, R, B, Id]]): Set[Id] = 
    val ls = for {
      id <- steps
      step <- newMap.get(id)
      reachable = step match {
        case Proof.Box(info, steps: Seq[Id] @unchecked) => computeReachableNodes(steps, newMap) + id
        case _ => Set(id)
      }
    } yield reachable
    ls.flatten.toSet

  override def removeStep(id: Id): Either[Error[Id], Pf] = for {
    RemoveResult(newStepSeq, modifiedSteps) <- 
      removeIdFromProofStructure(id, rootSteps).toRight(CannotRemoveStep(id, "step not found"))
    newMap = (steps - id) ++ modifiedSteps
    toRemove = steps.keySet -- computeReachableNodes(newStepSeq, newMap)
  } yield proofButWith(newRootSteps = newStepSeq, newSteps = newMap -- toRemove)
}
