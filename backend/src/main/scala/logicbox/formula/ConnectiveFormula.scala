package logicbox.formula

trait ConnectiveFormula[F]

object ConnectiveFormula {

  sealed trait BinaryConnective[F] extends ConnectiveFormula[F] {
    def phi: F
    def psi: F
  }

  trait And[F] extends BinaryConnective[F]
  trait Or[F] extends BinaryConnective[F]
  trait Implies[F] extends BinaryConnective[F]

  trait Not[F] extends ConnectiveFormula[F] {
    def phi: F
  }
  trait Contradiction[F]() extends ConnectiveFormula[F]

  object And {
    def unapply[F <: ConnectiveFormula[F]](formula: F): Option[(F, F)] = formula match {
      case f: And[F] => Some((f.phi, f.psi))
      case _ => None
    }
  }

  object Or {
    def unapply[F <: ConnectiveFormula[F]](formula: F): Option[(F, F)] = formula match {
      case f: Or[F] => Some((f.phi, f.psi))
      case _ => None
    }
  }

  object Implies {
    def unapply[F <: ConnectiveFormula[F]](formula: F): Option[(F, F)] = formula match {
      case f: Implies[F] => Some((f.phi, f.psi))
      case _ => None
    }
  }

  object Not {
    def unapply[F <: ConnectiveFormula[F]](formula: F): Option[F] = formula match {
      case f: Not[F] => Some(f.phi)
      case _ => None
    }
  }

  object Contradiction {
    def unapply[F <: ConnectiveFormula[F]](formula: F): Boolean = formula match {
      case f: Contradiction[F] => true
      case _ => false
    }
  }
}
