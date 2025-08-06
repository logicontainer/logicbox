package logicbox.formula

import scala.util.parsing.combinator.PackratParsers
import scala.util.parsing.input.Reader
import scala.util.parsing.input.Position
import scala.util.parsing.input.NoPosition
import Formula._, Term._

class ArithLogicParser extends PackratParsers {
  override type Elem = ArithLogicToken
  type Arith = FormulaKind.Arith
  type AVar = Term.Var[Arith]

  private def varexp: Parser[AVar] = accept("var", { case ArithLogicToken.Ident(c) => Var(c) })
  private def termexp: Parser[ArithLogicTerm] = {
    def zeroexp: Parser[Term.Zero] = accept("zero", { case ArithLogicToken.Zero() => Zero() })
    def oneexp: Parser[Term.One] = accept("one", { case ArithLogicToken.One() => One() })
    def atomictermexp: Parser[ArithLogicTerm] = varexp | zeroexp | oneexp | withParens(termexp)

    def multexp: Parser[ArithLogicTerm] = 
      ((atomictermexp ~ rep(ArithLogicToken.Mult() ~ atomictermexp))) ^^ {
        case t ~ ts => 
          ts.foldLeft(t) {
            case (term, _ ~ t1) => Mult(term, t1)
          }
      }

    def plusexp: Parser[ArithLogicTerm] = 
      ((multexp ~ rep(ArithLogicToken.Plus() ~ multexp))) ^^ {
        case t ~ ts => 
          ts.foldLeft(t) {
            case (term, _ ~ t1) => Plus(term, t1)
          }
      }

    plusexp
  }
  
  private def contrexp: Parser[Contradiction[Arith]] = elem(ArithLogicToken.Contradiction()) ^^^ Contradiction()
  private def tautexp: Parser[Tautology[Arith]] = elem(ArithLogicToken.Tautology()) ^^^ Tautology()
  private def equalityexp: Parser[Equals[Arith]] = (termexp ~ ArithLogicToken.Equals() ~ termexp) ^^ {
    case t1 ~ _ ~ t2 => Equals(t1, t2)
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

  def parseVariable(input: List[ArithLogicToken]): AVar = {
    phrase(varexp)(ArithLogicTokenReader(input)) match {
      case p @ (NoSuccess(_, _) | Failure(_, _) | Error(_, _))  => 
        throw new RuntimeException(p.toString)
      case Success(result, _) => result
    }
  }

  def parseTerm(input: List[ArithLogicToken]): ArithLogicTerm = {
    phrase(termexp)(ArithLogicTokenReader(input)) match {
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
