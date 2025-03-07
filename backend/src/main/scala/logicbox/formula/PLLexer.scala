package logicbox.formula

import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.{ Reader, Position, NoPosition }

class PLLexer extends RegexParsers {
  import PLToken._
  override def skipWhitespace = true
  
  def and = ("A" | "∧" | "and" | "AND") ^^^ And()
  def or = ("V" | "∧" | "or" | "OR") ^^^ Or()
  def not = ("!" | "¬" | "not" | "NOT") ^^^ Not()
  def implies = ("->" | "=>" | "implies" | "IMPLIES") ^^^ Implies()
  def contradiction = ("bot" | "BOT" | "FALSE" | "false" | "contradiction" | "CONTRADICTION" | "⊥") ^^^ Contradiction()
  def tautology = ("top" | "TOP" | "TRUE" | "true" | "TAUTOLOGY" | "tautology" | "⊤") ^^^ Tautology()
  def atom = """[a-z]""".r ^^ { case str => Atom(str.charAt(0)) }
  def leftParen = "(" ^^^ LeftParen()
  def rightParen = ")" ^^^ RightParen()

  def token: Parser[PLToken] = and | or | not | implies | contradiction | tautology | atom | leftParen | rightParen
  def tokens: Parser[List[PLToken]] = rep1(token)

  def apply(input: String): List[PLToken] =
    parse(phrase(tokens), input) match {
      case p @ (NoSuccess(_, _) | Failure(_, _) | Error(_, _))  => ???
      case Success(result, _) => result
    }
}

class TokenReader(tokens: List[PLToken]) extends Reader[PLToken] {
  override def first: PLToken = tokens.head
  override def atEnd: Boolean = tokens.isEmpty
  override def pos: Position = NoPosition
  override def rest: Reader[PLToken] = TokenReader(tokens.tail)
}
