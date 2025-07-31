package logicbox.formula

sealed abstract class PredLogicToken
object PredLogicToken {
  case class LeftParen() extends PredLogicToken
  case class RightParen() extends PredLogicToken
  case class Comma() extends PredLogicToken

  case class And() extends PredLogicToken
  case class Or() extends PredLogicToken
  case class Implies() extends PredLogicToken
  case class Not() extends PredLogicToken

  case class Contradiction() extends PredLogicToken
  case class Tautology() extends PredLogicToken

  case class Exists() extends PredLogicToken
  case class ForAll() extends PredLogicToken

  case class Equals() extends PredLogicToken

  case class Ident(x: String) extends PredLogicToken
}
