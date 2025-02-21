import 'react-contexify/ReactContexify.css';

import { AddBoxedLineCommand, AddLineCommand, RemoveProofStepCommand } from '@/lib/commands';
import { Item, ItemParams, Menu, Separator, Submenu } from 'react-contexify';

import { LineProofStep } from '@/types/types';
import { useHistory } from '@/contexts/HistoryProvider';
import { useProof } from '@/contexts/ProofProvider';

const MENU_ID = 'proof-step-context-menu';

export function ProofStepContextMenu () {
  const historyContext = useHistory();
  const proofContext = useProof();
  const handleAddProofStep = (uuid: string, isBox: boolean = false, prepend: boolean = false) => {
    const addLineCommand = isBox ? new AddBoxedLineCommand(uuid, prepend) : new AddLineCommand(uuid, prepend);
    historyContext.addToHistory(addLineCommand)
  }
  const handleRemoveProofStep = (uuid: string) => {
    const removeLineCommand = new RemoveProofStepCommand(uuid);
    historyContext.addToHistory(removeLineCommand)
  }
  const handleUpdateProofStep = (uuid: string, updatedLineProofStep: LineProofStep) => {
    console.log(updatedLineProofStep.uuid)
    proofContext.setActiveEdit(uuid)
  }

  const handleItemClick = ({ id, props }: ItemParams) => {
    console.log("handleItemClick", id, props);
    switch (id) {
      case "edit":
        console.log("edit", props.uuid)
        handleUpdateProofStep(props.uuid, {
          uuid: props.uuid,
          stepType: "line",
          formula: "p and r",
          latexFormula: `Id: ${props.uuid.slice(0, 5)}`,
          justification: {
            ruleName: "assumption",
            refs: []
          }
        })
        break;
      case "line-above":
        handleAddProofStep(props.uuid, false, true)
        break;
      case "line-below":
        handleAddProofStep(props.uuid, false, false)
        break;
      case "box-above":
        handleAddProofStep(props.uuid, true, true)
        break;
      case "box-below":
        handleAddProofStep(props.uuid, true, false)
        break;
      case "delete":
        handleRemoveProofStep(props.uuid)
        break;
    }
  }

  return (
    <div>
      <Menu id={MENU_ID}>
        <Item id="edit" onClick={handleItemClick}>Edit</Item>
        <Separator />
        <Submenu label="Add above">
          <Item id="line-above" onClick={handleItemClick}>Line</Item>
          <Item id="box-above" onClick={handleItemClick}>Box</Item>
        </Submenu>
        <Submenu label="Add below">
          <Item id="line-below" onClick={handleItemClick}>Line</Item>
          <Item id="box-below" onClick={handleItemClick}>Box</Item>
        </Submenu>
        <Separator />
        <Item id="delete" className='text-red-500' onClick={handleItemClick}>Delete</Item>
      </Menu>
    </div>
  );
}