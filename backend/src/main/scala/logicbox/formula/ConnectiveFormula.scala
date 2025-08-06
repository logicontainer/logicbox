package logicbox.formula

trait ConnectiveFormula[F] {
  def unapplyAnd(f: F): Option[(F, F)]
  def unapplyOr(f: F): Option[(F, F)]
  def unapplyImplies(f: F): Option[(F, F)]
  def unapplyNot(f: F): Option[F]
  def unapplyContradiction(f: F): Boolean
}

object ConnectiveFormula {
  object & {
    def unapply[F](f: F)(implicit c: ConnectiveFormula[F]): Option[(F, F)] = c.unapplyAnd(f)
  }

  object || {
    def unapply[F](f: F)(implicit c: ConnectiveFormula[F]): Option[(F, F)] = c.unapplyOr(f)
  }

  object --> {
    def unapply[F](f: F)(implicit c: ConnectiveFormula[F]): Option[(F, F)] = c.unapplyImplies(f)
  }

  object ~ {
    def unapply[F](f: F)(implicit c: ConnectiveFormula[F]): Option[F] = c.unapplyNot(f)
  }

  object ⊥ {
    def unapply[F](f: F)(implicit c: ConnectiveFormula[F]): Boolean = c.unapplyContradiction(f)
  }
}
