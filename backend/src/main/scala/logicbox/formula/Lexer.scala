package logicbox.formula

import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.{ Reader, Position, NoPosition }

object Lexer extends RegexParsers {
  import Token._
  override def skipWhitespace = true
  
  def and = ("^" | "∧" | "and" | "AND" | "/\\") ^^^ And()
  def or = ("V" | "or" | "OR" | "\\/") ^^^ Or()
  def not = ("!" | "¬" | "not" | "NOT") ^^^ Not()
  def implies = ("->" | "=>" | "implies" | "IMPLIES") ^^^ Implies()
  def plus = "+" ^^^ Plus()
  def mult = "*" ^^^ Mult()
  def zero = "0" ^^^ Zero()
  def one = "1" ^^^ One()
  def contradiction = ("bot" | "BOT" | "FALSE" | "false" | "contradiction" | "CONTRADICTION" | "⊥") ^^^ Contradiction()
  def tautology = ("top" | "TOP" | "TRUE" | "true" | "TAUTOLOGY" | "tautology" | "⊤") ^^^ Tautology()
  def forall = ("forall" | "FORALL" | "∀") ^^^ ForAll()
  def exists = ("exists" | "EXISTS" | "∃") ^^^ Exists()
  def ident = """[A-Za-z]+((_\d)|(_\{\d+\}))?""".r ^^ { str => Ident(str) }
  def equalss = "=" ^^^ Equals()
  def comma = "," ^^^ Comma()
  
  def leftParen = "(" ^^^ LeftParen()
  def rightParen = ")" ^^^ RightParen()

  def token: Parser[Token] = and | or | not | implies | plus | mult | zero | one | contradiction | tautology | leftParen | rightParen | forall | exists | ident | equalss | comma
  def tokens: Parser[List[Token]] = rep1(token)

  def apply(input: String): List[Token] =
    parse(phrase(tokens), input) match {
      case p @ (NoSuccess(_, _) | Failure(_, _) | Error(_, _))  => throw java.lang.RuntimeException(p.toString)
      case Success(result, _) => result
    }
}

class TokenReader(tokens: List[Token]) extends Reader[Token] {
  override def first: Token = tokens.head
  override def atEnd: Boolean = tokens.isEmpty
  override def pos: Position = NoPosition
  override def rest: Reader[Token] = TokenReader(tokens.tail)
}
