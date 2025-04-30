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
      PredLogicLexer()("x") shouldBe List(LowerIdent('x'))
      PredLogicLexer()("f") shouldBe List(LowerIdent('f'))
      PredLogicLexer()("x") shouldBe List(LowerIdent('x'))
      PredLogicLexer()("P(g(x, y, z), y, z)") shouldBe List(
        UpperIdent('P'), LeftParen(), 
          LowerIdent('g'), LeftParen(), 
            LowerIdent('x'), Comma(), LowerIdent('y'), Comma(), LowerIdent('z'),
          RightParen(), Comma(), LowerIdent('y'), Comma(), LowerIdent('z'),
        RightParen()
      )
      PredLogicLexer()("forall x exists y(y = v)") shouldBe List(
        ForAll(), LowerIdent('x'), Exists(), LowerIdent('y'), LeftParen(), 
        LowerIdent('y'), Equals(), LowerIdent('v'), RightParen()
      )
    }
  }
}
