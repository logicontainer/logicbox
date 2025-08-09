package logicbox.formula 

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

class ParserTest extends AnyFunSpec {
  import Formula._, Term._, Token._

  describe("paresr prop logic formulas") {
    def parse(ts: List[Token]) = Parser.parse(ts, Parser.propLogicFormula)

    it("should parse atoms") {
      val ts = List(Ident("p"))
      parse(ts) shouldBe Atom('p')
    }

    it("should parse connectives") {
      val ts1 = List(Ident("p"), Token.And(), Ident("q"))
      parse(ts1) shouldBe Formula.And(Atom('p'), Atom('q'))
      val ts2 = List(Ident("p"), Token.Or(), Ident("q"), Token.And(), Ident("r"))
      parse(ts2) shouldBe Formula.And(Formula.Or(Atom('p'), Atom('q')), Atom('r'))
      val ts3 = List(Ident("p"), Token.Implies(), Ident("q"), Token.Implies(), Token.Not(), Ident("r"))
      parse(ts3) shouldBe Formula.Implies(Atom('p'), Formula.Implies(Atom('q'), Formula.Not(Atom('r'))))
    }

    it("should parse contr/taut") {
      val ts1 = List(Token.Contradiction())
      val ts2 = List(Token.Tautology())
      parse(ts1) shouldBe Formula.Contradiction()
      parse(ts2) shouldBe Formula.Tautology()
    }
  }

