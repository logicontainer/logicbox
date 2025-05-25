package logicbox.server.format

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors
import logicbox.formula.ArithLogicTerm._
import logicbox.formula.ArithLogicFormula._

class PrettyArithLogicFormulaTest extends AnyFunSpec {
  describe("Stringifiers::predLogicFormulaAsLaTeX") {
    import Stringifiers.arithLogicFormulaAsLaTeX => asLaTeX
    it("should do bot/top/predicates") {
      asLaTeX(Contradiction()) shouldBe "\\bot"
      asLaTeX(Tautology()) shouldBe "\\top"
    }

    it("should add brackets on binary operations") {
      asLaTeX(And(And(Tautology(), Tautology()), Tautology())) shouldBe "(\\top \\land \\top) \\land \\top"
      asLaTeX(Implies(Tautology(), Or(Tautology(), Tautology()))) shouldBe "\\top \\rightarrow (\\top \\lor \\top)"
      asLaTeX(Or(Tautology(), Implies(Tautology(), Tautology()))) shouldBe "\\top \\lor (\\top \\rightarrow \\top)"
    }

    it("should output 0, 1, addition, mult correctly") {
      asLaTeX(Equals(Plus(Zero(), Zero()), One())) shouldBe "0 + 0 = 1"
      asLaTeX(Equals(Plus(Plus(Zero(), One()), One()), One())) shouldBe "(0 + 1) + 1 = 1"
      asLaTeX(Equals(Mult(Mult(Zero(), Zero()), Mult(One(), One())), One())) shouldBe "(0 * 0) * (1 * 1) = 1"
    }
    
    it("should not add brackets on not") {
      asLaTeX(Not(Not(And(Contradiction(), Not(Not(Equals(Var("x"), Var("x")))))))) shouldBe 
        "\\lnot \\lnot (\\bot \\land \\lnot \\lnot (x = x))"
    }

    it("should add brackets on equality") {
      asLaTeX(Not(Equals(Var("x"), Var("y")))) shouldBe 
        "\\lnot (x = y)"
    }

    it("should add brackets inside forall/exists, but not around") {
      asLaTeX(ForAll(Var("x"), Equals(Var("x"), Var("y")))) shouldBe
        "\\forall x (x = y)"
      asLaTeX(Exists(Var("x"), Equals(Var("x"), Var("y")))) shouldBe
        "\\exists x (x = y)"
    }
  }

  describe("Stringifiers.propLogicFormulaAsASCII") {
    import Stringifiers.arithLogicFormulaAsASCII => asASCII
    it("should do bot/top/predicates") {
      asASCII(Contradiction()) shouldBe "false"
      asASCII(Tautology()) shouldBe "true"
    }

    it("should add brackets on binary operations") {
      asASCII(And(And(Tautology(), Tautology()), Tautology())) shouldBe "(true and true) and true"
      asASCII(Implies(Tautology(), Or(Tautology(), Tautology()))) shouldBe "true -> (true or true)"
      asASCII(Or(Tautology(), Implies(Tautology(), Tautology()))) shouldBe "true or (true -> true)"
    }

    it("should work with terms") {
      asASCII(Equals(Plus(Zero(), Zero()), One())) shouldBe "0 + 0 = 1"
      asASCII(Equals(Plus(Plus(Zero(), One()), One()), One())) shouldBe "(0 + 1) + 1 = 1"
      asASCII(Equals(Mult(Mult(Zero(), Zero()), Mult(One(), One())), One())) shouldBe "(0 * 0) * (1 * 1) = 1"
    }
    
    it("should not add brackets on not") {
      asASCII(Not(Not(And(Contradiction(), Not(Not(Equals(Var("x"), Var("x")))))))) shouldBe 
        "not not (false and not not (x = x))"
    }

    it("should add brackets on equality") {
      asASCII(Not(Equals(Var("x"), Var("y")))) shouldBe 
        "not (x = y)"
    }

    it("should add brackets inside forall/exists, but not around") {
      asASCII(ForAll(Var("x"), Equals(Var("x"), Var("y")))) shouldBe
        "forall x (x = y)"
      asASCII(Exists(Var("x"), Equals(Var("x"), Var("y")))) shouldBe
        "exists x (x = y)"
    }
  }
}
