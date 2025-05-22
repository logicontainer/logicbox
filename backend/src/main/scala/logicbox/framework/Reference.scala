package logicbox.framework

sealed trait Reference[+Formula, +BoxInfo]

object Reference {
  trait Line[Fm] extends Reference[Fm, Nothing] {
    def formula: Fm
  }

  trait Box[+Formula, +BoxInfo] extends Reference[Formula, BoxInfo] {
    def info: BoxInfo
    def first: Option[Reference[Formula, BoxInfo]]
    def last: Option[Reference[Formula, BoxInfo]]
  }

  object Line {
    def unapply[F](ref: Reference[F, ?]): Option[F] = ref match {
      case l: Line[F] @unchecked => Some(l.formula)
      case _ => None
    }
  }

  object Box {
    def unapply[F, I](ref: Reference[F, I]): Option[(I, Option[Reference[F, I]], Option[Reference[F, I]])] = ref match {
      case b: Box[F, I] => Some(b.info, b.first, b.last)
      case _ => None
    }
  }
}