  describe("parse pred logic formulas") {
    def parse(ts: List[Token]) = Parser.parse(ts, Parser.predLogicFormula)

    it("should parse predicate of variable") {
      val ts = List(
        Ident("P"), LeftParen(),
          Ident("x"),
        RightParen(),
      )

      parse(ts) shouldBe Predicate("P", List(Var("x")))
    }
    
    it("should parse the empty predicate") {
      val ts = List(
        Ident("P")
      )

      parse(ts) shouldBe Predicate("P", List())

      val ts2 = List(
        Ident("P"), Token.Implies(), Ident("Q")
      )

      parse(ts2) shouldBe Formula.Implies(Predicate("P", List()), Predicate("Q", List()))
    }

    it("should parse function") {
      val ts = List(Ident("f"), LeftParen(), Ident("x"), RightParen())
      Parser.parse(ts, Parser.predLogicTerm) shouldBe Term.FunAppl("f", List(Var("x")))
    }

    it("should parse functions with multiple arguments") {
      val ts = List(Ident("f"), LeftParen(), Ident("x"), Comma(), Ident("y"), RightParen())
      Parser.parse(ts, Parser.predLogicTerm) shouldBe Term.FunAppl("f", List(Var("x"), Var("y")))
    }

    it("should have quantifiers bind tighter than connectives") {
      val ts = List(Token.ForAll(), Ident("y"), Token.Contradiction(), Token.Or(), Token.Exists(), Ident("y"), Token.Contradiction())
      Parser.parse(ts, Parser.predLogicFormula) shouldBe Formula.Or(
        Formula.ForAll(Var("y"), Formula.Contradiction()),
        Formula.Exists(Var("y"), Formula.Contradiction())
      )
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

      parse(ts) shouldBe Predicate("P", List(
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

      parse(ts) shouldBe Predicate("P", List(Var("x"), Var("y"), Var("z")))
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

      parse(ts) shouldBe Predicate("P", List(
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

      parse(ts1) shouldBe 
        Formula.Exists(Var("x"), Predicate("P", List(Var("x"))))
      parse(ts2) shouldBe 
        Formula.ForAll(Var("x"), Predicate("P", List(Var("x"))))

      parse(ts3) shouldBe
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

      parse(ts) shouldBe Formula.Implies(
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
      parse(ts1) shouldBe Formula.Contradiction()
      parse(ts2) shouldBe Formula.Tautology()
    }
    
    it("should parse equality of terms") {
      val ts1 = List(Ident("x"), Token.Equals(), Ident("y"))
      val ts2 = List(
        Ident("f"), LeftParen(), Ident("x"), Comma(), Ident("y"), RightParen(), 
        Token.Equals(), 
        Ident("g"), LeftParen(), Ident("y"), RightParen(),
      )

      parse(ts1) shouldBe Formula.Equals(
        Term.Var("x"),
        Term.Var("y"),
      )

      parse(ts2) shouldBe
        Formula.Equals(
          Term.FunAppl("f", List(Term.Var("x"), Term.Var("y"))),
          Term.FunAppl("g", List(Term.Var("y"))),
        )
    }
  }

  describe("parse arithmetic formulas"){
    def parse(ts: List[Token]) = Parser.parse(ts, Parser.arithLogicFormula)

    it("should parse exists and forall") {
      val ts1 = List(
        Token.Exists(), Ident("x"), Ident("P"), LeftParen(), Ident("x"), RightParen(),
      )
      val ts2 = List(
        Token.ForAll(), Ident("x"), Ident("x"), Token.Equals(), Ident("x")
      )
      val ts3 = List(
        Token.ForAll(), Ident("x"), Token.Exists(), Ident("y"), Token.ForAll(), Ident("z"), 
          Ident("x"), Token.Equals(), Ident("y")
      )

      parse(ts3) shouldBe
        Formula.ForAll(Var("x"), 
          Formula.Exists(Var("y"), 
            Formula.ForAll(Var("z"),
              Formula.Equals(Var("x"), Var("y"))
            )
          )
        )
    }
    
    it("should parse equality of terms") {
      val ts = List(Ident("x"), Token.Equals(), Ident("y"))

      parse(ts) shouldBe Formula.Equals(
        Term.Var("x"),
        Term.Var("y"),
      )
    }

    it("should parse zero") {
      val ts = List(Token.Zero(), Token.Equals(), Token.Zero())

      parse(ts) shouldBe Formula.Equals(
        Term.Zero(), Term.Zero()
      )
    }

    it("should parse one") {
      val ts = List(Token.One(), Token.Equals(), Token.One())

      parse(ts) shouldBe Formula.Equals(
        Term.One(), Term.One()
      )
    }

    it("should parse *") {
      val ts1 = List(Token.One(), Token.Mult(), Token.One(), Token.Equals(), Token.One())

      parse(ts1) shouldBe Formula.Equals(
        Term.Mult(
          Term.One(), 
          Term.One(), 
        ),
        Term.One(), 
      )

      // left ass.
      val ts2 = List(
        Token.One(), Token.Mult(), Token.One(), Token.Mult(), Token.One(), 
        Token.Equals(), 
        Token.One()
      )

      parse(ts2) shouldBe Formula.Equals(
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
      val ts1 = List(Token.One(), Token.Plus(), Token.One(), Token.Equals(), Token.One())

      parse(ts1) shouldBe Formula.Equals(
        Term.Plus(
          Term.One(), 
          Term.One(), 
        ),
        Term.One(), 
      )

      // left ass.
      val ts2 = List(
        Token.One(), Token.Plus(), Token.One(), Token.Plus(), Token.One(), 
        Token.Equals(), 
        Token.One()
      )

      parse(ts2) shouldBe Formula.Equals(
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
        import Token.Plus => P
        import Token.Mult => M
        List(
          Ident("x"), M(), Ident("y"), P(),
          Ident("x"), M(), Ident("y"), P(),
          Ident("y"), P(), Ident("y"),
          
          Token.Equals(), 
          Token.Zero()
        )
      }

      import Term.Plus => P
      import Term.Mult => M
      
      parse(ts) shouldBe Formula.Equals(
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
        import Token.Plus => P
        import Token.Mult => M
        List(
          Ident("x"), M(), Ident("y"), P(),
          Ident("x"), M(), 
            LeftParen(), Ident("y"), P(), Ident("y"), RightParen(),
          P(), Ident("y"),
          
          Token.Equals(), 
          Token.Zero()
        )
      }
      
      import Term.Plus => P
      import Term.Mult => M
      parse(ts) shouldBe Formula.Equals(
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
