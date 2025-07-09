package logicbox.framework

// steps is a list of 'nodes' in the AST
// eg. for phi -> psi, index 0 referes to phi, 1 to psi
//     for boxes, index 0 is assumption, 1 is concl, 2 is the fresh variable
case class Location(steps: List[Int]) {
  def root: Location = this // identity
  def lhs: Location = Location(steps :+ 0)
  def rhs: Location = Location(steps :+ 1)
  def negated: Location = Location(steps :+ 0)
  def formulaInsideQuantifier: Location = Location(steps :+ 0)

  def firstLine: Location = Location(steps :+ 0)
  def lastLine: Location = Location(steps :+ 1)
  def freshVar: Location = Location(steps :+ 2)

  def operand(idx: Int) = Location(steps :+ idx)

  def premise(idx: Int) = Location(steps :+ idx)
  def conclusion = Location(steps :+ 0)
}

object Location {
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
