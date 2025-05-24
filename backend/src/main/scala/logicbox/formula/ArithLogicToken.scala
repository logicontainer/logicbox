package logicbox.formula

sealed abstract class ArithLogicToken
object ArithLogicToken {
  case class LeftParen() extends ArithLogicToken
  case class RightParen() extends ArithLogicToken
  case class Comma() extends ArithLogicToken

  case class And() extends ArithLogicToken
  case class Or() extends ArithLogicToken
  case class Implies() extends ArithLogicToken
  case class Not() extends ArithLogicToken

  case class Contradiction() extends ArithLogicToken
  case class Tautology() extends ArithLogicToken

  case class Exists() extends ArithLogicToken
  case class ForAll() extends ArithLogicToken

  case class Equals() extends ArithLogicToken

  case class Ident(x: String) extends ArithLogicToken

  case class Plus() extends ArithLogicToken
  case class Mult() extends ArithLogicToken
  case class Zero() extends ArithLogicToken
  case class One() extends ArithLogicToken
}
