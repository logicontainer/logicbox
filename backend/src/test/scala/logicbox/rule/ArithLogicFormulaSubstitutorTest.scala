package logicbox.rule

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import logicbox.formula.{ArithLogicLexer, ArithLogicParser, ArithLogicFormula}
import logicbox.formula.Term._
import logicbox.formula.Formula._
import logicbox.formula._

class ArithLogicFormulaSubstitutorTest extends AnyFunSpec {
  private type Arith = FormulaKind.Arith

  private def parse(str: String): ArithLogicFormula = {
    val lexer = ArithLogicLexer()
    val parser = ArithLogicParser()
    parser.parseFormula(lexer(str))
  }

  val substitutor = FormulaSubstitutor[Arith]()

  describe("substitute") {
    it("should leave contr./taut. alone") {
      substitutor.substitute(Contradiction(), Var[Arith]("y"), Var[Arith]("x")) shouldBe Contradiction()
      substitutor.substitute(Tautology(), Var[Arith]("y"), Var[Arith]("x")) shouldBe Tautology()
    }

    it("should work in equalities") {
      val f1 = parse("x = z")
      val exp1 = parse("y = z")
      substitutor.substitute(f1, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp1

      val f2 = parse("z = x")
      val exp2 = parse("z = y")
      substitutor.substitute(f2, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp2
    }

    it("should leave 0, 1 alone") {
      val f = parse("0 = 1")
      substitutor.substitute(f, Var[Arith]("y"), Var[Arith]("x")) shouldBe f
    }

    it("should substitute inside addition") {
      val f = parse("y + x = x + y")
      val exp = parse("y + y = y + y")
      substitutor.substitute(f, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp
    }

    it("should substitute inside multiplication") {
      val f = parse("y * x = x * y")
      val exp = parse("y * y = y * y")
      substitutor.substitute(f, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp
    }

    it("should substitute inside connectives") {
      val f =   parse("x = x and z = x or x = z -> not (z = x and x = z)")
      val exp = parse("y = y and z = y or y = z -> not (z = y and y = z)")

      substitutor.substitute(f, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp
    }

    it("should substitute free occurances within forall") {
      val f = parse("forall z x = x")
      val exp = parse("forall z y = y")

      substitutor.substitute(f, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp
    }

    it("should not substitute forall-bound occurances") {
      val f = parse("forall x x = x")
      val exp = parse("forall x x = x")

      substitutor.substitute(f, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp
    }

    it("should substitute free occurances within exists") {
      val f = parse("exists z x = x")
      val exp = parse("exists z y = y")

      substitutor.substitute(f, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp
    }

    it("should not substitute exists-bound occurances") {
      val f = parse("exists x x = x")
      val exp = parse("exists x x = x")

      substitutor.substitute(f, Var[Arith]("y"), Var[Arith]("x")) shouldBe exp
    }
  }

  describe("hasFreeOccurance") {
    it("should find occurances with equality") {
      val f = parse("a = b")
      substitutor.hasFreeOccurance(f, Var[Arith]("a")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Arith]("b")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Arith]("c")) shouldBe false
    }

    it("should find occurances within addition") {
      val f = parse("a + b = k")
      substitutor.hasFreeOccurance(f, Var[Arith]("a")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Arith]("b")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Arith]("c")) shouldBe false
    }

    it("should find occurances within multiplication") {
      val f = parse("k = a * b")
      substitutor.hasFreeOccurance(f, Var[Arith]("a")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Arith]("b")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Arith]("c")) shouldBe false
    }

    it("should find no occurances inside 0, 1") {
      val f = parse("0 = 1")
      substitutor.hasFreeOccurance(f, Var[Arith]("a")) shouldBe false
      substitutor.hasFreeOccurance(f, Var[Arith]("b")) shouldBe false
      substitutor.hasFreeOccurance(f, Var[Arith]("c")) shouldBe false
    }

    it("should find occurances within connectives and equality") {
      val f = parse("a = a and b = b or c = c implies d = d and not f = f")
      substitutor.hasFreeOccurance(f, Var[Arith]("a")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Arith]("b")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Arith]("c")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Arith]("d")) shouldBe true

      substitutor.hasFreeOccurance(f, Var[Arith]("g")) shouldBe false
      substitutor.hasFreeOccurance(f, Var[Arith]("h")) shouldBe false
      substitutor.hasFreeOccurance(f, Var[Arith]("k")) shouldBe false
      substitutor.hasFreeOccurance(f, Var[Arith]("v")) shouldBe false
    }

