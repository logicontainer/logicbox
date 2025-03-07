package logicbox.formula

import scala.util.parsing.combinator.PackratParsers

class PLParser extends PackratParsers {
  import PLFormula._

  override type Elem = PLToken

  private def atomexp: Parser[Atom] = accept("atom", { case PLToken.Atom(c) => Atom(c) })
  private def contrexp: Parser[Contradiction] = elem(PLToken.Contradiction()) ^^^ Contradiction()
  private def tautexp: Parser[Tautology] = elem(PLToken.Tautology()) ^^^ Tautology()

  private def simpleexps: Parser[PLFormula] = atomexp | tautexp | contrexp

  private def c: Parser[PLFormula] = 
    simpleexps |
    ((PLToken.Not() ~ c) ^^ { case _ ~ phi => Not(phi) }) |
    withParens(formula)

  private def b: Parser[PLFormula] = {
    ((c ~ rep((PLToken.And() | PLToken.Or()) ~ c)) ^^ { case phi ~ ls => 
      ls.foldLeft(phi: PLFormula) {
        case (form, PLToken.And() ~ psi) => And(form, psi)
        case (form, PLToken.Or() ~ psi) => Or(form, psi)
        case _ => ???
      }
    })
  }

  private def a: Parser[PLFormula] =
    ((rep(b ~ PLToken.Implies()) ~ b) ^^ {  case ls ~ phi =>
      ls.foldRight(phi) {
        case (psi ~ _, form) => Implies(psi, form)
      }
    })

  private def withParens[T](parser: Parser[T]) = PLToken.LeftParen() ~> parser <~ PLToken.RightParen()
  
  private def formula: Parser[PLFormula] = a

  def apply(input: List[PLToken]): PLFormula =
    phrase(formula)(TokenReader(input)) match {
      case p @ (NoSuccess(_, _) | Failure(_, _) | Error(_, _))  => 
        throw new RuntimeException(p.toString)
      case Success(result, _) => result
    }
}
