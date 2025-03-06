package logicbox.proof

import logicbox.framework._

object ProofStubs {
  case class StubFormula(i: Int = 0)

  sealed trait StubRule
  case class Good() extends StubRule
  case class Bad() extends StubRule

  case class StubBoxInfo(info: String = "")
  case class StubViolation()

  type F = StubFormula
  type R = StubRule
  type B = StubBoxInfo
  type V = StubViolation
  type Id = String

  case class StubLine(
    override val formula: StubFormula = StubFormula(),
    override val rule: StubRule = Good(),
    override val refs: Seq[Id] = Seq(),
  ) extends Proof.Line[StubFormula, StubRule, Id]

  case class StubBox(
    override val info: StubBoxInfo = StubBoxInfo(),
    override val steps: Seq[Id] = Seq(),
  ) extends Proof.Box[StubBoxInfo, Id]

  case class StubProof(
    override val rootSteps: Seq[Id] = Seq(),
    val map: Map[Id, Proof.Step[F, R, B, Id]] = Map.empty
  ) extends Proof[F, R, B, Id] {
    override def getStep(id: Id): Either[Proof.StepNotFound[Id], Proof.Step[F, R, B, Id]] = 
      map.get(id) match {
        case None => Left(Proof.StepNotFound(id, s"STUB THING $id"))
        case Some(value) => Right(value)
      }
  }

  case class StubRuleChecker() extends RuleChecker[F, R, B, V] {
    var refsCalledWith: Option[List[Reference[StubFormula, StubBoxInfo]]] = None

    override def check(rule: StubRule, formula: StubFormula, refs: List[Reference[StubFormula, StubBoxInfo]]): List[StubViolation] = 
      refsCalledWith = Some(refs)
      rule match {
        case Good() => Nil
        case Bad() => List(StubViolation())
      }
  }
}
