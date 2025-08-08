package logicbox.formula 

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

class LexerTest extends AnyFunSpec {
  describe("apply"){
    import Token._
    it("should lex simple formulas") {
      Lexer("false") shouldBe List(Contradiction())
      Lexer("true") shouldBe List(Tautology())
      Lexer("x") shouldBe List(Ident("x"))
      Lexer("f") shouldBe List(Ident("f"))
      Lexer("x") shouldBe List(Ident("x"))
      Lexer("forall x exists y(y = v)") shouldBe List(
        ForAll(), Ident("x"), Exists(), Ident("y"), LeftParen(), 
        Ident("y"), Equals(), Ident("v"), RightParen()
      )
      Lexer("P_{43} f_{41} x_0") shouldBe List(
        Ident("P_{43}"), Ident("f_{41}"), Ident("x_0")
      )
      Lexer("P_{43} f_{41} x_0") shouldBe List(
        Ident("P_{43}"), Ident("f_{41}"), Ident("x_0")
      )
      assertThrows[RuntimeException] {
        Lexer("P_{}")
      }
      assertThrows[RuntimeException] {
        Lexer("x_12")
      }
    }
    
    it("should lex plus mult") {
      Lexer("a + b_{12} * c_4") shouldBe List(
        Ident("a"), Plus(), Ident("b_{12}"), Mult(), Ident("c_4")
      )
    }

    it("should lex 0, 1") {
      Lexer(" 0 1 0 1  1") shouldBe List(
        Zero(), One(), Zero(), One(), One()
      )
    }
  }
}
