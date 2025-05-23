package logicbox.formula 

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

class PredLogicLexerTest extends AnyFunSpec {
  import PredLogicToken._
  describe("apply"){
    it("should lex simple formulas") {
      PredLogicLexer()("false") shouldBe List(Contradiction())
      PredLogicLexer()("true") shouldBe List(Tautology())
      PredLogicLexer()("x") shouldBe List(Ident("x"))
      PredLogicLexer()("f") shouldBe List(Ident("f"))
      PredLogicLexer()("x") shouldBe List(Ident("x"))
      PredLogicLexer()("P(g(x, y, z), y, z)") shouldBe List(
        Ident("P"), LeftParen(), 
          Ident("g"), LeftParen(), 
            Ident("x"), Comma(), Ident("y"), Comma(), Ident("z"),
          RightParen(), Comma(), Ident("y"), Comma(), Ident("z"),
        RightParen()
      )
      PredLogicLexer()("forall x exists y(y = v)") shouldBe List(
        ForAll(), Ident("x"), Exists(), Ident("y"), LeftParen(), 
        Ident("y"), Equals(), Ident("v"), RightParen()
      )
      PredLogicLexer()("P_{43} f_{41} x_0") shouldBe List(
        Ident("P_{43}"), Ident("f_{41}"), Ident("x_0")
      )
      PredLogicLexer()("P_{43} f_{41} x_0") shouldBe List(
        Ident("P_{43}"), Ident("f_{41}"), Ident("x_0")
      )
      assertThrows[RuntimeException] {
        PredLogicLexer()("P_{}")
      }
      assertThrows[RuntimeException] {
        PredLogicLexer()("x_12")
      }
    }
  }
}
