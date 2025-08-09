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

class FreshVariableDuplicateCheckerTest extends AnyFunSpec {
  case class StubLine(formula: String) extends Proof.Line[String, Unit, String] {
    override def rule: Unit = ()
    override def refs: Seq[String] = Nil
  }

  case class StubBox(info: FreshVarBoxInfo[String], steps: Seq[String] = Nil) extends Proof.Box[FreshVarBoxInfo[String], String] {
  }

  case class StubProof(rootSteps: Seq[String], map: Map[String, Proof.Step[String, Unit, FreshVarBoxInfo[String], String]]) extends Proof[String, Unit, FreshVarBoxInfo[String], String] {
    override def getStep(id: String): Option[Step[String, Unit, FreshVarBoxInfo[String], String]] = 
      map.get(id)
  }

  def fresh(s: String): FreshVarBoxInfo[String] = FreshVarBoxInfo(Some(s))
  val nofresh: FreshVarBoxInfo[String] = FreshVarBoxInfo(None)

  describe("check") {
    val checker: ProofChecker[Any, Any, FreshVarBoxInfo[String], String] = FreshVariableDuplicateChecker()
    it("should not fail on empty proof") {
      val pf = StubProof(Seq(), Map())
      checker.check(pf) shouldBe Nil
    }

    it("should fail for a box within another box with same fresh var") {
      val pf = StubProof(Seq("outer"), Map(
        "outer" -> StubBox(fresh("x_0"), Seq("inner")),
        "inner" -> StubBox(fresh("x_0"), Seq())
      ))

      checker.check(pf) shouldBe List(
        ("inner", RedefinitionOfFreshVar("outer"))
      )
    }

    it("should not fail for box with no fresh var") {
      val pf = StubProof(Seq("box"), Map(
        "box" -> StubBox(FreshVarBoxInfo(None), Seq())
      ))

      checker.check(pf) shouldBe Nil
    }

    it("should fail if happens with a grandparent") {
      val pf = StubProof(Seq("b1"), Map(
        "b1" -> StubBox(fresh("a"), Seq("b2")),
        "b2" -> StubBox(nofresh, Seq("b3")),
        "b3" -> StubBox(fresh("a"), Seq())
      ))

      checker.check(pf) shouldBe List(
        ("b3", RedefinitionOfFreshVar("b1"))
      )
    }

    it("should fail if happens with a grandparent were parent has fresh var") {
      val pf = StubProof(Seq("b1"), Map(
        "b1" -> StubBox(fresh("a"), Seq("b2")),
        "b2" -> StubBox(fresh("b"), Seq("b3")),
        "b3" -> StubBox(fresh("a"), Seq())
      ))

      checker.check(pf) shouldBe List(
        ("b3", RedefinitionOfFreshVar("b1"))
      )
    }

    it("should react when not first step of box") {
      val pf = StubProof(Seq("outer"), Map(
        "outer" -> StubBox(fresh("x_0"), Seq("line", "inner")),
        "line" -> StubLine("bruh"),
        "inner" -> StubBox(fresh("x_0"), Seq())
      ))

      checker.check(pf) shouldBe List(
        ("inner", RedefinitionOfFreshVar("outer"))
      )
    }

    it("should trigger multiple errors with same original box") {
      val pf = StubProof(Seq("b1"), Map(
        "b1" -> StubBox(fresh("a"), Seq("b2")),
        "b2" -> StubBox(fresh("a"), Seq("b3")),
        "b3" -> StubBox(fresh("a"), Seq())
      ))

      checker.check(pf).toSet shouldBe Set(
        ("b2", RedefinitionOfFreshVar("b1")),
        ("b3", RedefinitionOfFreshVar("b1"))
      )
    }

    it("should check second step of box with no fresh var") {
      val pf = StubProof(Seq("b1"), Map(
        "b1" -> StubBox(nofresh, Seq("aline", "b2")),
        "aline" -> StubLine("yep"),
        "b2" -> StubBox(fresh("a"), Seq("b3")),
        "b3" -> StubBox(fresh("a"), Seq())
      ))

      checker.check(pf).toSet shouldBe Set(
        ("b3", RedefinitionOfFreshVar("b2"))
      )
    }

    it("should check second step of proof") {
      val pf = StubProof(Seq("something", "outer"), Map(
        "something" -> StubLine("yep"),
        "outer" -> StubBox(fresh("x_0"), Seq("inner")),
        "inner" -> StubBox(fresh("x_0"), Seq())
      ))

      checker.check(pf) shouldBe List(
        ("inner", RedefinitionOfFreshVar("outer"))
      )
    }
  }
}
