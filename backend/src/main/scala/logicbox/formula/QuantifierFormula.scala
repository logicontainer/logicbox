package logicbox.formula

trait QuantifierFormula[F, T, V] {
  def unapplyExists(f: F): Option[(V, F)]
  def unapplyForAll(f: F): Option[(V, F)]
  def unapplyEquals(f: F): Option[(T, T)]
}

object QuantifierFormula {
  object ∃ {
    def unapply[F, V](f: F)(implicit q: QuantifierFormula[F, ?, V]): Option[(V, F)] = q.unapplyExists(f)
  }

  object ∀ {
    def unapply[F, V](f: F)(implicit q: QuantifierFormula[F, ?, V]): Option[(V, F)] = q.unapplyForAll(f)
  }

  object === {
    infix def unapply[F, T](f: F)(implicit q: QuantifierFormula[F, T, ?]): Option[(T, T)] = q.unapplyEquals(f)
  }
}
