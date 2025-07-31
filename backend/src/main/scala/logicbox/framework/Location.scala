package logicbox.framework

// steps is a list of 'nodes' in the AST
// eg. for phi -> psi, index 0 referes to phi, 1 to psi
//     for boxes, index 0 is assumption, 1 is concl, 2 is the fresh variable
case class Location(steps: List[Location.Step]) {
  import Location.Step._
  def root: Location = this // identity
  def lhs: Location = Location(steps :+ Lhs)
  def rhs: Location = Location(steps :+ Rhs)
  def negated: Location = Location(steps :+ Negated)
  def formulaInsideQuantifier: Location = Location(steps :+ InsideQuantifier)

  def firstLine: Location = Location(steps :+ FirstLine)
  def lastLine: Location = Location(steps :+ LastLine)
  def freshVar: Location = Location(steps :+ FreshVar)

  def operand(idx: Int) = Location(steps :+ Operand(idx))

  def premise(idx: Int) = Location(steps :+ Premise(idx))
  def conclusion = Location(steps :+ Conclusion)
}

object Location {
  enum Step {
    case Lhs; case Rhs; 
    case Negated
    case InsideQuantifier

    case FirstLine
    case LastLine
    case FreshVar

    case Operand(idx: Int)

    case Premise(idx: Int)
    case Conclusion
  }

  def root = Location(Nil)
  def lhs = root.lhs
  def rhs = root.rhs
  def negated = root.negated
  def formulaInsideQuantifier = root.formulaInsideQuantifier

  def firstLine = root.firstLine
  def lastLine = root.lastLine
  def freshVar = root.freshVar

  def operand(idx: Int) = root.operand(idx)

  def premise(idx: Int) = root.premise(idx)
  def conclusion = root.conclusion
}
