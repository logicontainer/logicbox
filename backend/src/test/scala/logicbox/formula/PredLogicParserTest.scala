package logicbox.formula 

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

class PredLogicParserTest extends AnyFunSpec {
  import PredLogicToken._
  import PredLogicTerm._
  import PredLogicFormula._

  describe("apply"){
    it("should parse predicate of variable") {
      val ts = List(
        UpperIdent('P'), LeftParen(),
          LowerIdent('x'),
        RightParen(),
      )

      PredLogicParser()(ts) shouldBe Predicate('P', List(Var('x')))
    }

    it("should parse predicate of function vars") {
      val ts = List(
        UpperIdent('P'), LeftParen(),
          LowerIdent('f'), LeftParen(),
            LowerIdent('y'),
          RightParen(), Comma(),
          LowerIdent('g'), LeftParen(),
            LowerIdent('x'),
          RightParen(),
        RightParen(),
      )

      PredLogicParser()(ts) shouldBe Predicate('P', List(
        FunAppl('f', List(Var('y'))),
        FunAppl('g', List(Var('x'))),
      ))
    }

    it("should parse predicate of multiple vars") {
      val ts = List(
        UpperIdent('P'), LeftParen(),
          LowerIdent('x'), Comma(), LowerIdent('y'),
        RightParen(),
      )

      PredLogicParser()(ts) shouldBe Predicate('P', List(Var('x'), Var('y')))
    }

    it("should parse predicate of complicated terms") {
      val ts = List(
        UpperIdent('P'), LeftParen(),
          LowerIdent('f'), LeftParen(), 
            LowerIdent('x'), Comma(),
            LowerIdent('y'), Comma(),
            LowerIdent('g'), LeftParen(),
              LowerIdent('g'), LeftParen(), LowerIdent('x'), RightParen(),
            RightParen(),
          RightParen(), Comma(),
          LowerIdent('g'), LeftParen(), 
            LowerIdent('x'),
          RightParen(),
        RightParen(),
      )

      PredLogicParser()(ts) shouldBe Predicate('P', List(
        FunAppl('f', List(
          Var('x'), 
          Var('y'), 
          FunAppl('g', List(FunAppl('g', List(Var('x'))))),
        )),
        FunAppl('g', List(Var('x')))
      ))
    }

    it("should parse exists and forall") {
      val ts1 = List(
        PredLogicToken.Exists(), LowerIdent('x'), UpperIdent('P'), LeftParen(), LowerIdent('x'), RightParen(),
      )
      val ts2 = List(
        PredLogicToken.ForAll(), LowerIdent('x'), UpperIdent('P'), LeftParen(), LowerIdent('x'), RightParen(),
      )

      PredLogicParser()(ts1) shouldBe 
        PredLogicFormula.Exists(Var('x'), Predicate('P', List(Var('x'))))
      PredLogicParser()(ts2) shouldBe 
        PredLogicFormula.ForAll(Var('x'), Predicate('P', List(Var('x'))))
    }
  }
}
