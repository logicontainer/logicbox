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
    def unapply[F, R, I](step: Step[F, R, ?, I]): Option[(F, R, Seq[I])] = step match {
      case line: Line[F, R, I] => Some(line.formula, line.rule, line.refs)
      case _ => None
    }
    
  }

  object Box {
    def unapply[B, I](step: Step[?, ?, B, I]): Option[(B, Seq[I])] = step match {
      case box: Box[B, I] => Some(box.info, box.steps)
      case _ => None
    }
  }

  case class StepNotFound[Id](id: Id)
}

trait Proof[+F, +R, +B, Id] {
  def getStep(id: Id): Either[Proof.StepNotFound[Id], Proof.Step[F, R, B, Id]]
  def rootSteps: Seq[Id]
}
