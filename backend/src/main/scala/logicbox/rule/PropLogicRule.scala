package logicbox.rule

import logicbox.framework.{ Reference }
import logicbox.formula.PropLogicFormula

type PLBoxInfo = Unit

sealed trait PropLogicRule

object PropLogicRule {
  enum Side { case Left; case Right }
  case class Premise() extends PropLogicRule
  case class Assumption() extends PropLogicRule 
  case class Copy() extends PropLogicRule

  case class AndElim(side: Side) extends PropLogicRule
  case class AndIntro() extends PropLogicRule

  case class OrIntro(side: Side) extends PropLogicRule
  case class OrElim() extends PropLogicRule

  case class ImplicationIntro() extends PropLogicRule
  case class ImplicationElim() extends PropLogicRule

  case class NotIntro() extends PropLogicRule
  case class NotElim() extends PropLogicRule

  case class ContradictionElim() extends PropLogicRule

  case class NotNotElim() extends PropLogicRule
  case class NotNotIntro() extends PropLogicRule

  case class ModusTollens() extends PropLogicRule

  case class ProofByContradiction() extends PropLogicRule
  case class LawOfExcludedMiddle() extends PropLogicRule
}

