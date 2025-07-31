package logicbox.rule

sealed trait ArithLogicRule

object ArithLogicRule {
  case class Peano1() extends ArithLogicRule
  case class Peano2() extends ArithLogicRule
  case class Peano3() extends ArithLogicRule
  case class Peano4() extends ArithLogicRule
  case class Peano5() extends ArithLogicRule
  case class Peano6() extends ArithLogicRule

  case class Induction() extends ArithLogicRule
}
