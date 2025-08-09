package logicbox.formula

import scala.util.parsing.combinator.PackratParsers
import scala.util.parsing.input.Reader
import scala.util.parsing.input.Position
import scala.util.parsing.input.NoPosition

object Parser extends PackratParsers {
  import FormulaKind._, Formula._, Term._

  type Elem = Token

  private def withParens[T](p: Parser[T]): Parser[T] = Token.LeftParen() ~> p <~ Token.RightParen()

  def variable[K <: (Pred | Arith)]: Parser[Term.Var[K]] = accept("variable", { case Token.Ident(c) => Var(c) })

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

  def termlistexp[K <: (Arith | Pred)](termexp: Parser[Term[K]]): Parser[List[Term[K]]] = {
    (rep(termexp ~ Token.Comma()) ~ termexp) ^^ {
      case ls ~ t => ls.foldRight(List(t)) {
        case ((term ~ _), ts) => term :: ts
      }
    }
  }

  def funcsymb: Parser[String] = 
    accept("function symbol", { case Token.Ident(f) => f })

  def funcexp(termListParser: => Parser[List[Term[Pred]]]): Parser[FunAppl] = {
    (funcsymb ~ withParens(termListParser)) ^^ {
      case f ~ ts => FunAppl(f, ts)
    }
  }

  def arithLogicTerm: Parser[Term[Arith]] = {
    def arithTerm1 = oneexp | zeroexp | variable | withParens(arithLogicTerm)
    def arithTerm2 = multexp(arithTerm1) | arithTerm1
    def arithTerm3 = plusexp(arithTerm2) | arithTerm2

    arithTerm3
  }

  def contrexp[K <: FormulaKind]: Parser[Contradiction[K]] = Token.Contradiction() ^^^ Contradiction()
  def tautexp[K <: FormulaKind]: Parser[Tautology[K]] = Token.Tautology() ^^^ Tautology()

  def equals: Parser[Unit] = Token.Equals() ^^^ ()
  def equalityexp[K <: (Pred | Arith)](termParser: Parser[Term[K]]): Parser[Equals[K]] = (termParser ~ equals ~ termParser) ^^ {
    case t1 ~ _ ~ t2 => Equals(t1, t2)
  }
  
  def predsymb: Parser[String] = accept("predicate symbol", { case Token.Ident(p) => p })
  def predexp(termListParser: => Parser[List[Term[Pred]]]): Parser[Predicate] = {
    (predsymb ~ withParens(termListParser) ^^ {
      case p ~ ts => Predicate(p, ts)
    }) |
    predsymb ^^ { case p => Predicate(p, Nil) } 
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

  def impliesExp[K <: FormulaKind](inner: Parser[Formula[K]]): Parser[Implies[K]] = {
    (rep1(inner ~ Token.Implies()) ~ inner) ^^ {
      case (rest :+ (phi ~ tok)) ~ psi => rest.foldRight(Implies(phi, psi)) {
        case (phi ~ _, psi) => Implies(phi, psi)
      }
      case _ ~ psi => assert(false)
    }
  }

  def propLogicFormula: Parser[Formula[Prop]] = {
    def a = atomexp | tautexp[Prop] | contrexp[Prop]
    def b: Parser[Formula[Prop]] = 
      a |
      ((Token.Not() ~ b) ^^ { case _ ~ phi => Not(phi) }) |
      withParens(propLogicFormula)

    def c: Parser[Formula[Prop]] = andOrExp(b) | b
    def d: Parser[Formula[Prop]] = impliesExp(c) | c

    d
  }

  def predLogicTerm: Parser[Term[Pred]] =
    funcexp(termlistexp(predLogicTerm)) | variable

  def predLogicFormula: Parser[Formula[Pred]] = {
    def a: Parser[Formula[Pred]] =
      equalityexp(predLogicTerm) | 
        predexp(termlistexp(predLogicTerm)) | 
        contrexp[Pred] | tautexp[Pred]

    def b: Parser[Formula[Pred]] =
      a
      | ((Token.Not() ~ b) ^^ { case _ ~ phi => Not(phi) })
      | ((Token.ForAll() ~ variable[Pred] ~ b) ^^ { case _ ~ x ~ phi => ForAll(x, phi) })
      | ((Token.Exists() ~ variable[Pred] ~ b) ^^ { case _ ~ x ~ phi => Exists(x, phi) })
      | withParens(predLogicFormula)

    def c: Parser[Formula[Pred]] =
      andOrExp(b) | b

    def d: Parser[Formula[Pred]] =
      impliesExp(c) | c

    d
  }

  def arithLogicFormula: Parser[Formula[Arith]] = {
    def a: Parser[Formula[Arith]] =
      equalityexp(arithLogicTerm) | 
        contrexp[Arith] | tautexp[Arith]

    def b: Parser[Formula[Arith]] =
      a
      | ((Token.Not() ~ b) ^^ { case _ ~ phi => Not(phi) })
      | ((Token.ForAll() ~ variable[Arith] ~ b) ^^ { case _ ~ x ~ phi => ForAll(x, phi) })
      | ((Token.Exists() ~ variable[Arith] ~ b) ^^ { case _ ~ x ~ phi => Exists(x, phi) })
      | withParens(arithLogicFormula)

    def c: Parser[Formula[Arith]] =
      andOrExp(b) | b

    def d: Parser[Formula[Arith]] =
      impliesExp(c) | c

    d
  }

  def parse[T](input: List[Token], parser: Parser[T]): T = {
    phrase(parser)(TokenReader(input)) match {
      case p @ (NoSuccess(_, _) | Failure(_, _) | Error(_, _))  => 
        throw new RuntimeException(p.toString)
      case Success(result, _) => result
    }
  }
}
