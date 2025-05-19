package logicbox.proof

object PropLogicRuleParser {
  import logicbox.proof.PropLogicRule
  import logicbox.proof.PropLogicRule._

  def parse(rule: String): Option[PropLogicRule] = rule match {
    case "assumption" => Some(Assumption())
    case "premise" => Some(Premise())
    case "and_elim_1" => Some(AndElim(Side.Left))
    case "and_elim_2" => Some(AndElim(Side.Right))
    case "and_intro" => Some(AndIntro())
    case "or_intro_1" => Some(OrIntro(Side.Left))
    case "or_intro_2" => Some(OrIntro(Side.Right))
    case "or_elim" => Some(OrElim())
    case "implies_intro" => Some(ImplicationIntro())
    case "implies_elim" => Some(ImplicationElim())
    case "not_intro" => Some(NotIntro())
    case "not_elim" => Some(NotElim())
    case "bot_elim" => Some(ContradictionElim())
    case "not_not_elim" => Some(NotNotElim())
    case "modus_tollens" => Some(ModusTollens())
    case "not_not_intro" => Some(NotNotIntro())
    case "proof_by_contradiction" => Some(ProofByContradiction())
    case "law_of_excluded_middle" => Some(LawOfExcludedMiddle())
    case "copy" => Some(Copy())
    case _ => None
  }
}
