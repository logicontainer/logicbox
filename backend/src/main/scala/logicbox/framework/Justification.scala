package logicbox.framework

case class Justification[R, Id](rule: R, refs: Seq[Id])
