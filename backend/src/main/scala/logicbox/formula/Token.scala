package logicbox.formula

sealed abstract class Token
object Token {
  case class LeftParen() extends Token
  case class RightParen() extends Token

  case class And() extends Token
  case class Or() extends Token
  case class Implies() extends Token
  case class Not() extends Token

  case class Contradiction() extends Token
  case class Tautology() extends Token

  case class Exists() extends Token
  case class ForAll() extends Token

  case class Equals() extends Token

  case class Ident(x: String) extends Token

  case class Plus() extends Token
  case class Mult() extends Token
  case class Zero() extends Token
  case class One() extends Token
}
