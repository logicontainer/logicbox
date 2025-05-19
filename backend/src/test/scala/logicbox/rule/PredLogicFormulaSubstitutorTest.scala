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
  }
}
