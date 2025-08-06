package logicbox.formula

import scala.util.parsing.combinator.PackratParsers
import scala.util.parsing.input.Reader
import scala.util.parsing.input.Position
import scala.util.parsing.input.NoPosition

object Parser extends PackratParsers {
  import FormulaKind._, Formula._, Term._

  type Elem = Token

  private def withParens[T](p: Parser[T]): Parser[T] = Token.LeftParen() ~> p <~ Token.RightParen()


  def varexp[K <: (Pred | Arith)]: Parser[Term.Var[K]] = accept("variable", { case Token.Ident(c) => Var(c) })

  def oneexp: Parser[One] = Token.One() ^^^ One()
  def zeroexp: Parser[Zero] = Token.Zero() ^^^ Zero()

  def plusexp(termParser: Parser[Term[Arith]]): Parser[Plus] = {
    (termParser ~ rep1(Token.Plus() ~ termParser)) ^^ {
      case first ~ ((_ ~ second) :: rest) => rest.foldLeft(Plus(first, second)) {
        case (lhs, (_ ~ rhs)) => Plus(lhs, rhs)
      }
      case first ~ Nil => assert(false) // not possible (rep1)
    }
  }

  def multexp(termParser: Parser[Term[Arith]]): Parser[Mult] = {
    (termParser ~ rep1(Token.Mult() ~ termParser)) ^^ {
      case first ~ ((_ ~ second) :: rest) => rest.foldLeft(Mult(first, second)) {
        case (lhs, (_ ~ rhs)) => Mult(lhs, rhs)
      }
      case first ~ Nil => assert(false)
    }
  }

  def funcsymb: Parser[String] = accept("predicate symbol", { case Token.Ident(p) => p })
  def funcexp(termListParser: Parser[List[Term[Pred]]]): Parser[FunAppl] = (funcsymb ~ withParens(termListParser)) ^^ {
    case f ~ ts => FunAppl(f, ts)
  }

  def termlistexp[K <: (Pred | Arith)](termParser: Parser[Term[Pred]]) = {
    (rep(termParser ~ Token.Comma()) ~ termParser) ^^ {
      case ls ~ t => ls.foldRight(List(t)) {
        case ((term ~ _), ts) => term :: ts
      }
    }
  }

  def arithTerm: Parser[Term[Arith]] = {
    def arithTerm1 = oneexp | zeroexp | varexp | withParens(arithTerm)
    def arithTerm2 = multexp(arithTerm1) | arithTerm1
    def arithTerm3 = plusexp(arithTerm2) | arithTerm2

    arithTerm3
  }

  def predTerm: Parser[Term[Pred]] = funcexp(termlistexp(predTerm)) | varexp

  def contrexp[K <: FormulaKind]: Parser[Contradiction[K]] = Token.Contradiction() ^^^ Contradiction()
  def tautexp[K <: FormulaKind]: Parser[Tautology[K]] = Token.Tautology() ^^^ Tautology()

  def equals: Parser[Unit] = Token.Equals() ^^^ ()
  def equalityexp[K <: (Pred | Arith)](termParser: Parser[Term[K]]): Parser[Equals[K]] = (termParser ~ equals ~ termParser) ^^ {
    case t1 ~ _ ~ t2 => Equals(t1, t2)
  }

  def atomexp: Parser[Atom] = accept("propositional atom", { case Token.Ident(c) if c.length == 1 => Atom(c.charAt(0)) })

  def andOrExp[K <: FormulaKind](inner: Parser[Formula[K]]): Parser[And[K] | Or[K]] = {
    def combine(tok: Token.And | Token.Or, phi: Formula[K], psi: Formula[K]): And[K] | Or[K] = tok match {
      case Token.And() => And(phi, psi)
      case Token.Or() => Or(phi, psi)
    }

    def andOrSymb: Parser[Token.And | Token.Or] = (Token.And() | Token.Or()) ^^ {
      case Token.And() => Token.And()
      case Token.Or() => Token.Or()
      case _ => assert(false)
    }
    
    (inner ~ rep1(andOrSymb ~ inner)) ^^ {
      case phi ~ ((tok ~ psi) :: rest) => rest.foldLeft(combine(tok, phi, psi)) {
        case (phi, tok ~ psi) => combine(tok, phi, psi)
      }
      case phi ~ Nil => assert(false) // not possible (rep1)
    }
  }
  
  def connectiveExp[K <: FormulaKind](inner: Parser[Formula[K]]): Parser[Formula[K]] = {
    def connectiveExp1: Parser[Formula[K]] = 
        inner 
      | (Token.Not() ~ connectiveExp1) ^^ { case _ ~ phi => Not(phi) }
      | withParens(connectiveExp(inner))

    def connectiveExp2: Parser[Formula[K]] = 
      andOrExp(connectiveExp1) | connectiveExp1

    def connectiveExp3: Parser[Formula[K]] = {
      (rep1(Token.Implies() ~ connectiveExp2) ~ connectiveExp2) ^^ {
        case (rest :+ (_ ~ phi)) ~ psi => rest.foldRight(Implies(phi, psi)) {
          case (_ ~ phi, psi) => Implies(phi, psi)
        }
        case _ ~ psi => assert(false)
      }
    }

    connectiveExp3
  } 

  def quantexp[K <: (Pred | Arith)](varParser: Parser[Term.Var[K]], inner: Parser[Formula[K]]): Parser[Exists[K] | ForAll[K]] = {
    (Token.ForAll() ~ varParser ~ inner) ^^ {
      case _ ~ x ~ phi => ForAll(x, phi)
    } | 
    (Token.Exists() ~ varParser ~ inner) ^^ {
      case _ ~ x ~ phi => Exists(x, phi)
    }
  }

  def propLogicFormula: Parser[Formula[Prop]] = connectiveExp(atomexp)
  def predLogicFormula: Parser[Formula[Pred]] = connectiveExp(equalityexp(predTerm) | quantexp(varexp, predLogicFormula))
}
