package logicbox.marshal

import spray.json._
import logicbox.proof.PLRule
import logicbox.proof.PLRule._

class PLRuleWriter extends JsonWriter[PLRule] {
  private def toStr(rule: PLRule): String = rule match {
    case Assumption() => "assumption"
    case Premise() => "premise"
    case AndElim(side) => s"and_elim_${if (side == PLRule.Side.Left) then 0 else 1}"
    case AndIntro() => "and_intro"
    case OrIntro(side) => s"or_intro_${if (side == PLRule.Side.Left) then 0 else 1}"
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

  override def write(rule: PLRule): JsValue = JsString(toStr(rule))
}
