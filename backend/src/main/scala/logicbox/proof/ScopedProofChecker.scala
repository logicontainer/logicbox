package logicbox.proof

import logicbox.framework.{ProofChecker, Proof, Scope, Root, Error}
import logicbox.framework.Error._

// note: only scope violations, doesn't report steps/reference/boxes inwhich
// the id points to something invalid
class ScopedProofChecker[Id] 
  extends ProofChecker[Any, Any, Any, Id] 
{
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

  override def check(proof: Proof[Any, Any, Any, Id]): List[(Id, Error)] = {
    val scopes = collectScopes(proof, proof.rootSteps)
    val allIdsInProof = scopes.keySet

    def checkRefs(stepId: Id, refs: Seq[Id], seenSteps: Set[Id], openBoxes: Set[Id]): List[(Id, Error)] = {
      val refsInProof = refs.filter(allIdsInProof.contains)
      refsInProof.zipWithIndex.flatMap {
        case (refId, refIdx) =>
          val List(stepScope, refScope) = List(stepId, refId).map(scopes.apply)
          val Right(refStep) = proof.getStep(refId): @unchecked // unchecked because of above filter

          if (!isSubscope(stepScope, refScope, scopes)) {
            Some((stepId, ReferenceOutOfScope(refIdx)))
          } else if (!seenSteps.contains(refId)) refStep match {
            case Proof.Box(_, _) if openBoxes.contains(refId) => 
              Some((stepId, ReferenceToUnclosedBox(refIdx)))
            case _ => 
              Some(stepId, ReferenceToLaterStep(refIdx))
          } else None

      }.toList
    }

    def checkImpl(proof: Proof[Any, Any, Any, Id], steps: Seq[Id], seenSteps: Set[Id] = Set.empty, openedBoxes: Set[Id] = Set.empty): List[(Id, Error)] = {
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
