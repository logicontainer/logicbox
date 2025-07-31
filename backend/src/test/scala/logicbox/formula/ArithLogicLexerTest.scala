package logicbox.formula 

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors
import logicbox.formula.ArithLogicToken._

class ArithLogicLexerTest extends AnyFunSpec {
  describe("apply"){
    it("should lex simple formulas") {
      ArithLogicLexer()("false") shouldBe List(Contradiction())
      ArithLogicLexer()("true") shouldBe List(Tautology())
      ArithLogicLexer()("x") shouldBe List(Ident("x"))
      ArithLogicLexer()("f") shouldBe List(Ident("f"))
      ArithLogicLexer()("x") shouldBe List(Ident("x"))
      ArithLogicLexer()("forall x exists y(y = v)") shouldBe List(
        ForAll(), Ident("x"), Exists(), Ident("y"), LeftParen(), 
        Ident("y"), Equals(), Ident("v"), RightParen()
      )
      ArithLogicLexer()("P_{43} f_{41} x_0") shouldBe List(
        Ident("P_{43}"), Ident("f_{41}"), Ident("x_0")
      )
      ArithLogicLexer()("P_{43} f_{41} x_0") shouldBe List(
        Ident("P_{43}"), Ident("f_{41}"), Ident("x_0")
      )
      assertThrows[RuntimeException] {
        ArithLogicLexer()("P_{}")
      }
      assertThrows[RuntimeException] {
        ArithLogicLexer()("x_12")
      }
    }
    
    it("should lex plus mult") {
      ArithLogicLexer()("a + b_{12} * c_4") shouldBe List(
        Ident("a"), Plus(), Ident("b_{12}"), Mult(), Ident("c_4")
      )
    }

    it("should lex 0, 1") {
      ArithLogicLexer()(" 0 1 0 1  1") shouldBe List(
        Zero(), One(), Zero(), One(), One()
      )
    }
  }
}
