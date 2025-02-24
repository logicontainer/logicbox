import { BoxProofStep, LineProofStep, ProofStep, ProofStepPosition } from "@/types/types";
import { InitBoxServerCommand, InitLineServerCommand, RemoveStepServerCommand, ServerCommand, UpdateLineServerCommand } from "./server-commands";

import { ProofContextProps } from "@/contexts/ProofProvider";
// import { ProofStep } from "@/types/types";
import { v4 as uuidv4 } from 'uuid';

export abstract class Command {
  private commandUuid: string;
  constructor() {
    this.commandUuid = uuidv4();
  }
  getCommandUuid (): string { return this.commandUuid };
  abstract execute (proofContext: ProofContextProps): ServerCommand[];
  abstract undo (proofContext: ProofContextProps): ServerCommand[];
  abstract getDescription (): string;
}
export class AddLineCommand extends Command {
  private newLineUuid: string;
  private position: ProofStepPosition;
  constructor(uuid: string, prepend: boolean = false) {
    super();
    this.newLineUuid = uuidv4();
    this.position = {
      nearProofStepWithUuid: uuid,
      prepend: prepend
    }
  }

  execute (proofContext: ProofContextProps): ServerCommand[] {
    console.log("Execute AddLineCommand near line with uuid " + this.position.nearProofStepWithUuid)
    proofContext.addLine({
      "stepType": "line",
      "uuid": this.newLineUuid,
      "formula": "p and r",
      "latexFormula": this.newLineUuid.slice(0, 5),
      "justification": {
        "ruleName": "assumption",
        "refs": []
      },
    }, this.position)

    return [
      new InitLineServerCommand(this.newLineUuid, this.position.nearProofStepWithUuid, this.position.prepend ? "before" : "after")
    ]
  }

  undo (proofContext: ProofContextProps): ServerCommand[] {
    console.log("Undoing AddLineCommand near line with uuid " + this.newLineUuid)
    proofContext.removeLine(this.newLineUuid)
    return [
      new RemoveStepServerCommand(this.newLineUuid)
    ]
  }
  getDescription (): string {
    return `Add line ${this.position.prepend ? "before" : "after"} line with uuid ${this.position.nearProofStepWithUuid}`;
  }
}


export class AddBoxedLineCommand extends Command {
  private newBoxUuid: string;
  private newLineUuid: string;
  private position: ProofStepPosition;
  constructor(uuid: string, prepend: boolean = false) {
    super();
    this.newBoxUuid = uuidv4();
    this.newLineUuid = uuidv4();
    this.position = {
      nearProofStepWithUuid: uuid,
      prepend: prepend
    }
  }

  execute (proofContext: ProofContextProps): ServerCommand[] {
    console.log("Execute AddBoxLineCommand near line with uuid " + this.position.nearProofStepWithUuid)
    proofContext.addLine(
      {
        stepType: "box",
        uuid: this.newBoxUuid,
        proof: [
          {
            stepType: "line",
            uuid: this.newLineUuid,
            formula: "p and r",
            latexFormula: this.newLineUuid.slice(0, 5),
            justification: {
              "ruleName": "assumption",
              "refs": []
            },
          }
        ]
      }, this.position)
    return [
      new InitBoxServerCommand(this.newBoxUuid, this.newLineUuid, this.position.nearProofStepWithUuid, this.position.prepend ? "before" : "after"),
    ]
  }

  undo (proofContext: ProofContextProps): ServerCommand[] {
    console.log("Undoing AddBoxLineCommand near line with uuid " + this.newBoxUuid)
    proofContext.removeLine(this.newBoxUuid)
    return [
      new RemoveStepServerCommand(this.newBoxUuid)
    ]
  }
  getDescription (): string {
    return `Add box line ${this.position.prepend ? "before" : "after"} line with uuid ${this.position.nearProofStepWithUuid}`;
  }
}


export class RemoveProofStepCommand extends Command {
  private proofStepUuid: string;
  private proofStep: ProofStep | null;
  private position: ProofStepPosition;

  constructor(uuid: string) {
    super();
    this.proofStepUuid = uuid
    this.proofStep = null;
    this.position = {
      nearProofStepWithUuid: "",
      prepend: false
    }
  }

