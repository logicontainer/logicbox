package logicbox.rule

import logicbox.framework.Reference

case class ReferenceLineImpl[F](formula: F) extends Reference.Line[F]

case class ReferenceBoxImpl[F, B](info: B, first: Option[Reference[F, B]], last: Option[Reference[F, B]]) 
  extends Reference.Box[F, B]

