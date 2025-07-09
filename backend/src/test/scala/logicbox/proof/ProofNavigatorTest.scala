package logicbox.proof

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors
import logicbox.framework.Navigator
import logicbox.framework.Location

class ProofNavigatorTest extends AnyFunSpec {
  import logicbox.ProofStubs._
  describe("get") {
    val nav = ProofNavigator[F, B, Id, String](
      new Navigator[StubFormula, String] {
        override def get(subject: StubFormula, loc: Location): Option[String] = 
          Some(s"${subject.i.toString}_${loc.steps.map(_.toString).mkString}")
      },
      new Navigator[StubBoxInfo, String] {
        override def get(subject: StubBoxInfo, loc: Location): Option[String] = 
          Some(s"${subject.info}_${loc.steps.map(_.toString).mkString}")
      }
    )

    it("should get formula of single line") {
      val pf = StubProof(
        rootSteps = Seq("1"),
        map = Map(
          "1" -> StubLine(StubFormula(10))
        )
      )

      nav.get((pf, "1"), Location.root) shouldBe Some("10_")
    }

    it("should get assumption of box") {
      val pf = StubProof(
        rootSteps = Seq("box", "ass"),
        map = Map(
          "ass" -> StubLine(StubFormula(40)),
          "box" -> StubBox(info = StubBoxInfo(), steps = Seq("ass"))
        )
      )

      nav.get((pf, "box"), Location.firstLine) shouldBe Some("40_")
    }

    it("should get conclusion of first step of box") {
      val pf = StubProof(
        rootSteps = Seq("outer", "inner", "ass"),
        map = Map(
          "ass" -> StubLine(StubFormula()),
          "concl" -> StubLine(StubFormula(30)),
          "inner" -> StubBox(info = StubBoxInfo(), steps = Seq("ass", "concl")),
          "outer" -> StubBox(info = StubBoxInfo(), steps = Seq("inner")),
        )
      )

      nav.get((pf, "outer"), Location.firstLine.lastLine) shouldBe Some("30_")
    }

    it("should get deep nested boxes") {
      val pf = StubProof(
        rootSteps = Seq("b1", "b2", "b3", "line"),
        map = Map(
          "line" -> StubLine(StubFormula(123)),
          "b1" -> StubBox(StubBoxInfo(), Seq("line")),
          "b2" -> StubBox(StubBoxInfo(), Seq("b1")),
          "b3" -> StubBox(StubBoxInfo(), Seq("b2"))
        )
      )

      nav.get((pf, "b3"), Location.firstLine.lastLine.firstLine) shouldBe Some("123_")
    }

    it("should obtain box info") {
      val pf = StubProof(
        rootSteps = Seq("box"),
        map = Map(
          "box" -> StubBox(info = StubBoxInfo("yessir"))
        )
      )

      nav.get((pf, "box"), Location.freshVar) shouldBe Some("yessir_")
    }

    it("should let rest of location through to box info") {
      val pf = StubProof(
        rootSteps = Seq("box"),
        map = Map(
          "box" -> StubBox(info = StubBoxInfo("thing"))
        )
      )

      nav.get((pf, "box"), Location.freshVar.operand(4)) shouldBe Some("thing_4")
    }

    it("should let rest of location through to formula") {
      val pf = StubProof(
        rootSteps = Seq("1"),
        map = Map(
          "1" -> StubLine(StubFormula(123))
        )
      )

      nav.get((pf, "1"), Location.lhs.rhs.lhs) shouldBe Some("123_010")
    }

    it("should return none on invalid id") {
      val pf = StubProof()
      nav.get((pf, "bruh"), Location.root) shouldBe None
    }

    it("should return none if location is invalid") {
      val pf = StubProof(
        rootSteps = Seq("box"),
        map = Map("box" -> StubBox())
      )

      nav.get((pf, "box"), Location.operand(3)) shouldBe None
    }
  }
}
