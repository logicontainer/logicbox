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

  def lhs() = Location(Nil).lhs()
  def rhs() = Location(Nil).rhs()
  def inside() = Location(Nil).inside()

  def assumption() = Location(Nil).assumption()
  def conclusion() = Location(Nil).conclusion()
  def freshVar() = Location(Nil).freshVar()
}

// steps is a list of 'nodes' in the AST
// eg. for phi -> psi, index 0 referes to phi, 1 to psi
//     for boxes, index 0 is assumption, 1 is concl, 2 is the fresh variable
case class Location(steps: List[Int]) {
  def lhs(): Location = Location(steps :+ 0)
  def rhs(): Location = Location(steps :+ 1)
  def inside(): Location = Location(steps :+ 0)

  def assumption(): Location = Location(steps :+ 0)
  def conclusion(): Location = Location(steps :+ 1)
  def freshVar(): Location = Location(steps :+ 2)
}
