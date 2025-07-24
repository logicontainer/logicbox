package logicbox.rule

import logicbox.framework.{RuleChecker, Reference, Error, RulePosition}
import logicbox.framework.Reference.Line
import logicbox.framework.Reference.Box
import logicbox.rule.{ReferenceLineImpl, ReferenceBoxImpl}
import logicbox.framework.Location

case class OptionRuleChecker[F, R, B, V](
  ruleChecker: RuleChecker[F, R, B]
) extends RuleChecker[Option[F], Option[R], Option[B]] {

  private def computeConcreteReference(optRef: Reference[Option[F], Option[B]], refIdx: Int): Either[Error, Reference[F, B]] = {
    optRef match {
      case Line(Some(formula)) => 
        Right(ReferenceLineImpl(formula))

      case Box(Some(info), ass, concl) => 
        val List(optAssRef, optConclRef) = List(ass, concl).map {
          case None => None
          case Some(ref) => computeConcreteReference(ref, -1).toOption
        }

        Right(ReferenceBoxImpl(info, optAssRef, optConclRef))

      case Line(None) =>
        Left(Error.Miscellaneous(Location.premise(refIdx), "missing formula"))

      case Box(None, _, _) | _ => 
        Left(Error.Miscellaneous(Location.premise(refIdx), "missing box info"))
    }
  }

  private def computeConcreteReferences(
    optRefs: List[Reference[Option[F], Option[B]]]
  ): Either[List[Error], List[Reference[F, B]]] = {
    val init: (List[Reference[F, B]], List[Error]) = (Nil, Nil)
    val (refs, vs) = optRefs.zipWithIndex.foldRight(init) {
      case ((optRef, refIdx), (refs, missingIdxs)) => 
        computeConcreteReference(optRef, refIdx) match {
          case Right(ref) => (ref :: refs, missingIdxs)
          case Left(viol) => (refs, viol :: missingIdxs)
        }
    }

    if (vs.nonEmpty)
      Left(vs)
    else
      Right(refs)
  }
  
  private def computeArgumentViolations(
    rule: Option[R], formula: Option[F]
  ): List[Error] = {
    { 
      if (formula.isEmpty) List(
        Error.MissingFormula()
      ) else Nil
    } ++ {
      if (rule.isEmpty) List(
        Error.MissingRule()
      ) else Nil
    }
  }

  override def check(
    rule: Option[R], formula: Option[F], 
    optRefs: List[Reference[Option[F], Option[B]]]
  ): List[Error] = {
    val refsOrMissingRefs = computeConcreteReferences(optRefs)
    val argViolations = computeArgumentViolations(rule, formula)

    argViolations ++ { (rule, formula, refsOrMissingRefs) match {
      case (Some(rule), Some(formula), Right(refs)) => 
        // delegate
        ruleChecker.check(rule, formula, refs)
      
      case (_, _, Left(missingRefs)) => missingRefs
      case _ => Nil
    }}
  }
}
