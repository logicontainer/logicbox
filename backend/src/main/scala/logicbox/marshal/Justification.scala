package logicbox.marshal

case class Justification[R, Id](rule: R, refs: Seq[Id])
