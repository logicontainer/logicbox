package logicbox.formula

sealed abstract class PLToken
object PLToken {
  case class LeftParen() extends PLToken
  case class RightParen() extends PLToken
  case class And() extends PLToken
  case class Or() extends PLToken
  case class Implies() extends PLToken
  case class Not() extends PLToken
  case class Contradiction() extends PLToken
  case class Tautology() extends PLToken
  case class Atom(c: Char) extends PLToken
}
