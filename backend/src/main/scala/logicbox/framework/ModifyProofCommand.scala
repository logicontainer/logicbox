package logicbox.framework

sealed trait ModifyProofCommand[+F, +R, +Id]

case class AddLine[Id](id: Id, where: ModifiableProof.Pos[Id]) extends ModifyProofCommand[Nothing, Nothing, Id]
case class AddBox[Id](id: Id, where: ModifiableProof.Pos[Id]) extends ModifyProofCommand[Nothing, Nothing, Id]

case class UpdateFormula[Id, F](lineId: Id, formula: F) extends ModifyProofCommand[F, Nothing, Id]
case class UpdateRule[Id, R](lineId: Id, rule: R) extends ModifyProofCommand[Nothing, R, Id]
case class UpdateReferences[Id](lineId: Id, refs: Seq[Id]) extends ModifyProofCommand[Nothing, Nothing, Id]

case class RemoveStep[Id](id: Id) extends ModifyProofCommand[Nothing, Nothing, Id]
