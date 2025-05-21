package logicbox.framework

case object Root
type Scope[+Id] = Root.type | Id
