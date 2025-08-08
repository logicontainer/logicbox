package logicbox.ruleSubstitu

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import logicbox.formula._
import logicbox.rule.FormulaSubstitutor

class PredLogicFormulaSubstitutorTest extends AnyFunSpec {
  import Term._, Formula._
  private type Pred = FormulaKind.Pred

  private def parse(str: String): PredLogicFormula = {
    Parser.parse(Lexer(str), Parser.predLogicFormula)
  }

  val substitutor = FormulaSubstitutor[Pred]()

  describe("substitute") {
    it("should substitute x with y inside predicate") {
      val f1 = parse("P(x)")
      val exp1 = parse("P(y)")

      val f2 = parse("Q(x, z)")
      val exp2 = parse("Q(y, z)")

      substitutor.substitute(f1, Var[Pred]("y"), Var[Pred]("x")) shouldBe exp1
      substitutor.substitute(f2, Var[Pred]("y"), Var[Pred]("x")) shouldBe exp2
    }

    it("should substitute x with y inside function application") {
      val f1 = parse("P(f(x))")
      val exp1 = parse("P(f(y))")

      substitutor.substitute(f1, Var[Pred]("y"), Var[Pred]("x")) shouldBe exp1
    }

    it("should substitute inside connectives") {
      val f = parse("P(x, x) and P(z, x) or P(x, z) -> not P(z, x, z)")
      val exp = parse("P(y, y) and P(z, y) or P(y, z) -> not P(z, y, z)")

      substitutor.substitute(f, Var[Pred]("y"), Var[Pred]("x")) shouldBe exp
    }

    it("should leave contr./taut. alone") {
      substitutor.substitute(Contradiction(), Var[Pred]("y"), Var[Pred]("x")) shouldBe Contradiction()
      substitutor.substitute(Tautology(), Var[Pred]("y"), Var[Pred]("x")) shouldBe Tautology()
    }

    it("should work in equalities") {
      val f1 = parse("x = z")
      val exp1 = parse("y = z")
      substitutor.substitute(f1, Var[Pred]("y"), Var[Pred]("x")) shouldBe exp1

      val f2 = parse("z = x")
      val exp2 = parse("z = y")
      substitutor.substitute(f2, Var[Pred]("y"), Var[Pred]("x")) shouldBe exp2
    }

    it("should substitute free occurances within forall") {
      val f = parse("forall z P(x)")
      val exp = parse("forall z P(y)")

      substitutor.substitute(f, Var[Pred]("y"), Var[Pred]("x")) shouldBe exp
    }

    it("should not substitute forall-bound occurances") {
      val f = parse("forall x P(x)")
      val exp = parse("forall x P(x)")

      substitutor.substitute(f, Var[Pred]("y"), Var[Pred]("x")) shouldBe exp
    }

    it("should substitute free occurances within exists") {
      val f = parse("exists z P(x)")
      val exp = parse("exists z P(y)")

      substitutor.substitute(f, Var[Pred]("y"), Var[Pred]("x")) shouldBe exp
    }

    it("should not substitute exists-bound occurances") {
      val f = parse("exists x P(x)")
      val exp = parse("exists x P(x)")

      substitutor.substitute(f, Var[Pred]("y"), Var[Pred]("x")) shouldBe exp
    }
  }

  describe("hasFreeOccurance") {
    it("should find occurance within predicate") {
      val f = parse("P(a)")
      substitutor.hasFreeOccurance(f, Var[Pred]("a")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Pred]("b")) shouldBe false
    }

    it("should find occurance within function") {
      val f = parse("P(f(a))")
      substitutor.hasFreeOccurance(f, Var[Pred]("a")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Pred]("b")) shouldBe false

