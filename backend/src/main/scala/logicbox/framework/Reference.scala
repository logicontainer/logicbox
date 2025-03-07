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
    def unapply[F](f: Line[F]): Option[(F)] =
      Some(f.formula)
  }

  object Box {
    def unapply[F, I](b: Box[F, I]): Option[(I, F, F)] =
      Some(b.info, b.assumption, b.conclusion)
  }
}
