package logicbox.server.format

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors
import logicbox.formula.PredLogicTerm._
import logicbox.formula.PredLogicFormula._

class PrettyPropLogicFormulaTest extends AnyFunSpec {
  describe("Stringifiers::predLogicFormulaAsLaTeX") {
    import Stringifiers.predLogicFormulaAsLaTeX => asLaTeX
    it("should do bot/top/predicates") {
      asLaTeX(Contradiction()) shouldBe "\\bot"
      asLaTeX(Tautology()) shouldBe "\\top"
    }

    it("should do predicates") {
      asLaTeX(Predicate("P", List(Var("x")))) shouldBe "P(x)"
      asLaTeX(Predicate("P", List(Var("x"), Var("y"), Var("z")))) shouldBe "P(x, y, z)"
      asLaTeX(Predicate("Q", List(FunAppl("f", List(Var("x"), Var("y")))))) shouldBe "Q(f(x, y))"
    }

    it("should add brackets on binary operations") {
      asLaTeX(And(And(Tautology(), Tautology()), Tautology())) shouldBe "(\\top \\land \\top) \\land \\top"
      asLaTeX(Implies(Tautology(), Or(Tautology(), Tautology()))) shouldBe "\\top \\rightarrow (\\top \\lor \\top)"
      asLaTeX(Or(Tautology(), Implies(Tautology(), Tautology()))) shouldBe "\\top \\lor (\\top \\rightarrow \\top)"
    }
    
    it("should not add brackets on not") {
      asLaTeX(Not(Not(And(Contradiction(), Not(Not(Predicate("Q", List(Var("x"))))))))) shouldBe 
        "\\lnot \\lnot (\\bot \\land \\lnot \\lnot Q(x))"
    }

    it("should add brackets on equality") {
      asLaTeX(Not(Equals(Var("x"), Var("y")))) shouldBe 
        "\\lnot (x = y)"
    }

    it("should add brackets inside forall/exists, but not around") {
      asLaTeX(And(ForAll(Var("x"), Equals(Var("x"), Var("y"))), ForAll(Var("y"), Predicate("P", List(Var("y")))))) shouldBe
        "\\forall x (x = y) \\land \\forall y P(y)"
      asLaTeX(And(Exists(Var("x"), Equals(Var("x"), Var("y"))), Exists(Var("y"), Predicate("P", List(Var("y")))))) shouldBe
        "\\exists x (x = y) \\land \\exists y P(y)"
    }
  }

  describe("Stringifiers.propLogicFormulaAsASCII") {
    import Stringifiers.predLogicFormulaAsASCII => asASCII
    it("should do bot/top/predicates") {
      asASCII(Contradiction()) shouldBe "false"
      asASCII(Tautology()) shouldBe "true"
    }

    it("should do predicates") {
      asASCII(Predicate("P", List(Var("x")))) shouldBe "P(x)"
      asASCII(Predicate("P", List(Var("x"), Var("y"), Var("z")))) shouldBe "P(x, y, z)"
      asASCII(Predicate("Q", List(FunAppl("f", List(Var("x"), Var("y")))))) shouldBe "Q(f(x, y))"
    }

    it("should add brackets on binary operations") {
      asASCII(And(And(Tautology(), Tautology()), Tautology())) shouldBe "(true and true) and true"
      asASCII(Implies(Tautology(), Or(Tautology(), Tautology()))) shouldBe "true -> (true or true)"
      asASCII(Or(Tautology(), Implies(Tautology(), Tautology()))) shouldBe "true or (true -> true)"
    }
    
    it("should not add brackets on not") {
      asASCII(Not(Not(And(Contradiction(), Not(Not(Predicate("Q", List(Var("x"))))))))) shouldBe 
        "not not (false and not not Q(x))"
    }

    it("should add brackets on equality") {
      asASCII(Not(Equals(Var("x"), Var("y")))) shouldBe 
        "not (x = y)"
    }

    it("should add brackets inside forall/exists, but not around") {
      asASCII(And(ForAll(Var("x"), Equals(Var("x"), Var("y"))), ForAll(Var("y"), Predicate("P", List(Var("y")))))) shouldBe
        "forall x (x = y) and forall y P(y)"
      asASCII(And(Exists(Var("x"), Equals(Var("x"), Var("y"))), Exists(Var("y"), Predicate("P", List(Var("y")))))) shouldBe
        "exists x (x = y) and exists y P(y)"
    }
  }
}
