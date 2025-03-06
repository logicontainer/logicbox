package logicbox.marshal

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

class PrettyPLFormulaTest extends AnyFunSpec {
  import logicbox.formula.PLFormula._
  describe("PrettyPLFormula::asLaTeX") {
    import PrettyPLFormula.asLaTeX
    it("should do bot/top/atoms") {
      asLaTeX(Contradiction()) shouldBe "\\bot"
      asLaTeX(Tautology()) shouldBe "\\top"
      asLaTeX(Atom('A')) shouldBe "A"
      asLaTeX(Atom('z')) shouldBe "z"
    }

    it("should add brackets on binary operations") {
      asLaTeX(And(And(Atom('p'), Atom('q')), Atom('r'))) shouldBe "(p \\land q) \\land r"
      asLaTeX(Implies(Atom('r'), Or(Atom('p'), Atom('q')))) shouldBe "r \\rightarrow (p \\lor q)"
      asLaTeX(Or(Atom('r'), Implies(Atom('p'), Atom('q')))) shouldBe "r \\lor (p \\rightarrow q)"
    }
    
    it("should not add brackets on not") {
      asLaTeX(Not(Not(And(Atom('p'), Not(Not(Atom('q'))))))) shouldBe 
        "\\lnot \\lnot (p \\land \\lnot \\lnot q)"
    }
  }

  describe("PrettyPLFormula::asASCII") {
    import PrettyPLFormula.asASCII
    it("should do bot/top/atoms") {
      asASCII(Contradiction()) shouldBe "false"
      asASCII(Tautology()) shouldBe "true"
      asASCII(Atom('A')) shouldBe "A"
      asASCII(Atom('z')) shouldBe "z"
    }

    it("should add brackets on binary operations") {
      asASCII(And(And(Atom('p'), Atom('q')), Atom('r'))) shouldBe "(p and q) and r"
      asASCII(Implies(Atom('r'), Or(Atom('p'), Atom('q')))) shouldBe "r -> (p or q)"
      asASCII(Or(Atom('r'), Implies(Atom('p'), Atom('q')))) shouldBe "r or (p -> q)"
    }
    
    it("should not add brackets on not") {
      asASCII(Not(Not(And(Atom('p'), Not(Not(Atom('q'))))))) shouldBe 
        "not not (p and not not q)"
    }
  }
}
