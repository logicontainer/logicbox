package logicbox.rule

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import logicbox.formula.{PredLogicLexer, PredLogicParser, PredLogicFormula}
import logicbox.formula.PredLogicTerm._
import logicbox.formula.PredLogicFormula._

class PredLogicFormulaSubstitutorTest extends AnyFunSpec {
  private def parse(str: String): PredLogicFormula = {
    val lexer = PredLogicLexer()
    val parser = PredLogicParser()
    parser(lexer(str))
  }

  val substitutor = PredLogicFormulaSubstitutor()

  describe("substitute") {
    it("should substitute x with y inside predicate") {
      val f1 = parse("P(x)")
      val exp1 = parse("P(y)")

      val f2 = parse("Q(x, z)")
      val exp2 = parse("Q(y, z)")

      substitutor.substitute(f1, Var('y'), Var('x')) shouldBe exp1
      substitutor.substitute(f2, Var('y'), Var('x')) shouldBe exp2
    }

    it("should substitute x with y inside function application") {
      val f1 = parse("P(f(x))")
      val exp1 = parse("P(f(y))")

      substitutor.substitute(f1, Var('y'), Var('x')) shouldBe exp1
    }

    it("should substitute inside connectives") {
      val f = parse("P(x, x) and P(z, x) or P(x, z) -> not P(z, x, z)")
      val exp = parse("P(y, y) and P(z, y) or P(y, z) -> not P(z, y, z)")

      substitutor.substitute(f, Var('y'), Var('x')) shouldBe exp
    }

    it("should leave contr./taut. alone") {
      substitutor.substitute(Contradiction(), Var('y'), Var('x')) shouldBe Contradiction()
      substitutor.substitute(Tautology(), Var('y'), Var('x')) shouldBe Tautology()
    }

    it("should work in equalities") {
      val f1 = parse("x = z")
      val exp1 = parse("y = z")
      substitutor.substitute(f1, Var('y'), Var('x')) shouldBe exp1

      val f2 = parse("z = x")
      val exp2 = parse("z = y")
      substitutor.substitute(f2, Var('y'), Var('x')) shouldBe exp2
    }

    it("should substitute free occurances within forall") {
      val f = parse("forall z P(x)")
      val exp = parse("forall z P(y)")

      substitutor.substitute(f, Var('y'), Var('x')) shouldBe exp
    }

    it("should not substitute forall-bound occurances") {
      val f = parse("forall x P(x)")
      val exp = parse("forall x P(x)")

      substitutor.substitute(f, Var('y'), Var('x')) shouldBe exp
    }

    it("should substitute free occurances within exists") {
      val f = parse("exists z P(x)")
      val exp = parse("exists z P(y)")

      substitutor.substitute(f, Var('y'), Var('x')) shouldBe exp
    }

    it("should not substitute exists-bound occurances") {
      val f = parse("exists x P(x)")
      val exp = parse("exists x P(x)")

      substitutor.substitute(f, Var('y'), Var('x')) shouldBe exp
    }

    it("should find occurance within predicate") {
      val f = parse("P(a)")
      substitutor.hasFreeOccurance(f, Var('a')) shouldBe true
      substitutor.hasFreeOccurance(f, Var('b')) shouldBe false
    }

    it("should find occurance within function") {
      val f = parse("P(f(a))")
      substitutor.hasFreeOccurance(f, Var('a')) shouldBe true
      substitutor.hasFreeOccurance(f, Var('b')) shouldBe false

      val g = parse("P(f(f(b)))")
      substitutor.hasFreeOccurance(g, Var('b')) shouldBe true
      substitutor.hasFreeOccurance(g, Var('a')) shouldBe false
    }

    it("should find occurances of functions") {
      val f = parse("P(f(f(a)))")
      substitutor.hasFreeOccurance(f, FunAppl('f', Var('a') :: Nil)) shouldBe true
      substitutor.hasFreeOccurance(f, FunAppl('f', FunAppl('f', Var('a') :: Nil) :: Nil)) shouldBe true
    }

    it("should find occurances within connectives and equality") {
      val f = parse("P(a) and P(b) or Q(c) implies Q(d) and not G(f)")
      substitutor.hasFreeOccurance(f, Var('a')) shouldBe true
      substitutor.hasFreeOccurance(f, Var('b')) shouldBe true
      substitutor.hasFreeOccurance(f, Var('c')) shouldBe true
      substitutor.hasFreeOccurance(f, Var('d')) shouldBe true

      substitutor.hasFreeOccurance(f, Var('g')) shouldBe false
      substitutor.hasFreeOccurance(f, Var('h')) shouldBe false
      substitutor.hasFreeOccurance(f, Var('k')) shouldBe false
      substitutor.hasFreeOccurance(f, Var('v')) shouldBe false
    }

    it("should find occurances with equality") {
      val f = parse("a = b")
      substitutor.hasFreeOccurance(f, Var('a')) shouldBe true
      substitutor.hasFreeOccurance(f, Var('b')) shouldBe true
      substitutor.hasFreeOccurance(f, Var('c')) shouldBe false
    }

    it("should not find any free occurances in false/true") {
      substitutor.hasFreeOccurance(Contradiction(), Var('a')) shouldBe false
      substitutor.hasFreeOccurance(Contradiction(), Var('z')) shouldBe false
      substitutor.hasFreeOccurance(Tautology(), Var('a')) shouldBe false
      substitutor.hasFreeOccurance(Tautology(), Var('z')) shouldBe false
    }
    
    it("should find free occurances within forall") {
      val f = parse("forall x P(y, z)")
      substitutor.hasFreeOccurance(f, Var('y')) shouldBe true
      substitutor.hasFreeOccurance(f, Var('z')) shouldBe true
      substitutor.hasFreeOccurance(f, Var('a')) shouldBe false
    }

    it("should not find bound occurances within forall") {
      val f = parse("forall x P(y, x)")
      substitutor.hasFreeOccurance(f, Var('x')) shouldBe false
    }

    it("should find free occurances within exists") {
      val f = parse("exists x P(y, z)")
      substitutor.hasFreeOccurance(f, Var('y')) shouldBe true
      substitutor.hasFreeOccurance(f, Var('z')) shouldBe true
      substitutor.hasFreeOccurance(f, Var('a')) shouldBe false
    }

    it("should not find bound occurances within exists") {
      val f = parse("exists x P(y, x)")
      substitutor.hasFreeOccurance(f, Var('x')) shouldBe false
    }
  }
}
