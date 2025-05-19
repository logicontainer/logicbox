package logicbox.rule

import logicbox.framework.Reference

case class ReferenceLineImpl[F](formula: F) extends Reference.Line[F]

case class ReferenceBoxImpl[F, B](info: B, first: F, last: F) 
  extends Reference.Box[F, B]

