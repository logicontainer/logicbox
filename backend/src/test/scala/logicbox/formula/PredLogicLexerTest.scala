package logicbox.formula 

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

class PredLogicLexerTest extends AnyFunSpec {
  import Token._
  describe("apply"){
    it("should lex simple formulas") {
      Lexer("false") shouldBe List(Contradiction())
      Lexer("true") shouldBe List(Tautology())
      Lexer("x") shouldBe List(Ident("x"))
      Lexer("f") shouldBe List(Ident("f"))
      Lexer("x") shouldBe List(Ident("x"))
      Lexer("P(g(x, y, z), y, z)") shouldBe List(
        Ident("P"), LeftParen(), 
          Ident("g"), LeftParen(), 
            Ident("x"), Comma(), Ident("y"), Comma(), Ident("z"),
          RightParen(), Comma(), Ident("y"), Comma(), Ident("z"),
        RightParen()
      )
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
      Lexer("\\//\\") shouldBe List(Or(), And())
      assertThrows[RuntimeException] {
        Lexer("P_{}")
      }
      assertThrows[RuntimeException] {
        Lexer("x_12")
      }
    }
  }
}
