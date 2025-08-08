package logicbox.formula 

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

class PredLogicParserTest extends AnyFunSpec {
  import Formula._
  import Term._
  import Token._

  describe("apply"){
    it("should parse predicate of variable") {
      val ts = List(
        Ident("P"), LeftParen(),
          Ident("x"),
        RightParen(),
      )

      Parser.parse(ts, Parser.predLogicFormula) shouldBe Predicate("P", List(Var("x")))
    }
    
    it("should parse the empty predicate") {
      val ts = List(
        Ident("P")
      )

      Parser.parse(ts, Parser.predLogicFormula) shouldBe Predicate("P", List())

      val ts2 = List(
        Ident("P"), Token.Implies(), Ident("Q")
      )

      Parser.parse(ts2, Parser.predLogicFormula) shouldBe Formula.Implies(Predicate("P", List()), Predicate("Q", List()))
    }

    it("should parse function") {
      val ts = List(Ident("f"), LeftParen(), Ident("x"), RightParen())
      Parser.parse(ts, Parser.predLogicTerm) shouldBe Term.FunAppl("f", List(Var("x")))
    }

    it("should parse functions with multiple arguments") {
      val ts = List(Ident("f"), LeftParen(), Ident("x"), Comma(), Ident("y"), RightParen())
      Parser.parse(ts, Parser.predLogicTerm) shouldBe Term.FunAppl("f", List(Var("x"), Var("y")))
    }

    it("should parse predicate of function vars") {
      val ts = List(
        Ident("P"), LeftParen(),
          Ident("f"), LeftParen(),
            Ident("y"),
          RightParen(), Comma(),
          Ident("g"), LeftParen(),
            Ident("x"),
          RightParen(),
        RightParen(),
      )

      Parser.parse(ts, Parser.predLogicFormula) shouldBe Predicate("P", List(
        FunAppl("f", List(Var("y"))),
        FunAppl("g", List(Var("x"))),
      ))
    }

    it("should parse predicate of multiple vars") {
      val ts = List(
        Ident("P"), LeftParen(),
          Ident("x"), Comma(), Ident("y"), Comma(), Ident("z"),
        RightParen(),
      )

      Parser.parse(ts, Parser.predLogicFormula) shouldBe Predicate("P", List(Var("x"), Var("y"), Var("z")))
    }

    it("should parse predicate of complicated terms") {
      val ts = List(
        Ident("P"), LeftParen(),
          Ident("f"), LeftParen(), 
            Ident("x"), Comma(),
            Ident("y"), Comma(),
            Ident("g"), LeftParen(),
              Ident("g"), LeftParen(), Ident("x"), RightParen(),
            RightParen(),
          RightParen(), Comma(),
          Ident("g"), LeftParen(), 
            Ident("x"),
          RightParen(),
        RightParen(),
      )

      Parser.parse(ts, Parser.predLogicFormula) shouldBe Predicate("P", List(
        FunAppl("f", List(
          Var("x"), 
          Var("y"), 
          FunAppl("g", List(FunAppl("g", List(Var("x"))))),
        )),
        FunAppl("g", List(Var("x")))
      ))
    }

    it("should parse exists and forall") {
      val ts1 = List(
        Token.Exists(), Ident("x"), Ident("P"), LeftParen(), Ident("x"), RightParen(),
      )
      val ts2 = List(
        Token.ForAll(), Ident("x"), Ident("P"), LeftParen(), Ident("x"), RightParen(),
      )
      val ts3 = List(
        Token.ForAll(), Ident("x"), Token.Exists(), Ident("y"), Token.ForAll(), Ident("z"), 
          Ident("P"), LeftParen(),
            Ident("x"), Comma(), Ident("y"), Comma(), Ident("z"),
          RightParen()
      )

      Parser.parse(ts1, Parser.predLogicFormula) shouldBe 
        Formula.Exists(Var("x"), Predicate("P", List(Var("x"))))
      Parser.parse(ts2, Parser.predLogicFormula) shouldBe 
        Formula.ForAll(Var("x"), Predicate("P", List(Var("x"))))

      Parser.parse(ts3, Parser.predLogicFormula) shouldBe
        Formula.ForAll(Var("x"), 
          Formula.Exists(Var("y"), 
            Formula.ForAll(Var("z"),
              Predicate("P", List(
                Var("x"),
                Var("y"),
                Var("z")
              ))
            )
          )
        )
    }

    it("should correctly parenthesize things") {
      val ts = List(
        Ident("P"), Token.Implies(), Ident("L"), Token.Or(), Ident("Q"), Token.And(), Token.Not(), 
        LeftParen(), 
          Ident("R"), Token.Implies(), Ident("Q"), Token.Implies(), Ident("S"), 
        RightParen()
      )

      Parser.parse(ts, Parser.predLogicFormula) shouldBe Formula.Implies(
        Predicate("P", Nil), 
        Formula.And(
          Formula.Or(Predicate("L", Nil), Predicate("Q", Nil)),
          Formula.Not(
            Formula.Implies(
              Predicate("R", Nil),
              Formula.Implies(Predicate("Q", Nil), Predicate("S", Nil))
            )
          )
        )
      )
    }

    it("should parse contr/taut") {
      val ts1 = List(Token.Contradiction())
      val ts2 = List(Token.Tautology())
      Parser.parse(ts1, Parser.predLogicFormula) shouldBe Formula.Contradiction()
      Parser.parse(ts2, Parser.predLogicFormula) shouldBe Formula.Tautology()
    }
    
    it("should parse equality of terms") {
      val ts1 = List(Ident("x"), Token.Equals(), Ident("y"))
      val ts2 = List(
        Ident("f"), LeftParen(), Ident("x"), Comma(), Ident("y"), RightParen(), 
        Token.Equals(), 
        Ident("g"), LeftParen(), Ident("y"), RightParen(),
      )

      Parser.parse(ts1, Parser.predLogicFormula) shouldBe Formula.Equals(
        Term.Var("x"),
        Term.Var("y"),
      )

      Parser.parse(ts2, Parser.predLogicFormula) shouldBe
        Formula.Equals(
          Term.FunAppl("f", List(Term.Var("x"), Term.Var("y"))),
          Term.FunAppl("g", List(Term.Var("y"))),
        )
    }
  }
}