      val g = parse("P(f(f(b)))")
      substitutor.hasFreeOccurance(g, Var[Pred]("b")) shouldBe true
      substitutor.hasFreeOccurance(g, Var[Pred]("a")) shouldBe false
    }

    it("should find occurances of functions") {
      val f = parse("P(f(f(a)))")
      substitutor.hasFreeOccurance(f, FunAppl("f", Var[Pred]("a") :: Nil)) shouldBe true
      substitutor.hasFreeOccurance(f, FunAppl("f", FunAppl("f", Var[Pred]("a") :: Nil) :: Nil)) shouldBe true
    }

    it("should find occurances within connectives and equality") {
      val f = parse("P(a) and P(b) or Q(c) implies Q(d) and not G(f)")
      substitutor.hasFreeOccurance(f, Var[Pred]("a")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Pred]("b")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Pred]("c")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Pred]("d")) shouldBe true

      substitutor.hasFreeOccurance(f, Var[Pred]("g")) shouldBe false
      substitutor.hasFreeOccurance(f, Var[Pred]("h")) shouldBe false
      substitutor.hasFreeOccurance(f, Var[Pred]("k")) shouldBe false
      substitutor.hasFreeOccurance(f, Var[Pred]("v")) shouldBe false
    }

    it("should find occurances with equality") {
      val f = parse("a = b")
      substitutor.hasFreeOccurance(f, Var[Pred]("a")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Pred]("b")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Pred]("c")) shouldBe false
    }

    it("should not find any free occurances in false/true") {
      substitutor.hasFreeOccurance(Contradiction(), Var[Pred]("a")) shouldBe false
      substitutor.hasFreeOccurance(Contradiction(), Var[Pred]("z")) shouldBe false
      substitutor.hasFreeOccurance(Tautology(), Var[Pred]("a")) shouldBe false
      substitutor.hasFreeOccurance(Tautology(), Var[Pred]("z")) shouldBe false
    }
    
    it("should find free occurances within forall") {
      val f = parse("forall x P(y, z)")
      substitutor.hasFreeOccurance(f, Var[Pred]("y")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Pred]("z")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Pred]("a")) shouldBe false
    }

    it("should not find bound occurances within forall") {
      val f = parse("forall x P(y, x)")
      substitutor.hasFreeOccurance(f, Var[Pred]("x")) shouldBe false
    }

    it("should find free occurances within exists") {
      val f = parse("exists x P(y, z)")
      substitutor.hasFreeOccurance(f, Var[Pred]("y")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Pred]("z")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Pred]("a")) shouldBe false
    }

    it("should not find bound occurances within exists") {
      val f = parse("exists x P(y, x)")
      substitutor.hasFreeOccurance(f, Var[Pred]("x")) shouldBe false
    }
  }

  describe("findReplacement") {
    it("should find replacement in equality 1") {
      val src = parse("x = x")
      val dst = parse("y = y")
      val x = Var[Pred]("x")

      val exp = Some(Right(Var[Pred]("y")))

      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacement in equality 2") {
      val src = parse("x = z")
      val dst = parse("z = z")
      val x = Var[Pred]("x")

      val exp = Some(Right(Var[Pred]("z")))

      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacement in f(x) -> f(y)") {
      val src = parse("f(x) = z")
      val dst = parse("f(y) = z")
      val x = Var[Pred]("x")

      val exp = Some(Right(Var[Pred]("y")))
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacement in f(x, z, x) -> f(y, z, y)") {
      val src = parse("f(x, z) = z")
      val dst = parse("f(y, z) = z")
      val x = Var[Pred]("x")

      val exp = Some(Right(Var[Pred]("y")))
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should disallow conflicting replacements") {
      val src = parse("x = x")
      val dst = parse("y = z")
      val x = Var[Pred]("x")

      val exp = None
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should work with equality that has no replacements") {
      val src = parse("a = b")
      val dst = parse("a = b")
      val x = Var[Pred]("x")

      val exp = Some(Left(()))
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should disallow structurally different formulas") {
      val src = parse("f(x) = b")
      val dst = parse("y = b")
      val x = Var[Pred]("x")

      val exp = None
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should not replace when function symbols differ") {
      val src = parse("f(x) = b")
      val dst = parse("g(y) = b")
      val x = Var[Pred]("x")

      val exp = None
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacements when nothing happens but there are occurances of x") {
      val f = parse("f(g(x, x)) = f(f(g(x, x), x))")
      val x = Var[Pred]("x")

      val exp = Some(Right(x)) // x replaced with itself
      substitutor.findReplacement(f, f, x) shouldBe exp
    }

    it("should disallow repl. when other variables are not equal") {
      val src = parse("x = b")
      val dst = parse("y = c")
      val x = Var[Pred]("x")

      val exp = None
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacement in predicate 1") {
      val src = parse("P(x)")
      val dst = parse("P(y)")
      val x = Var[Pred]("x")

      val exp = Some(Right(Var[Pred]("y")))
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacement in predicate 2") {
      val src = parse("P(x, z, x)")
      val dst = parse("P(y, z, y)")
      val x = Var[Pred]("x")

      val exp = Some(Right(Var[Pred]("y")))
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacements inside connectives") {
      val src = parse("P(x) and P(x) or P(x) implies P(x) and not P(x)")
      val dst = parse("P(y) and P(y) or P(y) implies P(y) and not P(y)")
      val x = Var[Pred]("x")


      val exp = Some(Right(Var[Pred]("y")))
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacements inside forall") {
      val src = parse("forall z P(x)")
      val dst = parse("forall z P(y)")
      val x = Var[Pred]("x")

      val exp = Some(Right(Var[Pred]("y")))
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should not allow replacements of forall-bound occurances") {
      val src = parse("forall x P(x)")
      val dst = parse("forall x P(y)")

      val x = Var[Pred]("x")

      val exp = None
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should allow no replacements inside forall bound by replacement var") {
      val src = parse("forall x P(z)")
      val dst = parse("forall x P(z)")

      val x = Var[Pred]("x")

      val exp = Some(Left(()))
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacements inside exists") {
      val src = parse("exists z P(x)")
      val dst = parse("exists z P(y)")
      val x = Var[Pred]("x")

      val exp = Some(Right(Var[Pred]("y")))
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should not allow replacements of exists-bound occurances") {
      val src = parse("exists x P(x)")
      val dst = parse("exists x P(y)")

      val x = Var[Pred]("x")

      val exp = None
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should allow no replacements inside exists bound by replacement var") {
      val src = parse("exists x P(z)")
      val dst = parse("exists x P(z)")

      val x = Var[Pred]("x")

      val exp = Some(Left(()))
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should disallow replacement when forall-quantified vars are not equal") {
      val src = parse("forall x P(z)")
      val dst = parse("forall y P(z)")
      val x = Var[Pred]("x")

      val exp = None
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should disallow replacement when exists-quantified vars are not equal") {
      val src = parse("exists x P(z)")
      val dst = parse("exists y P(z)")
      val x = Var[Pred]("x")

      val exp = None
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should work with contr./taut.") {
      val src1 = parse("true")
      val dst1 = parse("true")
      val src2 = parse("false")
      val dst2 = parse("false")


      val x = Var[Pred]("x")

      val exp = Some(Left(()))
      substitutor.findReplacement(src1, dst1, x) shouldBe exp
      substitutor.findReplacement(src2, dst2, x) shouldBe exp
    }

    it("should not allow replacement when predicate symbols do not match") {
      val src = parse("P(x)")
      val dst = parse("Q(y)")
      val x = Var[Pred]("x")

      val exp = None
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }
  }

  describe("equalExcept") {
    it("should be true if no occurance of t1 and f1 = f2") {
      val f1 = parse("P(a)")
      val f2 = parse("P(a)")
      val t1 = Var[Pred]("b")
      val t2 = Var[Pred]("c")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }
    
    it("should be false t1 is replaced by other than t2") {
      val f1 = parse("P(a)")
      val f2 = parse("P(b)")
      val t1 = Var[Pred]("a")
      val t2 = Var[Pred]("c")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should allow good replacement P(x) -> P(y)") {
      val f1 = parse("P(x)")
      val f2 = parse("P(y)")
      val t1 = Var[Pred]("x")
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should not allow when replacement is not correct") {
      val f1 = parse("P(x)")
      val f2 = parse("P(y)")
      val t1 = Var[Pred]("z")
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should not allow when predicate symbols are not equal") {
      val f1 = parse("P(x)")
      val f2 = parse("Q(x)")
      val t1 = Var[Pred]("z")
      val t2 = Var[Pred]("z")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should work on predicates with multiple args") {
      val f1 = parse("P(x, y, x)")
      val f2 = parse("P(y, y, x)")
      val t1 = Var[Pred]("x")
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should be true when variable inside function is replaced") {
      val f1 = parse("P(f(x))")
      val f2 = parse("P(f(y))")
      val t1 = Var[Pred]("x") 
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should be false when invalid replacement inside function") {
      val f1 = parse("P(f(x))")
      val f2 = parse("P(f(z))")
      val t1 = Var[Pred]("x") 
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should work with multiple function args") {
      val f1 = parse("P(f(x, z, x))")
      val f2 = parse("P(f(y, z, x))")
      val t1 = Var[Pred]("x") 
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should reject when function symbols are not equal") {
      val f1 = parse("P(g(x, z, x))")
      val f2 = parse("P(f(y, z, x))")
      val t1 = Var[Pred]("x") 
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should allow equality with repl on lhs") {
      val f1 = parse("x = z")
      val f2 = parse("y = z")
      val t1 = Var[Pred]("x") 
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should not allow invalid repl on lhs in equality") {
      val f1 = parse("x = z")
      val f2 = parse("z = z")
      val t1 = Var[Pred]("x") 
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should allow equality with repl on rhs") {
      val f1 = parse("z = x")
      val f2 = parse("z = y")
      val t1 = Var[Pred]("x") 
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should not allow invalid repl on rhs in equality") {
      val f1 = parse("z = x")
      val f2 = parse("z = z")
      val t1 = Var[Pred]("x") 
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should be true if function is replaced with variable") {
      val f1 = parse("z = f(x)")
      val f2 = parse("z = y")
      val t1 = FunAppl("f", Var[Pred]("x") :: Nil)
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should be false if function is invalidly replaced with variable") {
      val f1 = parse("z = f(y)")
      val f2 = parse("z = y")
      val t1 = FunAppl("f", Var[Pred]("x") :: Nil)
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should work with stuff") {
      val f1 = parse("z = f(f(x))")
      val f2 = parse("z = f(f(f(x)))")
      val t1 = FunAppl("f", Var[Pred]("x") :: Nil)
      val t2 = FunAppl("f", FunAppl("f", Var[Pred]("x") :: Nil) :: Nil)

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should work inside connectives 1") {
      val f1 = parse("x = x and x = x or x = x implies x = x and not x = x")
      val f2 = parse("x = y and y = x or y = x implies x = y and not y = y")
      val t1 = Var[Pred]("x")
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should work inside connectives 2") {
      val f1 = parse("x = x and x = x or f(x) = x implies x = x and not x = x")
      val f2 = parse("x = y and y = x or f(z) = x implies x = y and not y = y") // invalid
      val t1 = Var[Pred]("x")
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should be true if happens on free occur. inside forall") {
      val f1 = parse("forall x (x = f(z))")
      val f2 = parse("forall x (x = f(y))")
      val t1 = Var[Pred]("z")
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should be true if nothing happens and forall-quantified variable is t1") {
      val f1 = parse("forall x (x = f(x))")
      val f2 = parse("forall x (x = f(x))")
      val t1 = Var[Pred]("x")
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should be false if replacement happens on forall-bound var") {
      val f1 = parse("forall x (x = f(x))")
      val f2 = parse("forall x (x = f(y))")
      val t1 = Var[Pred]("x")
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should not allow forall-quantified variable to change") {
      val f1 = parse("forall x (x = f(x))")
      val f2 = parse("forall y (x = f(y))")
      val t1 = Var[Pred]("x")
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should be true if happens on free occur. inside exists") {
      val f1 = parse("exists x (x = f(z))")
      val f2 = parse("exists x (x = f(y))")
      val t1 = Var[Pred]("z")
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should be true if nothing happens and exists-quantified variable is t1") {
      val f1 = parse("exists x (x = f(x))")
      val f2 = parse("exists x (x = f(x))")
      val t1 = Var[Pred]("x")
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should be false if replacement happens on exists-bound var") {
      val f1 = parse("exists x (x = f(x))")
      val f2 = parse("exists x (x = f(y))")
      val t1 = Var[Pred]("x")
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should not allow exists-quantified variable to change") {
      val f1 = parse("exists x (x = f(x))")
      val f2 = parse("exists y (x = f(y))")
      val t1 = Var[Pred]("x")
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should be happy with simple things") {
      val f1 = parse("false and not true -> false or false and not true")
      val f2 = parse("false and not true -> false or false and not true")
      val t1 = Var[Pred]("x")
      val t2 = Var[Pred]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }
  }
}
