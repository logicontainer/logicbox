package logicbox.framework

object Location {
  // enum Step {
  //   case Inside
  //   case Lhs
  //   case Rhs
  //   case FreshVar
  //   case Assumption
  //   case Conclusion
  // }

  def root = Location(Nil)
  def lhs = root.lhs
  def rhs = root.rhs
  def negated = root.negated
  def formulaInsideQuantifier = root.insideForAll

  def assumption = root.assumption
  def conclusion = root.conclusion
  def freshVar = root.freshVar
}

// steps is a list of 'nodes' in the AST
// eg. for phi -> psi, index 0 referes to phi, 1 to psi
//     for boxes, index 0 is assumption, 1 is concl, 2 is the fresh variable
case class Location(steps: List[Int]) {
  def lhs: Location = Location(steps :+ 0)
  def rhs: Location = Location(steps :+ 1)
  def negated: Location = Location(steps :+ 0)
  def insideForAll: Location = Location(steps :+ 0)

  def assumption: Location = Location(steps :+ 0)
  def conclusion: Location = Location(steps :+ 1)
  def freshVar: Location = Location(steps :+ 2)
}
