package logicbox.formula 

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

class ArithLogicParserTest extends AnyFunSpec {
  import ArithLogicToken._
  import Term.Var
  describe("apply"){
    it("should parse exists and forall") {
      val ts1 = List(
        ArithLogicToken.Exists(), Ident("x"), Ident("P"), LeftParen(), Ident("x"), RightParen(),
      )
      val ts2 = List(
        ArithLogicToken.ForAll(), Ident("x"), Ident("x"), ArithLogicToken.Equals(), Ident("x")
      )
      val ts3 = List(
        ArithLogicToken.ForAll(), Ident("x"), ArithLogicToken.Exists(), Ident("y"), ArithLogicToken.ForAll(), Ident("z"), 
          Ident("x"), ArithLogicToken.Equals(), Ident("y")
      )

      ArithLogicParser().parseFormula(ts3) shouldBe
        Formula.ForAll(Var("x"), 
          Formula.Exists(Var("y"), 
            Formula.ForAll(Var("z"),
              Formula.Equals(Var("x"), Var("y"))
            )
          )
        )
    }
    
    it("should parse equality of terms") {
      val ts = List(Ident("x"), ArithLogicToken.Equals(), Ident("y"))

      ArithLogicParser().parseFormula(ts) shouldBe Formula.Equals(
        Term.Var("x"),
        Term.Var("y"),
      )
    }

    it("should parse zero") {
      val ts = List(ArithLogicToken.Zero(), ArithLogicToken.Equals(), ArithLogicToken.Zero())

      ArithLogicParser().parseFormula(ts) shouldBe Formula.Equals(
        Term.Zero(), Term.Zero()
      )
    }

    it("should parse one") {
      val ts = List(ArithLogicToken.One(), ArithLogicToken.Equals(), ArithLogicToken.One())

      ArithLogicParser().parseFormula(ts) shouldBe Formula.Equals(
        Term.One(), Term.One()
      )
    }

    it("should parse *") {
      val ts1 = List(ArithLogicToken.One(), ArithLogicToken.Mult(), ArithLogicToken.One(), ArithLogicToken.Equals(), ArithLogicToken.One())

      ArithLogicParser().parseFormula(ts1) shouldBe Formula.Equals(
        Term.Mult(
          Term.One(), 
          Term.One(), 
        ),
        Term.One(), 
      )

      // left ass.
      val ts2 = List(
        ArithLogicToken.One(), ArithLogicToken.Mult(), ArithLogicToken.One(), ArithLogicToken.Mult(), ArithLogicToken.One(), 
        ArithLogicToken.Equals(), 
        ArithLogicToken.One()
      )

      ArithLogicParser().parseFormula(ts2) shouldBe Formula.Equals(
        Term.Mult(
          Term.Mult(
            Term.One(), 
            Term.One(), 
          ),
          Term.One(), 
        ),
        Term.One(), 
      )
    }

    it("should parse +") {
      val ts1 = List(ArithLogicToken.One(), ArithLogicToken.Plus(), ArithLogicToken.One(), ArithLogicToken.Equals(), ArithLogicToken.One())

      ArithLogicParser().parseFormula(ts1) shouldBe Formula.Equals(
        Term.Plus(
          Term.One(), 
          Term.One(), 
        ),
        Term.One(), 
      )

      // left ass.
      val ts2 = List(
        ArithLogicToken.One(), ArithLogicToken.Plus(), ArithLogicToken.One(), ArithLogicToken.Plus(), ArithLogicToken.One(), 
        ArithLogicToken.Equals(), 
        ArithLogicToken.One()
      )

      ArithLogicParser().parseFormula(ts2) shouldBe Formula.Equals(
        Term.Plus(
          Term.Plus(
            Term.One(), 
            Term.One(), 
          ),
          Term.One(), 
        ),
        Term.One(), 
      )
    }

    it("should see x * y + x * y + y + y = 0 as ((((x * y) + (x * y)) + y) + y) = 0") {
      val ts = {
        import ArithLogicToken.Plus => P
        import ArithLogicToken.Mult => M
        List(
          Ident("x"), M(), Ident("y"), P(),
          Ident("x"), M(), Ident("y"), P(),
          Ident("y"), P(), Ident("y"),
          
          ArithLogicToken.Equals(), 
          ArithLogicToken.Zero()
        )
      }

      import Term.Plus => P
      import Term.Mult => M
      
      ArithLogicParser().parseFormula(ts) shouldBe Formula.Equals(
        P(
          P(
            P(
              M(Var("x"), Var("y")),
              M(Var("x"), Var("y")),
            ),
            Var("y")
          ),
          Var("y")
        ),
        Term.Zero()
      )
    }

    it("should allow parens on +, *") {
      val ts = {
        import ArithLogicToken.Plus => P
        import ArithLogicToken.Mult => M
        List(
          Ident("x"), M(), Ident("y"), P(),
          Ident("x"), M(), 
            LeftParen(), Ident("y"), P(), Ident("y"), RightParen(),
          P(), Ident("y"),
          
          ArithLogicToken.Equals(), 
          ArithLogicToken.Zero()
        )
      }
      
      import Term.Plus => P
      import Term.Mult => M
      ArithLogicParser().parseFormula(ts) shouldBe Formula.Equals(
        P(
          P(
            M(Var("x"), Var("y")),
            M(
              Var("x"),
              P(Var("y"), Var("y"))
            )
          ),
          Var("y")
        ),
        Term.Zero()
      )
    }
  }
}
