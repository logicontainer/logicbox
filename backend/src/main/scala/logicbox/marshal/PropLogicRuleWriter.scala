package logicbox.marshal

import spray.json._
import logicbox.proof.PropLogicRule
import logicbox.proof.PropLogicRule._

class PropLogicRuleWriter extends JsonWriter[PropLogicRule] {
  private def toStr(rule: PropLogicRule): String = rule match {
    case Assumption() => "assumption"
    case Premise() => "premise"
    case AndElim(side) => s"and_elim_${if (side == PropLogicRule.Side.Left) then 1 else 2}"
    case AndIntro() => "and_intro"
    case OrIntro(side) => s"or_intro_${if (side == PropLogicRule.Side.Left) then 1 else 2}"
    case OrElim() => "or_elim"
    case ImplicationIntro() => "implies_intro"
    case ImplicationElim() => "implies_elim"
    case NotIntro() => "not_intro"
    case NotElim() => "not_elim"
    case ContradictionElim() => "bot_elim"
    case NotNotElim() => "not_not_elim"
    case ModusTollens() => "modus_tollens"
    case NotNotIntro() => "not_not_intro"
    case ProofByContradiction() => "proof_by_contradiction"
    case LawOfExcludedMiddle() => "law_of_excluded_middle"
    case Copy() => "copy"
  }

  override def write(rule: PropLogicRule): JsValue = JsString(toStr(rule))
}
