package logicbox.proof

import logicbox.framework.{ Reference }
import logicbox.formula.PLFormula

type PLBoxInfo = Unit

sealed trait PLRule

object PLRule {
  import PLFormula.*
  import PLViolation._

  enum Side { case Left; case Right }
  case class Premise() extends PLRule
  case class Assumption() extends PLRule 
  case class Copy() extends PLRule

  case class AndElim(side: Side) extends PLRule
  case class AndIntro() extends PLRule

  case class OrIntro(side: Side) extends PLRule
  case class OrElim() extends PLRule

  case class ImplicationIntro() extends PLRule
  case class ImplicationElim() extends PLRule

  case class NotIntro() extends PLRule
  case class NotElim() extends PLRule

  case class ContradictionElim() extends PLRule

  case class NotNotElim() extends PLRule
  case class NotNotIntro() extends PLRule

  case class ModusTollens() extends PLRule

  case class ProofByContradiction() extends PLRule
  case class LawOfExcludedMiddle() extends PLRule
}

