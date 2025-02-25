import { UndoRedo } from "./UndoRedo";
import { ValidateProofButton } from "./ValidateProofButton";

export function Toolbar() {
  return (
    <div className="flex gap-4">
      <div className="flex gap-4">
        <UndoRedo />
        <ValidateProofButton />
      </div>
    </div>
  );
}
