package logicbox.formula

trait QuantifierFormula[F, T, V <: T]

object QuantifierFormula {
  trait Exists[F, T, V <: T] extends QuantifierFormula[F, T, V] {
    def x: V
    def phi: F
  }

  trait ForAll[F, T, V <: T] extends QuantifierFormula[F, T, V] {
    def x: V
    def phi: F
  }

  trait Equals[F, T, V <: T] extends QuantifierFormula[F, T, V] {
    def t1: T
    def t2: T
  }

  object Exists {
    def unapply[F, T, V <: T](f: QuantifierFormula[F, T, V]): Option[(V, F)] = f match {
      case f: Exists[F, T, V] => Some(f.x, f.phi)
      case _ => None
    }
  }

  object ForAll {
    def unapply[F, T, V <: T](f: QuantifierFormula[F, T, V]): Option[(V, F)] = f match {
      case f: ForAll[F, T, V] => Some(f.x, f.phi)
      case _ => None
    }
  }

  object Equals {
    def unapply[F, T, V <: T](f: QuantifierFormula[F, T, V]): Option[(T, T)] = f match {
      case f: Equals[F, T, V] => Some(f.t1, f.t2)
      case _ => None
    }
  }
}
