package logicbox.rule

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import logicbox.formula._
import logicbox.rule.FormulaSubstitutor

class FormulaSubstitutorTest extends AnyFunSpec {
  import Term._, Formula._
  private type Pred = FormulaKind.Pred
  private type Arith = FormulaKind.Arith

  private def parse(str: String): PredLogicFormula = {
    Parser.parse(Lexer(str), Parser.predLogicFormula)
  }

  val substitutor = FormulaSubstitutor[Pred]()

  describe("substitute (w. pred formulas)") {
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

  describe("hasFreeOccurance (w. pred formulas)") {
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

  describe("findReplacement (w. pred formulas)") {
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

  describe("equalExcept (w. pred formulas)") {
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

  val arithSubs = FormulaSubstitutor[Arith]()
  def arithParse(s: String) = Parser.parse(Lexer(s), Parser.arithLogicFormula)

  describe("substitute (w. arith formulas)") {
    it("should leave contr./taut. alone") {
      arithSubs.substitute(Contradiction(), Var[Arith]("y"), Var[Arith]("x")) shouldBe Contradiction()
      arithSubs.substitute(Tautology(), Var[Arith]("y"), Var[Arith]("x")) shouldBe Tautology()
    }

    it("should work in equalities") {
      val f1 = arithParse("x = z")
      val exp1 = arithParse("y = z")
      arithSubs.substitute(f1, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp1

      val f2 = arithParse("z = x")
      val exp2 = arithParse("z = y")
      arithSubs.substitute(f2, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp2
    }

    it("should leave 0, 1 alone") {
      val f = arithParse("0 = 1")
      arithSubs.substitute(f, Var[Arith]("y"), Var[Arith]("x")) shouldBe f
    }

    it("should substitute inside addition") {
      val f = arithParse("y + x = x + y")
      val exp = arithParse("y + y = y + y")
      arithSubs.substitute(f, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp
    }

    it("should substitute inside multiplication") {
      val f = arithParse("y * x = x * y")
      val exp = arithParse("y * y = y * y")
      arithSubs.substitute(f, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp
    }

    it("should substitute inside connectives") {
      val f =   arithParse("x = x and z = x or x = z -> not (z = x and x = z)")
      val exp = arithParse("y = y and z = y or y = z -> not (z = y and y = z)")

      arithSubs.substitute(f, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp
    }

    it("should substitute free occurances within forall") {
      val f = arithParse("forall z x = x")
      val exp = arithParse("forall z y = y")

      arithSubs.substitute(f, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp
    }

    it("should not substitute forall-bound occurances") {
      val f = arithParse("forall x x = x")
      val exp = arithParse("forall x x = x")

      arithSubs.substitute(f, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp
    }

    it("should substitute free occurances within exists") {
      val f = arithParse("exists z x = x")
      val exp = arithParse("exists z y = y")

      arithSubs.substitute(f, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp
    }

    it("should not substitute exists-bound occurances") {
      val f = arithParse("exists x x = x")
      val exp = arithParse("exists x x = x")

      arithSubs.substitute(f, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp
    }
  }

  describe("hasFreeOccurance (w. arith formulas)") {
    it("should find occurances with equality") {
      val f = arithParse("a = b")
      arithSubs.hasFreeOccurance(f, Var[Arith]("a")) shouldBe true
      arithSubs.hasFreeOccurance(f, Var[Arith]("b")) shouldBe true
      arithSubs.hasFreeOccurance(f, Var[Arith]("c")) shouldBe false
    }

    it("should find occurances within addition") {
      val f = arithParse("a + b = k")
      arithSubs.hasFreeOccurance(f, Var[Arith]("a")) shouldBe true
      arithSubs.hasFreeOccurance(f, Var[Arith]("b")) shouldBe true
      arithSubs.hasFreeOccurance(f, Var[Arith]("c")) shouldBe false
    }

    it("should find occurances within multiplication") {
      val f = arithParse("k = a * b")
      arithSubs.hasFreeOccurance(f, Var[Arith]("a")) shouldBe true
      arithSubs.hasFreeOccurance(f, Var[Arith]("b")) shouldBe true
      arithSubs.hasFreeOccurance(f, Var[Arith]("c")) shouldBe false
    }

    it("should find no occurances inside 0, 1") {
      val f = arithParse("0 = 1")
      arithSubs.hasFreeOccurance(f, Var[Arith]("a")) shouldBe false
      arithSubs.hasFreeOccurance(f, Var[Arith]("b")) shouldBe false
      arithSubs.hasFreeOccurance(f, Var[Arith]("c")) shouldBe false
    }

    it("should find occurances within connectives and equality") {
      val f = arithParse("a = a and b = b or c = c implies d = d and not f = f")
      arithSubs.hasFreeOccurance(f, Var[Arith]("a")) shouldBe true
      arithSubs.hasFreeOccurance(f, Var[Arith]("b")) shouldBe true
      arithSubs.hasFreeOccurance(f, Var[Arith]("c")) shouldBe true
      arithSubs.hasFreeOccurance(f, Var[Arith]("d")) shouldBe true

      arithSubs.hasFreeOccurance(f, Var[Arith]("g")) shouldBe false
      arithSubs.hasFreeOccurance(f, Var[Arith]("h")) shouldBe false
      arithSubs.hasFreeOccurance(f, Var[Arith]("k")) shouldBe false
      arithSubs.hasFreeOccurance(f, Var[Arith]("v")) shouldBe false
    }

    it("should not find any free occurances in false/true") {
      arithSubs.hasFreeOccurance(Contradiction(), Var[Arith]("a")) shouldBe false
      arithSubs.hasFreeOccurance(Contradiction(), Var[Arith]("z")) shouldBe false
      arithSubs.hasFreeOccurance(Tautology(), Var[Arith]("a")) shouldBe false
      arithSubs.hasFreeOccurance(Tautology(), Var[Arith]("z")) shouldBe false
    }
    
    it("should find free occurances within forall") {
      val f = arithParse("forall x y = z")
      arithSubs.hasFreeOccurance(f, Var[Arith]("y")) shouldBe true
      arithSubs.hasFreeOccurance(f, Var[Arith]("z")) shouldBe true
      arithSubs.hasFreeOccurance(f, Var[Arith]("a")) shouldBe false
    }

    it("should not find bound occurances within forall") {
      val f = arithParse("forall x y = x")
      arithSubs.hasFreeOccurance(f, Var[Arith]("x")) shouldBe false
    }

    it("should find free occurances within exists") {
      val f = arithParse("exists x y = z")
      arithSubs.hasFreeOccurance(f, Var[Arith]("y")) shouldBe true
      arithSubs.hasFreeOccurance(f, Var[Arith]("z")) shouldBe true
      arithSubs.hasFreeOccurance(f, Var[Arith]("a")) shouldBe false
    }

    it("should not find bound occurances within exists") {
      val f = arithParse("exists x y = x")
      arithSubs.hasFreeOccurance(f, Var[Arith]("x")) shouldBe false
    }
  }

  describe("findReplacement (w. arith formulas)") {
    it("should find replacement in equality 1") {
      val src = arithParse("x = x")
      val dst = arithParse("y = y")
      val x = Var[Arith]("x")

      val exp = Some(Right(Var[Arith]("y")))

      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacement in equality 2") {
      val src = arithParse("x = z")
      val dst = arithParse("z = z")
      val x = Var[Arith]("x")

      val exp = Some(Right(Var[Arith]("z")))

      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacement in addition") {
      val src = arithParse("z + x = x + k")
      val dst = arithParse("z + y = y + k")
      val x = Var[Arith]("x")

      val exp = Some(Right(Var[Arith]("y")))

      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacement in multiplication") {
      val src = arithParse("z * x = x * k")
      val dst = arithParse("z * y = y * k")
      val x = Var[Arith]("x")

      val exp = Some(Right(Var[Arith]("y")))

      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should allow any replacements in 0") {
      val src = arithParse("0 = 0")
      val dst = arithParse("0 = 0")
      val x = Var[Arith]("x")

      val exp = Some(Left(()))

      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should allow any replacements in 1") {
      val src = arithParse("1 = 1")
      val dst = arithParse("1 = 1")
      val x = Var[Arith]("x")

      val exp = Some(Left(()))

      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should disallow conflicting replacements") {
      val src = arithParse("x = x")
      val dst = arithParse("y = z")
      val x = Var[Arith]("x")

      val exp = None
      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should work with equality that has no replacements") {
      val src = arithParse("a = b")
      val dst = arithParse("a = b")
      val x = Var[Arith]("x")

      val exp = Some(Left(()))
      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should disallow structurally different formulas") {
      val src = arithParse("x + 0 = b")
      val dst = arithParse("y = b")
      val x = Var[Arith]("x")

      val exp = None
      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacements when nothing happens but there are occurances of x") {
      val f = arithParse("x = x and y = x + 0 * 1 * x")
      val x = Var[Arith]("x")

      val exp = Some(Right(x)) // x replaced with itself
      arithSubs.findReplacement(f, f, x) shouldBe exp
    }

    it("should disallow repl. when other variables are not equal") {
      val src = arithParse("x = b")
      val dst = arithParse("y = c")
      val x = Var[Arith]("x")

      val exp = None
      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacements inside connectives") {
      val src = arithParse("x = x and x = x or x = x implies x = x and not x = x")
      val dst = arithParse("y = y and y = y or y = y implies y = y and not y = y")
      val x = Var[Arith]("x")

      val exp = Some(Right(Var[Arith]("y")))
      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacements inside forall") {
      val src = arithParse("forall z x = x")
      val dst = arithParse("forall z y = y")
      val x = Var[Arith]("x")

      val exp = Some(Right(Var[Arith]("y")))
      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should not allow replacements of forall-bound occurances") {
      val src = arithParse("forall x x = x")
      val dst = arithParse("forall x y = y")

      val x = Var[Arith]("x")

      val exp = None
      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should allow no replacements inside forall bound by replacement var") {
      val src = arithParse("forall x z = z")
      val dst = arithParse("forall x z = z")

      val x = Var[Arith]("x")

      val exp = Some(Left(()))
      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacements inside exists") {
      val src = arithParse("exists z x = x")
      val dst = arithParse("exists z y = y")
      val x = Var[Arith]("x")

      val exp = Some(Right(Var[Arith]("y")))
      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should not allow replacements of exists-bound occurances") {
      val src = arithParse("exists x x = x")
      val dst = arithParse("exists x y = y")

      val x = Var[Arith]("x")

      val exp = None
      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should allow no replacements inside exists bound by replacement var") {
      val src = arithParse("exists x z = z")
      val dst = arithParse("exists x z = z")

      val x = Var[Arith]("x")

      val exp = Some(Left(()))
      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should disallow replacement when forall-quantified vars are not equal") {
      val src = arithParse("forall x z = z")
      val dst = arithParse("forall y z = z")
      val x = Var[Arith]("x")

      val exp = None
      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should disallow replacement when exists-quantified vars are not equal") {
      val src = arithParse("exists x z = z")
      val dst = arithParse("exists y z = z")
      val x = Var[Arith]("x")

      val exp = None
      arithSubs.findReplacement(src, dst, x) shouldBe exp
    }

    it("should work with contr./taut.") {
      val src1 = arithParse("true")
      val dst1 = arithParse("true")
      val src2 = arithParse("false")
      val dst2 = arithParse("false")


      val x = Var[Arith]("x")

      val exp = Some(Left(()))
      arithSubs.findReplacement(src1, dst1, x) shouldBe exp
      arithSubs.findReplacement(src2, dst2, x) shouldBe exp
    }
  }

  describe("equalExcept (w. arith formulas)") {
    it("should be true if no occurance of t1 and f1 = f2") {
      val f1 = arithParse("a = a")
      val f2 = arithParse("a = a")
      val t1 = Var[Arith]("b")
      val t2 = Var[Arith]("c")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }
    
    it("should be false t1 is replaced by other than t2") {
      val f1 = arithParse("a = a")
      val f2 = arithParse("a = b")
      val t1 = Var[Arith]("a")
      val t2 = Var[Arith]("c")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should allow good replacement x = a -> y = a") {
      val f1 = arithParse("x = a")
      val f2 = arithParse("y = a")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should allow good replacement a = x -> a = y") {
      val f1 = arithParse("a = x")
      val f2 = arithParse("a = y")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should allow equality with repl on lhs") {
      val f1 = arithParse("x = z")
      val f2 = arithParse("y = z")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should not allow invalid repl on lhs in equality") {
      val f1 = arithParse("x = z")
      val f2 = arithParse("z = z")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should allow equality with repl on rhs") {
      val f1 = arithParse("z = x")
      val f2 = arithParse("z = y")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should not allow invalid repl on rhs in equality") {
      val f1 = arithParse("z = x")
      val f2 = arithParse("z = z")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should allow addition with repl on lhs") {
      val f1 = arithParse("z = x + k")
      val f2 = arithParse("z = y + k")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should not allow addition with invalid repl on lhs") {
      val f1 = arithParse("z = x + k")
      val f2 = arithParse("z = z + k")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should allow addition with repl on rhs") {
      val f1 = arithParse("z = k + x + k")
      val f2 = arithParse("z = k + y + k")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should not allow addition with invalid repl on rhs") {
      val f1 = arithParse("z = k + x + k")
      val f2 = arithParse("z = k + z + k")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should allow mult with repl on lhs") {
      val f1 = arithParse("z = x * k")
      val f2 = arithParse("z = y * k")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should not allow mult with invalid repl on lhs") {
      val f1 = arithParse("z = x * k")
      val f2 = arithParse("z = z * k")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should allow mult with repl on rhs") {
      val f1 = arithParse("z = k * x + k")
      val f2 = arithParse("z = k * y + k")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should not allow mult with invalid repl on rhs") {
      val f1 = arithParse("z = k * x + k")
      val f2 = arithParse("z = k * z + k")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should be true if addition is replaced with variable") {
      val f1 = arithParse("z = x + x")
      val f2 = arithParse("z = y")
      val t1 = Plus(Var[Arith]("x"), Var[Arith]("x"))
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should say yes if it is just 0") {
      val f1 = arithParse("0 = 0")
      val f2 = arithParse("0 = 0")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should say no if 0 repl with 1") {
      val f1 = arithParse("0 = 0")
      val f2 = arithParse("0 = 1")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should be false if function is invalidly replaced with variable") {
      val f1 = arithParse("z = x + y")
      val f2 = arithParse("z = y")
      val t1 = Plus(Var[Arith]("x"), Var[Arith]("x"))
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should work on complex example 1") {
      val f1 = arithParse("z + 0 * ((x * z + 1) + y) = x * (z + ((x * z + 1) + y) * y) + 0 * 1")
      val f2 = arithParse("z + 0 * (a           + y) = x * (z + (a           + y) * y) + 0 * 1")
      val t1 = Plus(Mult(Var[Arith]("x"), Var[Arith]("z")), One())
      val t2 = Var[Arith]("a")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }
    
    it("should work on complex example 2") {
      val f1 = arithParse("z + 0 * ((x * z + 1) + y) = x * (z + ((x * z + 1) + y) * y) + 0 * 1")
      val f2 = arithParse("z + 0 * ((a + 0)     + y) = x * (z + ((a + 0)     + y) * y) + 0 * 1")
      val t1 = Plus(Mult(Var[Arith]("x"), Var[Arith]("z")), One())
      val t2 = Plus(Var[Arith]("a"), Zero())

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should work on complex example 3") {
      val f1 = arithParse("x + 0 * (x + y) = x + y")
      val f2 = arithParse("x + 0 * a = a")
      val t1 = Plus(Var[Arith]("x"), Var[Arith]("y"))
      val t2 = Var[Arith]("a")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should work inside connectives 1") {
      val f1 = arithParse("x = x and x = x or x = x implies x = x and not x = x")
      val f2 = arithParse("x = y and y = x or y = x implies x = y and not y = y")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should work inside connectives 2") {
      val f1 = arithParse("x = x and x = x or x + x = x implies x = x and not x = x")
      val f2 = arithParse("x = y and y = x or x + z = x implies x = y and not y = y") // invalid
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should be true if happens on free occur. inside forall") {
      val f1 = arithParse("forall x (x = 0 + z)")
      val f2 = arithParse("forall x (x = 0 + y)")
      val t1 = Var[Arith]("z")
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should be true if nothing happens and forall-quantified variable is t1") {
      val f1 = arithParse("forall x (x = 0 + x)")
      val f2 = arithParse("forall x (x = 0 + x)")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should be false if replacement happens on forall-bound var") {
      val f1 = arithParse("forall x (x = 0 + x)")
      val f2 = arithParse("forall x (x = 0 + y)")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should not allow forall-quantified variable to change") {
      val f1 = arithParse("forall x (x = 0 + x)")
      val f2 = arithParse("forall y (x = 0 + y)")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should be true if happens on free occur. inside exists") {
      val f1 = arithParse("exists x (x = 0 + z)")
      val f2 = arithParse("exists x (x = 0 + y)")
      val t1 = Var[Arith]("z")
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should be true if nothing happens and exists-quantified variable is t1") {
      val f1 = arithParse("exists x (x = 0 + x)")
      val f2 = arithParse("exists x (x = 0 + x)")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should be false if replacement happens on exists-bound var") {
      val f1 = arithParse("exists x (x = 0 + x)")
      val f2 = arithParse("exists x (x = 0 + y)")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should not allow exists-quantified variable to change") {
      val f1 = arithParse("exists x (x = 0 + x)")
      val f2 = arithParse("exists y (x = 0 + y)")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should be happy with simple things") {
      val f1 = arithParse("false and not true -> false or false and not true")
      val f2 = arithParse("false and not true -> false or false and not true")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      arithSubs.equalExcept(f1, f2, t1, t2) shouldBe true
    }
  }

  describe("isFreeFor") {
    it("should say yes for simple formula") {
      val f = parse("false")
      val t = Var[Pred]("y")
      val x = Var[Pred]("x")

      substitutor.isFreeFor(f, t, x) shouldBe true
    }

    it("should say no for 'forall y P(x)' -> 'forall y P(y)' ") {
      val f = parse("forall y P(x)") // y occurs if in place of x
      val t = Var[Pred]("y")         // y occurs
      val x = Var[Pred]("x")

      substitutor.isFreeFor(f, t, x) shouldBe false
    }

    it("should say yes for 'forall y P(x)' -> 'forall y P(z)'") {
      val f = parse("forall y P(x)") 
      val t = Var[Pred]("z")
      val x = Var[Pred]("x")

      substitutor.isFreeFor(f, t, x) shouldBe true
    }

    it("should say no if it only happens below") {
      val f = parse("forall a forall y P(x)") 
      val t = Var[Pred]("y")
      val x = Var[Pred]("x")

      substitutor.isFreeFor(f, t, x) shouldBe false
    }
  }
}