  execute (proofContext: ProofContextProps): ServerCommand[] {
    console.log("Execute RemoveProofStepCommand for line " + this.proofStepUuid)
    const { proofStepDetails: nearestDeletableProofStep, cascadeCount } = proofContext.getNearestDeletableProofStep(this.proofStepUuid)
    if (nearestDeletableProofStep == null) {
      throw new Error("This line cannot be deleted because it is the only line in the proof")
    }
    this.proofStepUuid = nearestDeletableProofStep.proofStep.uuid
    this.proofStep = nearestDeletableProofStep.proofStep
    this.position = nearestDeletableProofStep.position

    if (cascadeCount > 0) {
      // TODO: Make a real dialog to confirm deletion
      window.alert(`This action will cascade and delete ${cascadeCount} surrounding box(es) as well.`)
    }

    proofContext.removeLine(
      this.proofStepUuid)
    return [
      new RemoveStepServerCommand(this.proofStepUuid)
    ]
  }

  undo (proofContext: ProofContextProps): ServerCommand[] {
    console.log("Undoing RemoveProofStepCommand for line " + this.proofStepUuid)

    if (this.proofStep == null) {
      throw new Error("Cannot undo RemoveProofStepCommand without proofStep")
    }
    proofContext.addLine(this.proofStep, this.position)
    // if (this.proofStep.stepType == "box") {
    //   // const innerLineProofStep = (this.proofStep as BoxProofStep).proof[0] as
    //   return [
    //     new InitBoxServerCommand(this.proofStep.uuid, innerLineProofStep.uuid, this.position.nearProofStepWithUuid, this.position.prepend ? "before" : "after"),
    //     new UpdateLineServerCommand(innerLineProofStep.uuid, innerLineProofStep.
    //   ]
    // }
    // return [
    //   new InitLineServerCommand(this.proofStepUuid, this.position.nearProofStepWithUuid, this.position.prepend ? "before" : "after"),
    //   new UpdateLineServerCommand(this.proofStepUuid, this.)
    // ]
    return [
      new InitLineServerCommand(this.proofStepUuid, this.position.nearProofStepWithUuid, this.position.prepend ? "before" :
        "after") // TODO: Should be init subtree or something. This needs a lot of recursive init line and update line commands
    ]
  }
  getDescription (): string {
    return `Remove line with uuid ${this.proofStepUuid}`;
  }
}




export class UpdateLineProofStepCommand extends Command {
  private proofStepUuid: string;
  private prevProofStep: LineProofStep | null;
  private updatedLineProofStep: LineProofStep;


  constructor(uuid: string, updatedLineProofStep: LineProofStep) {
    super();
    this.proofStepUuid = uuid
    this.prevProofStep = null;
    this.updatedLineProofStep = updatedLineProofStep;
  }

  execute (proofContext: ProofContextProps): ServerCommand[] {
    console.log("Execute UpdateProofStepCommand for line " + this.proofStepUuid)

    const proofStepDetails = proofContext.getProofStepDetails(this.proofStepUuid)

    if (proofStepDetails == null) {
      throw new Error("Cannot update proof step that doesn't exist")
    }

    if (proofStepDetails.proofStep.stepType != "line") {
      throw new Error("Cannot update proof step that is not a line")
    }

    this.prevProofStep = proofStepDetails.proofStep as LineProofStep

    proofContext.updateLine(
      this.proofStepUuid, this.updatedLineProofStep)

    return [
      new UpdateLineServerCommand(this.proofStepUuid, this.updatedLineProofStep.formula, this.updatedLineProofStep.justification.ruleName, this.updatedLineProofStep.justification.refs)
    ]
  }

  undo (proofContext: ProofContextProps): ServerCommand[] {
    console.log("Undoing UpdateProofStepCommand for line " + this.proofStepUuid)

    if (this.prevProofStep == null) {
      throw new Error("Cannot undo UpdateProofStepCommand without prevProofStep")
    }

    proofContext.updateLine(
      this.proofStepUuid, this.prevProofStep)

    return [
      new UpdateLineServerCommand(this.proofStepUuid, this.prevProofStep.formula, this.prevProofStep.justification.ruleName, this.prevProofStep.justification.refs)
    ]
  }
  getDescription (): string {
    return `Remove line with uuid ${this.proofStepUuid}`;
  }
}



