package logicbox.formula

import scala.util.parsing.combinator.PackratParsers
import scala.util.parsing.input.Reader
import scala.util.parsing.input.Position
import scala.util.parsing.input.NoPosition

import PredLogicFormula._
import PredLogicTerm._

class PredLogicParser extends PackratParsers {
  override type Elem = PredLogicToken

  private def varexp: Parser[PredLogicTerm.Var] = accept("var", { case PredLogicToken.Ident(c) => PredLogicTerm.Var(c) })
  private def funcsymb: Parser[String] = accept("function symbol", { case PredLogicToken.Ident(c) => c })

  private def funcexp: Parser[PredLogicTerm] = (funcsymb ~ withParens(termlistexp)) ^^ {
    case p ~ ts => FunAppl(p, ts)
  }

  private def termexp: Parser[PredLogicTerm] = funcexp | varexp

  def termlistexp: Parser[List[PredLogicTerm]] = 
    (rep(termexp ~ PredLogicToken.Comma()) ~ termexp) ^^ {
      case ls ~ t => ls.foldRight(List(t)) {
        case ((term ~ _), ts) => term :: ts
      }
    }

  private def contrexp: Parser[Contradiction] = elem(PredLogicToken.Contradiction()) ^^^ Contradiction()
  private def tautexp: Parser[Tautology] = elem(PredLogicToken.Tautology()) ^^^ Tautology()
  private def predsymb: Parser[String] = accept("predicate symbol", { case PredLogicToken.Ident(c) => c })

  private def predexp: Parser[Predicate] = 
    ((predsymb ~ withParens(termlistexp)) ^^ {
      case p ~ ts => Predicate(p, ts)
    }) | predsymb ^^ {
      case p => Predicate(p, Nil)
    }
  private def equalityexp: Parser[Equals] = (termexp ~ PredLogicToken.Equals() ~ termexp) ^^ {
    case t1 ~ _ ~ t2 => PredLogicFormula.Equals(t1, t2)
  }

  private def atomicexps: Parser[PredLogicFormula] = tautexp | contrexp | equalityexp | predexp

  private def c: Parser[PredLogicFormula] = 
    atomicexps |
    ((PredLogicToken.Not() ~ c) ^^ { case _ ~ phi => Not(phi) }) |
    ((PredLogicToken.Exists() ~ varexp ~ c) ^^ { 
      case _ ~ x ~ phi => Exists(x, phi)
    }) | 
    ((PredLogicToken.ForAll() ~ varexp ~ c) ^^ { 
      case _ ~ x ~ phi => ForAll(x, phi)
    }) | 
    withParens(formula)

  private def b: Parser[PredLogicFormula] = {
    ((c ~ rep((PredLogicToken.And() | PredLogicToken.Or()) ~ c)) ^^ { case phi ~ ls => 
      ls.foldLeft(phi: PredLogicFormula) {
        case (form, PredLogicToken.And() ~ psi) => And(form, psi)
        case (form, PredLogicToken.Or() ~ psi) => Or(form, psi)
        case _ => ???
      }
    })
  }

  private def a: Parser[PredLogicFormula] =
    ((rep(b ~ PredLogicToken.Implies()) ~ b) ^^ {  case ls ~ phi =>
      ls.foldRight(phi) {
        case (psi ~ _, form) => Implies(psi, form)
      }
    })

  private def withParens[T](parser: Parser[T]) = PredLogicToken.LeftParen() ~> parser <~ PredLogicToken.RightParen()
  
  private def formula: Parser[PredLogicFormula] = a

  def parseVariable(input: List[PredLogicToken]): PredLogicTerm.Var = {
    phrase(varexp)(PredLogicTokenReader(input)) match {
      case p @ (NoSuccess(_, _) | Failure(_, _) | Error(_, _))  => 
        throw new RuntimeException(p.toString)
      case Success(result, _) => result
    }
  }

  def parseTerm(input: List[PredLogicToken]): PredLogicTerm = {
    phrase(termexp)(PredLogicTokenReader(input)) match {
      case p @ (NoSuccess(_, _) | Failure(_, _) | Error(_, _))  => 
        throw new RuntimeException(p.toString)
      case Success(result, _) => result
    }
  }

  def parseFormula(input: List[PredLogicToken]): PredLogicFormula = {
    phrase(formula)(PredLogicTokenReader(input)) match {
      case p @ (NoSuccess(_, _) | Failure(_, _) | Error(_, _))  => 
        throw new RuntimeException(p.toString)
      case Success(result, _) => result
    }
  }
}
