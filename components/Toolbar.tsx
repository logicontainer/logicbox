import { UndoRedo } from "./UndoRedo";

export function Toolbar () {
  return (
    <div className="flex gap-4">
      <div className="flex-col">
        <UndoRedo />
      </div>
    </div>
  )
}