package logicbox.rule

import logicbox.framework.{Reference, Violation}
import logicbox.framework.Violation._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import logicbox.formula.{PredLogicLexer, PredLogicParser, PredLogicFormula}
import logicbox.rule.PredLogicRuleChecker
import logicbox.formula.PredLogicTerm
import logicbox.rule.PredLogicRule.ForAllElim

class PredLogicRuleCheckerTest extends AnyFunSpec {
  private val lexer = PredLogicLexer()
  private val parser = PredLogicParser()

  private def parse(str: String): PredLogicFormula = parser(lexer(str))

  private case class Line(formula: PredLogicFormula, rule: PredLogicRule, refs: List[Reference[PredLogicFormula, PredLogicBoxInfo]])
    extends Reference.Line[PredLogicFormula]

  private case class Box(fst: PredLogicFormula, lst: PredLogicFormula) extends Reference.Box[PredLogicFormula, PredLogicBoxInfo] {
    override def info = PredLogicBoxInfo()
    override def assumption = fst
    override def conclusion = lst
  }

  private def refLine(str: String): Reference[PredLogicFormula, PredLogicBoxInfo] = new Reference.Line[PredLogicFormula] {
    def formula = parse(str)
  }

  private def refBox(ass: String, concl: String): Reference.Box[PredLogicFormula, PredLogicBoxInfo] =
    Box(parse(ass), parse(concl))

  private val checker = PredLogicRuleChecker[PredLogicFormula, PredLogicTerm, PredLogicTerm.Var]()

  describe("ForAllElim") {
    it("should reject if refs are empty") {
      val f = parse("forall x P(x)")
      checker.check(ForAllElim(), f, Nil) should matchPattern {
        case List(WrongNumberOfReferences(1, 0, _)) =>
      }
    }

    it("should reject if ref is not forall") {
      val f = parse("P(x)")
      checker.check(ForAllElim(), f, List(refLine("P(x)"))) should matchPattern {
        case List(ReferenceDoesntMatchRule(0, _)) =>
      }
    }
  }
}
