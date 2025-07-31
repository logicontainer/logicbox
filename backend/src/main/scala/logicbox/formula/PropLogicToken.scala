package logicbox.formula

sealed abstract class PropLogicToken
object PropLogicToken {
  case class LeftParen() extends PropLogicToken
  case class RightParen() extends PropLogicToken
  case class And() extends PropLogicToken
  case class Or() extends PropLogicToken
  case class Implies() extends PropLogicToken
  case class Not() extends PropLogicToken
  case class Contradiction() extends PropLogicToken
  case class Tautology() extends PropLogicToken
  case class Atom(c: Char) extends PropLogicToken
}
