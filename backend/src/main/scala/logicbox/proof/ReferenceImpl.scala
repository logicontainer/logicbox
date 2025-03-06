package logicbox.proof

import logicbox.framework.Reference

case class ReferenceLineImpl[F](formula: F) extends Reference.Line[F]

case class ReferenceBoxImpl[F, B](info: B, assumption: F, conclusion: F) 
  extends Reference.Box[F, B]

