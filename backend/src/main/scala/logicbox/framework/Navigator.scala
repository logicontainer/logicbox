package logicbox.framework

trait Navigator[I, O] {
  // obtain the subtree of `subject` which `loc` refers to
  //  (eg. if loc = Location.rhs.lhs and subject = "p and (not q or r)", then get(subject, loc) = "not q")
  def get(subject: I, loc: Location): Option[O]
}
