package logicbox.framework

trait RuleChecker[-F, -R, -B] {
  def check(rule: R, formula: F, refs: List[Reference[F, B]]): List[Error]
}
