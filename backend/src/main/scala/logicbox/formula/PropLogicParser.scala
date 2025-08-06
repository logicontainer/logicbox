package logicbox.formula

import scala.util.parsing.combinator.PackratParsers

import Formula._, Term._

class PropLogicParser extends PackratParsers {
  override type Elem = PropLogicToken
  private type Prop = FormulaKind.Prop

  private def atomexp: Parser[Atom] = accept("atom", { case PropLogicToken.Atom(c) => Atom(c) })
  private def contrexp: Parser[Contradiction[Prop]] = elem(PropLogicToken.Contradiction()) ^^^ Contradiction()
  private def tautexp: Parser[Tautology[Prop]] = elem(PropLogicToken.Tautology()) ^^^ Tautology()

  private def simpleexps: Parser[PropLogicFormula] = atomexp | tautexp | contrexp

  private def c: Parser[PropLogicFormula] = 
    simpleexps |
    ((PropLogicToken.Not() ~ c) ^^ { case _ ~ phi => Not(phi) }) |
    withParens(formula)

  private def b: Parser[PropLogicFormula] = {
    ((c ~ rep((PropLogicToken.And() | PropLogicToken.Or()) ~ c)) ^^ { case phi ~ ls => 
      ls.foldLeft(phi: PropLogicFormula) {
        case (form, PropLogicToken.And() ~ psi) => And(form, psi)
        case (form, PropLogicToken.Or() ~ psi) => Or(form, psi)
        case _ => ???
      }
    })
  }

  private def a: Parser[PropLogicFormula] =
    ((rep(b ~ PropLogicToken.Implies()) ~ b) ^^ {  case ls ~ phi =>
      ls.foldRight(phi) {
        case (psi ~ _, form) => Implies(psi, form)
      }
    })

  private def withParens[T](parser: Parser[T]) = PropLogicToken.LeftParen() ~> parser <~ PropLogicToken.RightParen()
  
  private def formula: Parser[PropLogicFormula] = a

  def apply(input: List[PropLogicToken]): PropLogicFormula =
    phrase(formula)(PLTokenReader(input)) match {
      case p @ (NoSuccess(_, _) | Failure(_, _) | Error(_, _))  => 
        throw new RuntimeException(p.toString)
      case Success(result, _) => result
    }
}
