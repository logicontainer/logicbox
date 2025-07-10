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

    it("should get formula of single line by target 'conclusion'") {
      val pf = StubProof(
        rootSteps = Seq("1"),
        map = Map(
          "1" -> StubLine(StubFormula(10))
        )
      )

      nav.get((pf, "1"), Location.conclusion) shouldBe Some("10_")
    }

    it("should get formula of premises") {
      val pf = StubProof(
        rootSteps = Seq("1", "2"),
        map = Map(
          "1" -> StubLine(StubFormula(100)),
          "1b" -> StubLine(StubFormula(123)),
          "2" -> StubLine(StubFormula(), Good(), Seq("1", "1b"))
        )
      )

      nav.get((pf, "2"), Location.premise(0)) shouldBe Some("100_")
      nav.get((pf, "2"), Location.premise(1)) shouldBe Some("123_")
    }

    it("should return none when ref is OB") {
      val pf = StubProof(
        rootSteps = Seq("1"),
        map = Map(
          "0" -> StubLine(),
          "1" -> StubLine(StubFormula(10), Good(), Seq())
        )
      )

      nav.get((pf, "1"), Location.premise(0)) shouldBe None
    }

    it("should return none when ref is not a step") {
      val pf = StubProof(
        rootSteps = Seq("1"),
        map = Map(
          "1" -> StubLine(StubFormula(10), Good(), Seq("invalid"))
        )
      )

      nav.get((pf, "1"), Location.premise(0)) shouldBe None
      nav.get((pf, "2"), Location.conclusion) shouldBe None
    }

    it("should return none when try to get formula of box in ref") {
      val pf = StubProof(
        rootSteps = Seq("1"),
        map = Map(
          "box" -> StubBox(),
          "1" -> StubLine(StubFormula(10), Good(), Seq("box"))
        )
      )

      nav.get((pf, "1"), Location.premise(0)) shouldBe None
    }

    it("should get assumption of box") {
      val pf = StubProof(
        rootSteps = Seq("box", "ass"),
        map = Map(
          "ass" -> StubLine(StubFormula(40)),
          "box" -> StubBox(info = StubBoxInfo(), steps = Seq("ass")),
          "line" -> StubLine(StubFormula(), Good(), Seq("box"))
        )
      )

      nav.get((pf, "line"), Location.premise(0).firstLine) shouldBe Some("40_")
    }

    it("should propagate rest of location in assumption of box") {
      val pf = StubProof(
        rootSteps = Seq("box", "ass"),
        map = Map(
          "ass" -> StubLine(StubFormula(40)),
          "box" -> StubBox(info = StubBoxInfo(), steps = Seq("ass")),
          "line" -> StubLine(StubFormula(), Good(), Seq("box"))
        )
      )

      nav.get((pf, "line"), Location.premise(0).firstLine.lhs.operand(2)) shouldBe Some("40_LhsOperand(2)")
    }

    it("should return none when attempting to get first line of box which isn't a step") {
      val pf = StubProof(
        rootSteps = Seq("box"),
        map = Map(
          "box" -> StubBox(info = StubBoxInfo(), steps = Seq("ass")), // ass undefined
          "line" -> StubLine(StubFormula(), Good(), Seq("box"))
        )
      )

      nav.get((pf, "line"), Location.premise(0).firstLine.lhs.operand(2)) shouldBe None
    }

    it("should obtain box info") {
      val pf = StubProof(
        rootSteps = Seq("box", "line"),
        map = Map(
          "box" -> StubBox(info = StubBoxInfo("yessir")),
          "line" -> StubLine(StubFormula(), Good(), Seq("box"))
        )
      )

      nav.get((pf, "line"), Location.premise(0).freshVar) shouldBe Some("yessir_")
    }

    it("should let rest of location through to box info") {
      val pf = StubProof(
        rootSteps = Seq("box"),
        map = Map(
          "box" -> StubBox(info = StubBoxInfo("thing")),
          "line" -> StubLine(StubFormula(), Good(), Seq("box"))
        )
      )

      nav.get((pf, "line"), Location.premise(0).freshVar.operand(4)) shouldBe Some("thing_Operand(4)")
    }

    it("should let rest of location through to formula") {
      val pf = StubProof(
        rootSteps = Seq("1"),
        map = Map(
          "1" -> StubLine(StubFormula(123))
        )
      )

      nav.get((pf, "1"), Location.conclusion.lhs.rhs.lhs) shouldBe Some("123_LhsRhsLhs")
    }

    it("should return none on invalid id") {
      val pf = StubProof()
      nav.get((pf, "bruh"), Location.conclusion) shouldBe None
    }

    it("should return none when location is empty") {
      val pf = StubProof(
        rootSteps = Seq("1"),
        map = Map(
          "1" -> StubLine(StubFormula(123))
        )
      )

      nav.get((pf, "1"), Location.root) shouldBe None
    }

    it("should return none when first position is invalid") {
      val pf = StubProof(
        rootSteps = Seq("1"),
        map = Map(
          "1" -> StubLine(StubFormula(123))
        )
      )

      nav.get((pf, "1"), Location.lhs) shouldBe None
    }

    it("should return none if ref doesn't exist") {
      val pf = StubProof(
        rootSteps = Seq("line"),
        map = Map("line" -> StubBox())
      )

      nav.get((pf, "line"), Location.premise(0)) shouldBe None
    }
  }
}
