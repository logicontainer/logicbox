package logicbox.proof

import logicbox.framework.Navigator
import logicbox.rule.FreshVarBoxInfo
import logicbox.framework.Location
import logicbox.framework.Location.Step

class FreshVarBoxInfoNavigator[V] extends Navigator[FreshVarBoxInfo[V], V] {
  override def get(subject: FreshVarBoxInfo[V], loc: Location): Option[V] = loc.steps match {
    case Step.FreshVar :: Nil => subject.freshVar
    case _ => None
  }
}
