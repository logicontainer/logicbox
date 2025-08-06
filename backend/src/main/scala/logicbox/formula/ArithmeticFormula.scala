package logicbox.formula

trait ArithmeticTerm[T] {
  def unapplyZero(t: T): Boolean
  def unapplyOne(t: T): Boolean
  def unapplyPlus(t: T): Option[(T, T)]
  def unapplyMult(t: T): Option[(T, T)]
}

object ArithmeticTerm {
  object _0 {
    def unapply[T](t: T)(implicit a: ArithmeticTerm[T]): Boolean = a.unapplyZero(t)
  }
  object _1 {
    def unapply[T](t: T)(implicit a: ArithmeticTerm[T]): Boolean = a.unapplyOne(t)
  }

  object + {
    def unapply[T](t: T)(implicit a: ArithmeticTerm[T]): Option[(T, T)] = a.unapplyPlus(t)
  }

  object * {
    def unapply[T](t: T)(implicit a: ArithmeticTerm[T]): Option[(T, T)] = a.unapplyMult(t)
  }
}
