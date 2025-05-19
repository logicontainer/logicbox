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
        Ident('P'), LeftParen(),
          Ident('x'),
        RightParen(),
      )

      PredLogicParser()(ts) shouldBe Predicate('P', List(Var('x')))
    }

    it("should parse predicate of function vars") {
      val ts = List(
        Ident('P'), LeftParen(),
          Ident('f'), LeftParen(),
            Ident('y'),
          RightParen(), Comma(),
          Ident('g'), LeftParen(),
            Ident('x'),
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
        Ident('P'), LeftParen(),
          Ident('x'), Comma(), Ident('y'),
        RightParen(),
      )

      PredLogicParser()(ts) shouldBe Predicate('P', List(Var('x'), Var('y')))
    }

    it("should parse predicate of complicated terms") {
      val ts = List(
        Ident('P'), LeftParen(),
          Ident('f'), LeftParen(), 
            Ident('x'), Comma(),
            Ident('y'), Comma(),
            Ident('g'), LeftParen(),
              Ident('g'), LeftParen(), Ident('x'), RightParen(),
            RightParen(),
          RightParen(), Comma(),
          Ident('g'), LeftParen(), 
            Ident('x'),
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
        PredLogicToken.Exists(), Ident('x'), Ident('P'), LeftParen(), Ident('x'), RightParen(),
      )
      val ts2 = List(
        PredLogicToken.ForAll(), Ident('x'), Ident('P'), LeftParen(), Ident('x'), RightParen(),
      )
      val ts3 = List(
        PredLogicToken.ForAll(), Ident('x'), PredLogicToken.Exists(), Ident('y'), PredLogicToken.ForAll(), Ident('z'), 
          Ident('P'), LeftParen(),
            Ident('x'), Comma(), Ident('y'), Comma(), Ident('z'),
          RightParen()
      )

      PredLogicParser()(ts1) shouldBe 
        PredLogicFormula.Exists(Var('x'), Predicate('P', List(Var('x'))))
      PredLogicParser()(ts2) shouldBe 
        PredLogicFormula.ForAll(Var('x'), Predicate('P', List(Var('x'))))

      PredLogicParser()(ts3) shouldBe
        PredLogicFormula.ForAll(Var('x'), 
          PredLogicFormula.Exists(Var('y'), 
            PredLogicFormula.ForAll(Var('z'),
              Predicate('P', List(
                Var('x'),
                Var('y'),
                Var('z')
              ))
            )
          )
        )
    }
    
    it("should parse equality of terms") {
      val ts1 = List(Ident('x'), PredLogicToken.Equals(), Ident('y'))
      val ts2 = List(
        Ident('f'), LeftParen(), Ident('x'), Comma(), Ident('y'), RightParen(), 
        PredLogicToken.Equals(), 
        Ident('g'), LeftParen(), Ident('y'), RightParen(),
      )

      PredLogicParser()(ts1) shouldBe PredLogicFormula.Equals(
        PredLogicTerm.Var('x'),
        PredLogicTerm.Var('y'),
      )

      PredLogicParser()(ts2) shouldBe
        PredLogicFormula.Equals(
          PredLogicTerm.FunAppl('f', List(PredLogicTerm.Var('x'), PredLogicTerm.Var('y'))),
          PredLogicTerm.FunAppl('g', List(PredLogicTerm.Var('y'))),
        )
    }
  }
}
