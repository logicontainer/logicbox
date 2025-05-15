package logicbox.formula

import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.{ Reader, Position, NoPosition }

class PredLogicLexer extends RegexParsers {
  import PredLogicToken._
  override def skipWhitespace = true
  
  def and = ("A" | "∧" | "and" | "AND") ^^^ And()
  def or = ("V" | "∧" | "or" | "OR") ^^^ Or()
  def not = ("!" | "¬" | "not" | "NOT") ^^^ Not()
  def implies = ("->" | "=>" | "implies" | "IMPLIES") ^^^ Implies()
  def contradiction = ("bot" | "BOT" | "FALSE" | "false" | "contradiction" | "CONTRADICTION" | "⊥") ^^^ Contradiction()
  def tautology = ("top" | "TOP" | "TRUE" | "true" | "TAUTOLOGY" | "tautology" | "⊤") ^^^ Tautology()
  def forall = ("forall" | "FORALL" | "∀") ^^^ ForAll()
  def exists = ("exists" | "EXISTS" | "∃") ^^^ Exists()
  def ident = """[a-z|A-Z]""".r ^^ { str => Ident(str.charAt(0)) }
  def equalss = "=" ^^^ Equals()
  
  def comma = "," ^^^ Comma()
  def leftParen = "(" ^^^ LeftParen()
  def rightParen = ")" ^^^ RightParen()

  def token: Parser[PredLogicToken] = and | or | not | implies | contradiction | tautology | leftParen | rightParen | forall | exists | ident | comma | equalss
  def tokens: Parser[List[PredLogicToken]] = rep1(token)

  def apply(input: String): List[PredLogicToken] =
    parse(phrase(tokens), input) match {
      case p @ (NoSuccess(_, _) | Failure(_, _) | Error(_, _))  => throw java.lang.RuntimeException(p.toString)
      case Success(result, _) => result
    }
}

class PredLogicTokenReader(tokens: List[PredLogicToken]) extends Reader[PredLogicToken] {
  override def first: PredLogicToken = tokens.head
  override def atEnd: Boolean = tokens.isEmpty
  override def pos: Position = NoPosition
  override def rest: Reader[PredLogicToken] = PredLogicTokenReader(tokens.tail)
}
