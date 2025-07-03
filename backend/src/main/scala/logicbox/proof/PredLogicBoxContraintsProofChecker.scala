package logicbox.proof

import logicbox.framework.{Proof, ProofChecker, Error, RulePosition}
import logicbox.rule.{PredLogicRule}
import logicbox.rule.PredLogicRule._
import logicbox.proof.ProofCheckUtil.checkForEveryLine
import logicbox.proof.ProofCheckUtil.checkRefHasAssumptionOnFirstLine
import logicbox.proof.ProofCheckUtil.checkFirstLineOfBoxRef

class PredLogicBoxConstraintsProofChecker[R >: PredLogicRule, Id](
  assumptionRule: R
) extends ProofChecker[Any, R, Any, Id]
{
  type Pf = Proof[Any, R, Any, Id]

  override def check(proof: Pf): List[(Id, Error)] = checkForEveryLine(proof, {
    (id, line) => line.rule match {
      case ExistsElim() => 
        checkRefHasAssumptionOnFirstLine(proof, id, line, 1, assumptionRule)

      case ForAllIntro() => 
        checkFirstLineOfBoxRef(proof, id, line, 0, {
          case (_, Proof.Line(_, rule, _)) if rule == assumptionRule => List(
            (id, Error.Miscellaneous(RulePosition.Ref(0), "first line of box must not be assumption"))
          )
          case _ => Nil
        })

      case _ => Nil
    }
  })
}
