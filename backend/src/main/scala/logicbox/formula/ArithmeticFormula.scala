package logicbox.formula

trait ArithmeticTerm[T]

object ArithmeticTerm {
  sealed trait BinOp[T] extends ArithmeticTerm[T] {
    def t1: T
    def t2: T
  }

  trait Plus[T] extends BinOp[T]
  trait Mult[T] extends BinOp[T]

  trait Zero[T] extends ArithmeticTerm[T]
  trait One[T] extends ArithmeticTerm[T]

  object Plus {
    def unapply[F, T <: ArithmeticTerm[T], V <: T](f: F): Option[(T, T)] = f match {
      case p: Plus[T] @unchecked => Some(p.t1, p.t2)
      case _ => None
    }
  }

  object Mult {
    def unapply[F, T <: ArithmeticTerm[T], V <: T](f: F): Option[(T, T)] = f match {
      case p: Mult[T] @unchecked => Some(p.t1, p.t2)
      case _ => None
    }
  }

  object Zero {
    def unapply[F, T <: ArithmeticTerm[T], V <: T](f: F): Boolean = f match {
      case p: Zero[T] @unchecked => true
      case _ => false
    }
  }

  object One {
    def unapply[F, T <: ArithmeticTerm[T], V <: T](f: F): Boolean = f match {
      case p: One[T] @unchecked => true
      case _ => false
    }
  }
}

