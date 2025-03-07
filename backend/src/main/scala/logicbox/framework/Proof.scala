package logicbox.framework

object Proof {
  sealed trait Step[+Formula, +Rule, +BoxInfo, +Id]
  trait Line[+Formula, +Rule, +Id] extends Step[Formula, Rule, Nothing, Id] {
    def formula: Formula
    def rule: Rule
    def refs: Seq[Id]
  }

  trait Box[+BoxInfo, +Id] extends Step[Nothing, Nothing, BoxInfo, Id] {
    def info: BoxInfo
    def steps: Seq[Id]
  }

  object Line {
    def unapply[F, R, I](line: Line[F, R, I]): Option[(F, R, Seq[I])] =
      Some((line.formula, line.rule, line.refs))
  }

  object Box {
    def unapply[B, I](box: Box[B, I]): Option[(B, Seq[I])] =
      Some((box.info, box.steps))
  }

  case class StepNotFound[Id](id: Id, expl: String)
}

trait Proof[+F, +R, +B, Id] {
  def getStep(id: Id): Either[Proof.StepNotFound[Id], Proof.Step[F, R, B, Id]]
  def rootSteps: Seq[Id]
}

