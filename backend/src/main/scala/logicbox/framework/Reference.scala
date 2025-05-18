package logicbox.framework

sealed trait Reference[+Formula, +BoxInfo]

object Reference {
  trait Line[Fm] extends Reference[Fm, Nothing] {
    def formula: Fm
  }

  trait Box[+Formula, +BoxInfo] extends Reference[Formula, BoxInfo] {
    def info: BoxInfo
    def assumption: Formula
    def conclusion: Formula
  }

  object Line {
    def unapply[F](ref: Reference[F, ?]): Option[F] = ref match {
      case l: Line[F] => Some(l.formula)
      case _ => None
    }
  }

  object Box {
    def unapply[F, I](ref: Reference[F, I]): Option[(I, F, F)] = ref match {
      case b: Box[F, I] => Some(b.info, b.assumption, b.conclusion)
      case _ => None
    }
  }
}
