package logicbox.proof

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors
import logicbox.framework.Location
import logicbox.framework.Navigator
import logicbox.rule.ReferenceLineImpl
import logicbox.rule.ReferenceBoxImpl

class ReferenceNavigatorTest extends AnyFunSpec {
  case class StubFormula(str: String)
  case class BoxInfo(str: String)

  describe("get") {
    val formulaNav = new Navigator[StubFormula, StubFormula] {
      // do nothing, no matter loc
      override def get(subject: StubFormula, loc: Location): Option[StubFormula] = Some(StubFormula(s"${subject.str}_${loc.steps.map(_.toString).mkString}"))
    }
    val boxInfoNav = new Navigator[BoxInfo, String] {
      override def get(subject: BoxInfo, loc: Location): Option[String] = 
        Some(s"${subject.str}_${loc.steps.map(_.toString).mkString}")
    }
    val nav = ReferenceNavigator[StubFormula, BoxInfo, StubFormula | String](formulaNav, boxInfoNav)

    it("should correctly unpack step into formula") {
      nav.get(ReferenceLineImpl(StubFormula("test")), Location.root) shouldBe Some(StubFormula("test_"))
    }

    it("should correct delegate to formula nav. for line") {
      nav.get(ReferenceLineImpl(StubFormula("test")), Location.operand(4)) shouldBe Some(StubFormula("test_4"))
    }

    it("should correctly obtain the assumption") {
      nav.get(ReferenceBoxImpl(BoxInfo(""), Some(ReferenceLineImpl(StubFormula("ass"))), None), Location.assumption) shouldBe Some(StubFormula("ass_"))
      nav.get(ReferenceBoxImpl(BoxInfo(""), Some(ReferenceLineImpl(StubFormula("ass"))), None), Location.assumption.operand(2)) shouldBe Some(StubFormula("ass_2"))
    }

    it("shouold correct obtain the conclusion") {
      nav.get(ReferenceBoxImpl(BoxInfo(""), None, Some(ReferenceLineImpl(StubFormula("concl")))), Location.conclusion) shouldBe Some(StubFormula("concl_"))
      nav.get(ReferenceBoxImpl(BoxInfo(""), None, Some(ReferenceLineImpl(StubFormula("concl")))), Location.conclusion.operand(4)) shouldBe Some(StubFormula("concl_4"))
    }

    it("should obtain box info") {
      nav.get(ReferenceBoxImpl(BoxInfo("something important"), None, None), Location.freshVar) shouldBe Some("something important_")
      nav.get(ReferenceBoxImpl(BoxInfo("something important"), None, None), Location.freshVar.operand(55)) shouldBe Some("something important_55")
    }
    
    it("should work on deeply nested example") {
      val ref = ReferenceBoxImpl(
        BoxInfo("2"),
        Some(ReferenceBoxImpl(
          BoxInfo("2"),
          Some(ReferenceBoxImpl(BoxInfo("3"), None, None)),
          Some(ReferenceLineImpl(StubFormula("heyyy")))
        )),
        None
      )

      nav.get(ref, Location.assumption.conclusion.operand(4).lhs) shouldBe Some(StubFormula("heyyy_40"))
      nav.get(ref, Location.assumption.assumption.freshVar) shouldBe Some("3_")
    }
  }
}
