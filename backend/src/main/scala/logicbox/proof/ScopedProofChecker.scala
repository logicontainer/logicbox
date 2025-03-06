package logicbox.proof

import logicbox.framework.ProofChecker
import logicbox.framework.Proof
import logicbox.proof.ScopedProofChecker.Diagnostic

object ScopedProofChecker {
  sealed trait Diagnostic[+Id] {
    def stepId: Id
  }

  case object Root
  type Scope[+Id] = Root.type | Id

  case class ReferenceToLaterStep[+Id](stepId: Id, refIdx: Int, refId: Id) extends Diagnostic[Id]
  case class ScopeViolation[+Id](stepId: Id, stepScope: Scope[Id], refIdx: Int, refId: Id, refScope: Scope[Id]) extends Diagnostic[Id]
  case class ReferenceToUnclosedBox[+Id](stepId: Id, refIdx: Int, boxId: Id) extends Diagnostic[Id]
}

// note: only scope violations, doesn't report steps/reference/boxes inwhich
// the id points to something invalid
class ScopedProofChecker[Id] 
  extends ProofChecker[Any, Any, Any, Id, ScopedProofChecker.Diagnostic[Id]] 
{
  import ScopedProofChecker._

  // for every reachable id from `steps`, add entry id -> its scope in result
  private def collectScopes(proof: Proof[Any, Any, Any, Id], steps: Seq[Id], currentScope: Scope[Id] = Root): Map[Id, Scope[Id]] =
    steps.map {
      case stepId => 
        val optBoxSteps = proof.getStep(stepId) match {
          case Right(Proof.Box(_, boxSteps: Seq[Id] @unchecked)) =>
            collectScopes(proof, boxSteps, stepId)
          case _ => Map.empty
        }
        optBoxSteps + (stepId -> currentScope)
    }.flatten.toMap

  private def isSubscope(scope: Scope[Id], parent: Scope[Id], scopes: Map[Id, Scope[Id]]): Boolean = scope match {
    case Root => parent == Root
    case id: Id @unchecked => 
      scope == parent || isSubscope(scopes(id), parent, scopes)
  }

  override def check(proof: Proof[Any, Any, Any, Id]): List[Diagnostic[Id]] = {
    val scopes = collectScopes(proof, proof.rootSteps)
    val allIdsInProof = scopes.keySet

    def checkRefs(stepId: Id, refs: Seq[Id], seenSteps: Set[Id], openBoxes: Set[Id]): List[Diagnostic[Id]] = {
      val refsInProof = refs.filter(allIdsInProof.contains)
      refsInProof.zipWithIndex.flatMap {
        case (refId, refIdx) =>
          val List(stepScope, refScope) = List(stepId, refId).map(scopes.apply)
          val Right(refStep) = proof.getStep(refId): @unchecked // unchecked because of above filter

          if (!isSubscope(stepScope, refScope, scopes)) {
            Some(ScopeViolation(stepId, stepScope, refIdx, refId, refScope))
          } else if (!seenSteps.contains(refId)) refStep match {
            case Proof.Box(_, _) if openBoxes.contains(refId) => 
              Some(ReferenceToUnclosedBox(stepId, refIdx, refId))
            case _ => 
              Some(ReferenceToLaterStep(stepId, refIdx, refId))
          } else None

      }.toList
    }

    def checkImpl(proof: Proof[Any, Any, Any, Id], steps: Seq[Id], seenSteps: Set[Id] = Set.empty, openedBoxes: Set[Id] = Set.empty): List[Diagnostic[Id]] = {
      steps match {
        case Nil => Nil
        case stepId +: rest => (proof.getStep(stepId) match {
          case Right(Proof.Line(_, _, refs: Seq[Id] @unchecked)) => 
            checkRefs(stepId, refs, seenSteps, openedBoxes)

          case Right(Proof.Box(_, boxSteps: Seq[Id] @unchecked)) => 
            checkImpl(proof, boxSteps, seenSteps, openedBoxes + stepId)

          case _ => Nil
        }) ++ checkImpl(proof, rest, seenSteps + stepId, openedBoxes)
      }
    }

    checkImpl(proof, proof.rootSteps)
  }
}