    it("should not find any free occurances in false/true") {
      substitutor.hasFreeOccurance(Contradiction(), Var[Arith]("a")) shouldBe false
      substitutor.hasFreeOccurance(Contradiction(), Var[Arith]("z")) shouldBe false
      substitutor.hasFreeOccurance(Tautology(), Var[Arith]("a")) shouldBe false
      substitutor.hasFreeOccurance(Tautology(), Var[Arith]("z")) shouldBe false
    }
    
    it("should find free occurances within forall") {
      val f = parse("forall x y = z")
      substitutor.hasFreeOccurance(f, Var[Arith]("y")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Arith]("z")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Arith]("a")) shouldBe false
    }

    it("should not find bound occurances within forall") {
      val f = parse("forall x y = x")
      substitutor.hasFreeOccurance(f, Var[Arith]("x")) shouldBe false
    }

    it("should find free occurances within exists") {
      val f = parse("exists x y = z")
      substitutor.hasFreeOccurance(f, Var[Arith]("y")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Arith]("z")) shouldBe true
      substitutor.hasFreeOccurance(f, Var[Arith]("a")) shouldBe false
    }

    it("should not find bound occurances within exists") {
      val f = parse("exists x y = x")
      substitutor.hasFreeOccurance(f, Var[Arith]("x")) shouldBe false
    }
  }

  describe("findReplacement") {
    it("should find replacement in equality 1") {
      val src = parse("x = x")
      val dst = parse("y = y")
      val x = Var[Arith]("x")

      val exp = Some(Right(Var[Arith]("y")))

      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacement in equality 2") {
      val src = parse("x = z")
      val dst = parse("z = z")
      val x = Var[Arith]("x")

      val exp = Some(Right(Var[Arith]("z")))

      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacement in addition") {
      val src = parse("z + x = x + k")
      val dst = parse("z + y = y + k")
      val x = Var[Arith]("x")

      val exp = Some(Right(Var[Arith]("y")))

      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacement in multiplication") {
      val src = parse("z * x = x * k")
      val dst = parse("z * y = y * k")
      val x = Var[Arith]("x")

      val exp = Some(Right(Var[Arith]("y")))

      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should allow any replacements in 0") {
      val src = parse("0 = 0")
      val dst = parse("0 = 0")
      val x = Var[Arith]("x")

      val exp = Some(Left(()))

      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should allow any replacements in 1") {
      val src = parse("1 = 1")
      val dst = parse("1 = 1")
      val x = Var[Arith]("x")

      val exp = Some(Left(()))

      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should disallow conflicting replacements") {
      val src = parse("x = x")
      val dst = parse("y = z")
      val x = Var[Arith]("x")

      val exp = None
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should work with equality that has no replacements") {
      val src = parse("a = b")
      val dst = parse("a = b")
      val x = Var[Arith]("x")

      val exp = Some(Left(()))
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should disallow structurally different formulas") {
      val src = parse("x + 0 = b")
      val dst = parse("y = b")
      val x = Var[Arith]("x")

      val exp = None
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacements when nothing happens but there are occurances of x") {
      val f = parse("x = x and y = x + 0 * 1 * x")
      val x = Var[Arith]("x")

      val exp = Some(Right(x)) // x replaced with itself
      substitutor.findReplacement(f, f, x) shouldBe exp
    }

    it("should disallow repl. when other variables are not equal") {
      val src = parse("x = b")
      val dst = parse("y = c")
      val x = Var[Arith]("x")

      val exp = None
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacements inside connectives") {
      val src = parse("x = x and x = x or x = x implies x = x and not x = x")
      val dst = parse("y = y and y = y or y = y implies y = y and not y = y")
      val x = Var[Arith]("x")

      val exp = Some(Right(Var[Arith]("y")))
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacements inside forall") {
      val src = parse("forall z x = x")
      val dst = parse("forall z y = y")
      val x = Var[Arith]("x")

      val exp = Some(Right(Var[Arith]("y")))
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should not allow replacements of forall-bound occurances") {
      val src = parse("forall x x = x")
      val dst = parse("forall x y = y")

      val x = Var[Arith]("x")

      val exp = None
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should allow no replacements inside forall bound by replacement var") {
      val src = parse("forall x z = z")
      val dst = parse("forall x z = z")

      val x = Var[Arith]("x")

      val exp = Some(Left(()))
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should find replacements inside exists") {
      val src = parse("exists z x = x")
      val dst = parse("exists z y = y")
      val x = Var[Arith]("x")

      val exp = Some(Right(Var[Arith]("y")))
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should not allow replacements of exists-bound occurances") {
      val src = parse("exists x x = x")
      val dst = parse("exists x y = y")

      val x = Var[Arith]("x")

      val exp = None
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should allow no replacements inside exists bound by replacement var") {
      val src = parse("exists x z = z")
      val dst = parse("exists x z = z")

      val x = Var[Arith]("x")

      val exp = Some(Left(()))
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should disallow replacement when forall-quantified vars are not equal") {
      val src = parse("forall x z = z")
      val dst = parse("forall y z = z")
      val x = Var[Arith]("x")

      val exp = None
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should disallow replacement when exists-quantified vars are not equal") {
      val src = parse("exists x z = z")
      val dst = parse("exists y z = z")
      val x = Var[Arith]("x")

      val exp = None
      substitutor.findReplacement(src, dst, x) shouldBe exp
    }

    it("should work with contr./taut.") {
      val src1 = parse("true")
      val dst1 = parse("true")
      val src2 = parse("false")
      val dst2 = parse("false")


      val x = Var[Arith]("x")

      val exp = Some(Left(()))
      substitutor.findReplacement(src1, dst1, x) shouldBe exp
      substitutor.findReplacement(src2, dst2, x) shouldBe exp
    }
  }

  describe("equalExcept") {
    it("should be true if no occurance of t1 and f1 = f2") {
      val f1 = parse("a = a")
      val f2 = parse("a = a")
      val t1 = Var[Arith]("b")
      val t2 = Var[Arith]("c")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }
    
    it("should be false t1 is replaced by other than t2") {
      val f1 = parse("a = a")
      val f2 = parse("a = b")
      val t1 = Var[Arith]("a")
      val t2 = Var[Arith]("c")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should allow good replacement x = a -> y = a") {
      val f1 = parse("x = a")
      val f2 = parse("y = a")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should allow good replacement a = x -> a = y") {
      val f1 = parse("a = x")
      val f2 = parse("a = y")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should allow equality with repl on lhs") {
      val f1 = parse("x = z")
      val f2 = parse("y = z")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should not allow invalid repl on lhs in equality") {
      val f1 = parse("x = z")
      val f2 = parse("z = z")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should allow equality with repl on rhs") {
      val f1 = parse("z = x")
      val f2 = parse("z = y")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should not allow invalid repl on rhs in equality") {
      val f1 = parse("z = x")
      val f2 = parse("z = z")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should allow addition with repl on lhs") {
      val f1 = parse("z = x + k")
      val f2 = parse("z = y + k")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should not allow addition with invalid repl on lhs") {
      val f1 = parse("z = x + k")
      val f2 = parse("z = z + k")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should allow addition with repl on rhs") {
      val f1 = parse("z = k + x + k")
      val f2 = parse("z = k + y + k")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should not allow addition with invalid repl on rhs") {
      val f1 = parse("z = k + x + k")
      val f2 = parse("z = k + z + k")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should allow mult with repl on lhs") {
      val f1 = parse("z = x * k")
      val f2 = parse("z = y * k")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should not allow mult with invalid repl on lhs") {
      val f1 = parse("z = x * k")
      val f2 = parse("z = z * k")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should allow mult with repl on rhs") {
      val f1 = parse("z = k * x + k")
      val f2 = parse("z = k * y + k")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should not allow mult with invalid repl on rhs") {
      val f1 = parse("z = k * x + k")
      val f2 = parse("z = k * z + k")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should be true if addition is replaced with variable") {
      val f1 = parse("z = x + x")
      val f2 = parse("z = y")
      val t1 = Plus(Var[Arith]("x"), Var[Arith]("x"))
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should say yes if it is just 0") {
      val f1 = parse("0 = 0")
      val f2 = parse("0 = 0")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should say no if 0 repl with 1") {
      val f1 = parse("0 = 0")
      val f2 = parse("0 = 1")
      val t1 = Var[Arith]("x") 
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should be false if function is invalidly replaced with variable") {
      val f1 = parse("z = x + y")
      val f2 = parse("z = y")
      val t1 = Plus(Var[Arith]("x"), Var[Arith]("x"))
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should work on complex example 1") {
      val f1 = parse("z + 0 * ((x * z + 1) + y) = x * (z + ((x * z + 1) + y) * y) + 0 * 1")
      val f2 = parse("z + 0 * (a           + y) = x * (z + (a           + y) * y) + 0 * 1")
      val t1 = Plus(Mult(Var[Arith]("x"), Var[Arith]("z")), One())
      val t2 = Var[Arith]("a")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }
    
    it("should work on complex example 2") {
      val f1 = parse("z + 0 * ((x * z + 1) + y) = x * (z + ((x * z + 1) + y) * y) + 0 * 1")
      val f2 = parse("z + 0 * ((a + 0)     + y) = x * (z + ((a + 0)     + y) * y) + 0 * 1")
      val t1 = Plus(Mult(Var[Arith]("x"), Var[Arith]("z")), One())
      val t2 = Plus(Var[Arith]("a"), Zero())

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should work on complex example 3") {
      val f1 = parse("x + 0 * (x + y) = x + y")
      val f2 = parse("x + 0 * a = a")
      val t1 = Plus(Var[Arith]("x"), Var[Arith]("y"))
      val t2 = Var[Arith]("a")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should work inside connectives 1") {
      val f1 = parse("x = x and x = x or x = x implies x = x and not x = x")
      val f2 = parse("x = y and y = x or y = x implies x = y and not y = y")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should work inside connectives 2") {
      val f1 = parse("x = x and x = x or x + x = x implies x = x and not x = x")
      val f2 = parse("x = y and y = x or x + z = x implies x = y and not y = y") // invalid
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should be true if happens on free occur. inside forall") {
      val f1 = parse("forall x (x = 0 + z)")
      val f2 = parse("forall x (x = 0 + y)")
      val t1 = Var[Arith]("z")
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should be true if nothing happens and forall-quantified variable is t1") {
      val f1 = parse("forall x (x = 0 + x)")
      val f2 = parse("forall x (x = 0 + x)")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should be false if replacement happens on forall-bound var") {
      val f1 = parse("forall x (x = 0 + x)")
      val f2 = parse("forall x (x = 0 + y)")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should not allow forall-quantified variable to change") {
      val f1 = parse("forall x (x = 0 + x)")
      val f2 = parse("forall y (x = 0 + y)")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should be true if happens on free occur. inside exists") {
      val f1 = parse("exists x (x = 0 + z)")
      val f2 = parse("exists x (x = 0 + y)")
      val t1 = Var[Arith]("z")
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should be true if nothing happens and exists-quantified variable is t1") {
      val f1 = parse("exists x (x = 0 + x)")
      val f2 = parse("exists x (x = 0 + x)")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }

    it("should be false if replacement happens on exists-bound var") {
      val f1 = parse("exists x (x = 0 + x)")
      val f2 = parse("exists x (x = 0 + y)")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should not allow exists-quantified variable to change") {
      val f1 = parse("exists x (x = 0 + x)")
      val f2 = parse("exists y (x = 0 + y)")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe false
    }

    it("should be happy with simple things") {
      val f1 = parse("false and not true -> false or false and not true")
      val f2 = parse("false and not true -> false or false and not true")
      val t1 = Var[Arith]("x")
      val t2 = Var[Arith]("y")

      substitutor.equalExcept(f1, f2, t1, t2) shouldBe true
    }
  }
}
