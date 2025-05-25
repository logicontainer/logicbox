package logicbox.formula

import scala.util.parsing.combinator.PackratParsers
import scala.util.parsing.input.Reader
import scala.util.parsing.input.Position
import scala.util.parsing.input.NoPosition

import ArithLogicFormula._
import ArithLogicTerm._

class ArithLogicParser extends PackratParsers {
  override type Elem = ArithLogicToken


  private def varexp: Parser[ArithLogicTerm.Var] = accept("var", { case ArithLogicToken.Ident(c) => ArithLogicTerm.Var(c) })
  private def termexp: Parser[ArithLogicTerm] = {
    def zeroexp: Parser[ArithLogicTerm.Zero] = accept("zero", { case ArithLogicToken.Zero() => ArithLogicTerm.Zero() })
    def oneexp: Parser[ArithLogicTerm.One] = accept("one", { case ArithLogicToken.One() => ArithLogicTerm.One() })
    def atomictermexp: Parser[ArithLogicTerm] = varexp | zeroexp | oneexp | withParens(termexp)

    def multexp: Parser[ArithLogicTerm] = 
      ((atomictermexp ~ rep(ArithLogicToken.Mult() ~ atomictermexp))) ^^ {
        case t ~ ts => 
          ts.foldLeft(t) {
            case (term, _ ~ t1) => ArithLogicTerm.Mult(term, t1)
          }
      }

    def plusexp: Parser[ArithLogicTerm] = 
      ((multexp ~ rep(ArithLogicToken.Plus() ~ multexp))) ^^ {
        case t ~ ts => 
          ts.foldLeft(t) {
            case (term, _ ~ t1) => ArithLogicTerm.Plus(term, t1)
          }
      }

    plusexp
  }
  
  private def contrexp: Parser[Contradiction] = elem(ArithLogicToken.Contradiction()) ^^^ Contradiction()
  private def tautexp: Parser[Tautology] = elem(ArithLogicToken.Tautology()) ^^^ Tautology()
  private def equalityexp: Parser[Equals] = (termexp ~ ArithLogicToken.Equals() ~ termexp) ^^ {
    case t1 ~ _ ~ t2 => ArithLogicFormula.Equals(t1, t2)
  }

  private def atomicexps: Parser[ArithLogicFormula] = tautexp | contrexp | equalityexp

  private def c: Parser[ArithLogicFormula] = 
    atomicexps |
    ((ArithLogicToken.Not() ~ c) ^^ { case _ ~ phi => Not(phi) }) |
    ((ArithLogicToken.Exists() ~ varexp ~ c) ^^ { 
      case _ ~ x ~ phi => Exists(x, phi)
    }) | 
    ((ArithLogicToken.ForAll() ~ varexp ~ c) ^^ { 
      case _ ~ x ~ phi => ForAll(x, phi)
    }) | 
    withParens(formula)

  private def b: Parser[ArithLogicFormula] = {
    ((c ~ rep((ArithLogicToken.And() | ArithLogicToken.Or()) ~ c)) ^^ { case phi ~ ls => 
      ls.foldLeft(phi) {
        case (form, ArithLogicToken.And() ~ psi) => And(form, psi)
        case (form, _ ~ psi) => Or(form, psi)
      }
    })
  }

  private def a: Parser[ArithLogicFormula] =
    ((rep(b ~ ArithLogicToken.Implies()) ~ b) ^^ {  case ls ~ phi =>
      ls.foldRight(phi) {
        case (psi ~ _, form) => Implies(psi, form)
      }
    })

  private def withParens[T](parser: Parser[T]) = ArithLogicToken.LeftParen() ~> parser <~ ArithLogicToken.RightParen()
  
  private def formula: Parser[ArithLogicFormula] = a

  def parseVariable(input: List[ArithLogicToken]): ArithLogicTerm.Var = {
    phrase(varexp)(ArithLogicTokenReader(input)) match {
      case p @ (NoSuccess(_, _) | Failure(_, _) | Error(_, _))  => 
        throw new RuntimeException(p.toString)
      case Success(result, _) => result
    }
  }

  def parseFormula(input: List[ArithLogicToken]): ArithLogicFormula = {
    phrase(formula)(ArithLogicTokenReader(input)) match {
      case p @ (NoSuccess(_, _) | Failure(_, _) | Error(_, _))  => 
        throw new RuntimeException(p.toString)
      case Success(result, _) => result
    }
  }
}
