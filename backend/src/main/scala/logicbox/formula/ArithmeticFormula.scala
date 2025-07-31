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
    def unapply[T <: ArithmeticTerm[T]](t: T): Option[(T, T)] = t match {
      case p: Plus[T] @unchecked => Some(p.t1, p.t2)
      case _ => None
    }
  }

  object + {
    def unapply[T <: ArithmeticTerm[T]](t: T): Option[(T, T)] = Plus.unapply[T](t)
  }


  object Mult {
    def unapply[T <: ArithmeticTerm[T]](t: T): Option[(T, T)] = t match {
      case p: Mult[T] @unchecked => Some(p.t1, p.t2)
      case _ => None
    }
  }

  object ~* {
    def unapply[T <: ArithmeticTerm[T]](t: T): Option[(T, T)] = Mult.unapply[T](t)
  }

  object Zero {
    def unapply[T <: ArithmeticTerm[T]](t: T): Boolean = t match {
      case p: Zero[T] @unchecked => true
      case _ => false
    }
  }

  object One {
    def unapply[T <: ArithmeticTerm[T]](t: T): Boolean = t match {
      case p: One[T] @unchecked => true
      case _ => false
    }
  }
}

