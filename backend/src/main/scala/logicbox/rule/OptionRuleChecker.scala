package logicbox.proof

import logicbox.framework.{RuleChecker, Reference}
import logicbox.framework.Reference.Line
import logicbox.framework.Reference.Box

object OptionRuleChecker {
  sealed trait Violation[+V]
  case object MissingFormula extends Violation[Nothing]
  case object MissingRule extends Violation[Nothing]
  case class MissingDetailInReference(refIdx: Int, expl: String) extends Violation[Nothing]
  case class RuleViolation[+V](violation: V) extends Violation[V]
}

case class OptionRuleChecker[F, R, B, V](
  ruleChecker: RuleChecker[F, R, B, V]
) extends RuleChecker[Option[F], Option[R], Option[B], OptionRuleChecker.Violation[V]] {
  import OptionRuleChecker._

  private def computeConcreteReferences(
    optRefs: List[Reference[Option[F], Option[B]]]
  ): Either[List[Violation[V]], List[Reference[F, B]]] = {
    val init: (List[Reference[F, B]], List[MissingDetailInReference]) = (Nil, Nil)
    val (refs, missingIdxs) = optRefs.zipWithIndex.foldRight(init) {
      case ((optRef, refIdx), (refs, missingIdxs)) => (optRef: @unchecked) match {
        case Line(Some(formula: F @unchecked)) => 
          (ReferenceLineImpl(formula) :: refs, missingIdxs)

        case Box(Some(info: B @unchecked), Some(ass: F @unchecked), Some(concl: F @unchecked)) => 
          (ReferenceBoxImpl(info, ass, concl) :: refs, missingIdxs)
        
        case Line(None) => 
          (refs, MissingDetailInReference(refIdx, "missing formula") :: missingIdxs)

        case Box(
          info: Option[B] @unchecked, 
          ass: Option[F] @unchecked, 
          concl: Option[F] @unchecked
        ) => 
          val ms = { if (ass.isEmpty) List(
              MissingDetailInReference(refIdx, "missing assumption")
            ) else Nil
          } ++ { if (concl.isEmpty) List(
              MissingDetailInReference(refIdx, "missing conclusion")
            ) else Nil
          } ++ { if (info.isEmpty) List(
              MissingDetailInReference(refIdx, "missing box info")
            ) else Nil
          }
          (refs, ms ++ missingIdxs)
      }
    }

    if (missingIdxs.nonEmpty)
      Left(missingIdxs)
    else
      Right(refs)
  }
  
  private def computeArgumentViolations(
    rule: Option[R], formula: Option[F]
  ): List[Violation[V]] = {
    { 
      if (formula.isEmpty) List(
        MissingFormula
      ) else Nil
    } ++ {
      if (rule.isEmpty) List(
        MissingRule
      ) else Nil
    }
  }

  override def check(
    rule: Option[R], formula: Option[F], 
    optRefs: List[Reference[Option[F], Option[B]]]
  ): List[Violation[V]] = {
    val refsOrMissingRefs = computeConcreteReferences(optRefs)
    val argViolations = computeArgumentViolations(rule, formula)

    argViolations ++ { (rule, formula, refsOrMissingRefs) match {
      case (Some(rule), Some(formula), Right(refs)) => 
        // delegate
        ruleChecker.check(rule, formula, refs).map { RuleViolation(_) }
      
      case (_, _, Left(missingRefs)) => missingRefs
      case _ => Nil
    }}
  }
}
