package logicbox.framework

trait CheckableRule[Formula, BoxInfo, Violation] {
  def check(formula: Formula, refs: List[Reference[Formula, BoxInfo]]): List[Violation]
}
