package logicbox.proof

import logicbox.framework.Navigator
import logicbox.rule.FreshVarBoxInfo
import logicbox.framework.Location

class FreshVarBoxInfoNavigator[V] extends Navigator[FreshVarBoxInfo[V], V] {
  override def get(subject: FreshVarBoxInfo[V], loc: Location): Option[V] = loc.steps match {
    case Nil => subject.freshVar
    case _ => None
  }
}
