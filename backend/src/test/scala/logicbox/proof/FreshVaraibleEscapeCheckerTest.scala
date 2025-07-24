package logicbox.proof

import logicbox.framework._
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import org.scalatest.funspec.AnyFunSpec
import logicbox.framework.Proof.Step

import logicbox.framework.Error
import logicbox.framework.Error._
import logicbox.ProofStubs.StubFormula
import logicbox.rule.FreshVarBoxInfo

class FreshVaraibleEscapeCheckerTest extends AnyFunSpec {
  case class StubLine(formula: String) extends Proof.Line[String, Unit, String] {
    override def rule: Unit = ()
    override def refs: Seq[String] = Nil
  }

  case class StubBox(info: FreshVarBoxInfo[String], steps: Seq[String] = Nil) extends Proof.Box[FreshVarBoxInfo[String], String] {
  }

  case class StubProof(rootSteps: Seq[String], map: Map[String, Proof.Step[String, Unit, FreshVarBoxInfo[String], String]]) extends Proof[String, Unit, FreshVarBoxInfo[String], String] {
    override def getStep(id: String): Option[Step[String, Unit, FreshVarBoxInfo[String], String]] = map.get(id)
  }

  describe("check") {
    // stub: variable `v` occurs in formula `f` iff they are equal
    def stubVOccursInF(v: String, f: String): Boolean = v == f

    val checker = FreshVariableEscapeChecker(stubVOccursInF)

    it("should report when fresh variable occurs in formula outside box where it is defined") {
      val pf = StubProof(
        rootSteps = Seq("line", "box"),
        map = Map(
          "line" -> StubLine(formula = "equal"),
          "box" -> StubBox(info = FreshVarBoxInfo(Some("equal")))
        )
      )

      checker.check(pf) shouldBe List(
        ("line", FreshVarEscaped("box"))
      )
    }

    it("should not report when no occurance outside") {
      val pf = StubProof(
        rootSteps = Seq("l", "b"),
        map = Map(
          "l" -> StubLine(formula = "not equal"),
          "b" -> StubBox(info = FreshVarBoxInfo(Some("equal not")))
        )
      )

      checker.check(pf) shouldBe Nil
    }

    it("should report occurance of fresh var defined in box within another box") {
      val pf = StubProof(
        rootSteps = Seq("out", "line"),
        map = Map(
          "in" -> StubBox(FreshVarBoxInfo(Some("equal"))),
          "out" -> StubBox(FreshVarBoxInfo(None), Seq("in")),
          "line" -> StubLine("equal")
        )
      )

      checker.check(pf) shouldBe List(
        ("line", FreshVarEscaped("in"))
      )
    }

    it("should report when fresh var is used inside box which is defined within subbox") {
      val pf = StubProof(
        rootSteps = Seq("outer"),
        map = Map(
          "outer" -> StubBox(FreshVarBoxInfo(None), Seq("inner", "line")),
          "inner" -> StubBox(FreshVarBoxInfo(Some("inner_var"))),
          "line" -> StubLine("inner_var")
        )
      )

      checker.check(pf) shouldBe List(("line", FreshVarEscaped("inner")))
    }

    it("should not report when fresh var is used within box inwhich it is defined") {
      val pf = StubProof(
        rootSteps = Seq("box"),
        map = Map(
          "box" -> StubBox(FreshVarBoxInfo(Some("var")), Seq("line")),
          "line" -> StubLine("var") // fine
        )
      )

      checker.check(pf) shouldBe Nil
    }
  }
}
