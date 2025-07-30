import { LineProofStep, ProofStep, ProofStepPosition } from "@/types/types";

import { ProofContextProps } from "@/contexts/ProofProvider";
// import { ProofStep } from "@/types/types";
import { v4 as uuidv4 } from "uuid";

export abstract class Command {
  private commandUuid: string;
  constructor() {
    this.commandUuid = uuidv4();
  }
  getCommandUuid(): string {
    return this.commandUuid;
  }
  abstract execute(proofContext: ProofContextProps): void;
  abstract undo(proofContext: ProofContextProps): void;
  abstract getDescription(): string;
}

export class AddLineCommand extends Command {
  private newLineUuid: string;
  private position: ProofStepPosition;
  constructor(uuid: string, prepend: boolean = false, newLineUuid?: string) {
    super();
    this.newLineUuid = newLineUuid ?? uuidv4();
    this.position = {
      nearProofStepWithUuid: uuid,
      prepend: prepend,
    };
  }

  execute(proofContext: ProofContextProps): void {
    console.log(
      "Execute AddLineCommand near line with uuid " +
        this.position.nearProofStepWithUuid,
    );
    proofContext.addStep(
      {
        stepType: "line",
        uuid: this.newLineUuid,
        formula: {
          userInput: "?",
          ascii: null,
          latex: null,
        },
        justification: {
          rule: null,
          refs: [],
        },
      },
      this.position,
    );
  }

  undo(proofContext: ProofContextProps): void {
    console.log(
      "Undoing AddLineCommand near line with uuid " + this.newLineUuid,
    );
    proofContext.removeStep(this.newLineUuid);
  }

  getDescription(): string {
    return `Add line ${
      this.position.prepend ? "before" : "after"
    } line with uuid ${this.position.nearProofStepWithUuid}`;
  }
}

export class AddBoxedLineCommand extends Command {
  private newBoxUuid: string;
  private newLineUuid: string;
  private position: ProofStepPosition;
  constructor(
    uuid: string,
    prepend: boolean = false,
    newLineUuid?: string,
    newBoxUuid?: string,
  ) {
    super();
    this.newBoxUuid = newBoxUuid ?? uuidv4();
    this.newLineUuid = newLineUuid ?? uuidv4();
    this.position = {
      nearProofStepWithUuid: uuid,
      prepend: prepend,
    };
  }

  execute(proofContext: ProofContextProps): void {
    console.log(
      "Execute AddBoxLineCommand near line with uuid " +
        this.position.nearProofStepWithUuid,
    );
    proofContext.addStep(
      {
        stepType: "box",
        uuid: this.newBoxUuid,
        boxInfo: {
          freshVar: null,
        },
        proof: [
          {
            stepType: "line",
            uuid: this.newLineUuid,
            formula: {
              userInput: "?",
              ascii: null,
              latex: null,
            },
            justification: {
              rule: null,
              refs: [],
            },
          },
        ],
      },
      this.position,
    );
  }

  undo(proofContext: ProofContextProps): void {
    console.log(
      "Undoing AddBoxLineCommand near line with uuid " + this.newBoxUuid,
    );
    proofContext.removeStep(this.newBoxUuid);
  }
  getDescription(): string {
    return `Add box line ${
      this.position.prepend ? "before" : "after"
    } line with uuid ${this.position.nearProofStepWithUuid}`;
  }
}

export class RemoveProofStepCommand extends Command {
  private proofStepUuid: string;
  private proofStep: ProofStep | null;
  private position: ProofStepPosition;

  constructor(uuid: string) {
    super();
    this.proofStepUuid = uuid;
    this.proofStep = null;
    this.position = {
      nearProofStepWithUuid: "",
      prepend: false,
    };
  }

  execute(proofContext: ProofContextProps): void {
    const { proofStepDetails: nearestDeletableProofStep, cascadeCount } =
      proofContext.getNearestDeletableProofStep(this.proofStepUuid);
    if (nearestDeletableProofStep == null) {
      console.warn(
        "This line cannot be deleted because it is the only line in the proof",
      );
    }
    this.proofStepUuid = nearestDeletableProofStep.proofStep.uuid;
    this.proofStep = nearestDeletableProofStep.proofStep;
    this.position = nearestDeletableProofStep.position;

    if (cascadeCount > 0) {
      // TODO: Make a real dialog to confirm deletion
      window.alert(
        `This action will cascade and delete ${cascadeCount} surrounding box(es) as well.`,
      );
    }

    proofContext.removeStep(this.proofStepUuid);
  }

  undo(proofContext: ProofContextProps): void {
    console.log(
      "Undoing RemoveProofStepCommand for line " + this.proofStepUuid,
    );

    if (this.proofStep == null) {
      throw new Error("Cannot undo RemoveProofStepCommand without proofStep");
    }
    proofContext.addStep(this.proofStep, this.position);
  }
  getDescription(): string {
    return `Remove line with uuid ${this.proofStepUuid}`;
  }
}

export class UpdateLineProofStepCommand extends Command {
  private proofStepUuid: string;
  private prevProofStep: LineProofStep | null;
  private updatedLineProofStep: LineProofStep;

  constructor(uuid: string, updatedLineProofStep: LineProofStep) {
    super();
    this.proofStepUuid = uuid;
    this.prevProofStep = null;
    this.updatedLineProofStep = updatedLineProofStep;
  }

  execute(proofContext: ProofContextProps): void {
    console.log(
      "Execute UpdateProofStepCommand for line " + this.proofStepUuid,
    );

    const proofStepDetails = proofContext.getProofStepDetails(
      this.proofStepUuid,
    );

    if (proofStepDetails == null) {
      throw new Error("Cannot update proof step that doesn't exist");
    }

    if (proofStepDetails.proofStep.stepType != "line") {
      throw new Error("Cannot update proof step that is not a line");
    }

    this.prevProofStep = proofStepDetails.proofStep as LineProofStep;

    proofContext.updateLine(this.proofStepUuid, this.updatedLineProofStep);
  }

  undo(proofContext: ProofContextProps): void {
    console.log(
      "Undoing UpdateProofStepCommand for line " + this.proofStepUuid,
    );

    if (this.prevProofStep == null) {
      throw new Error(
        "Cannot undo UpdateProofStepCommand without prevProofStep",
      );
    }

    proofContext.updateLine(this.proofStepUuid, this.prevProofStep);
  }
  getDescription(): string {
    return `Remove line with uuid ${this.proofStepUuid}`;
  }
}

export class SetFreshVarOnBoxCommand extends Command {
  private boxUuid: string;
  private freshVar: string | null;
  private prevFreshVar: string | null = null;

  constructor(uuid: string, freshVar: string | null) {
    super();
    this.boxUuid = uuid;
    this.freshVar = freshVar;
  }

  execute(proofContext: ProofContextProps): void {
    const details = proofContext.getProofStepDetails(this.boxUuid);
    if (details?.proofStep.stepType !== "box") {
      throw new Error("Cannot set freshVar on a step that is not a box");
    }
    this.prevFreshVar = details.proofStep.stepType;
    proofContext.updateFreshVarOnBox(this.boxUuid, this.freshVar);
  }

  undo(proofContext: ProofContextProps): void {
    proofContext.updateFreshVarOnBox(this.boxUuid, this.prevFreshVar);
  }

  getDescription(): string {
    return `Set freshVar to ${this.freshVar} on box ${this.boxUuid}`;
  }
}

export class MoveProofStepCommand extends Command {
  private fromUuid: string;
  private toUuid: string;
  private prepend: boolean;

  private recoveryPosition: ProofStepPosition | null = null

  constructor(from: string, to: string, prepend: boolean) {
    super();
    this.fromUuid = from
    this.toUuid = to
    this.prepend = prepend
  }

  execute(proofContext: ProofContextProps): void {
    if (proofContext.isDescendant(this.fromUuid, this.toUuid)) {
      console.warn(`Cannot move a box into itself...`)
      return;
    }

    // while we are only-child, keep recursing outwards
    while (proofContext.isOnlyChild(this.fromUuid)) {
      const parent = proofContext.getParentUuid(this.fromUuid)
      if (!parent) {
        console.warn(`Unable to move`)
        return;
      }
      this.fromUuid = parent
    }

    const proofStep = proofContext.getProofStepDetails(this.fromUuid)?.proofStep
    if (!proofStep) {
      console.warn(`Proof step ${this.fromUuid} doesn't exist`)
      return;
    }

    this.recoveryPosition = proofContext.getNeighbour(this.fromUuid)
    if (!this.recoveryPosition) {
      console.warn(`Couldn't find valid neighbour of ${this.fromUuid}`)
    }

    proofContext.removeStep(this.fromUuid)
    proofContext.addStep(proofStep, {
      nearProofStepWithUuid: this.toUuid,
      prepend: this.prepend
    })
  }

  undo(proofContext: ProofContextProps): void {
    const proofStep = proofContext.getProofStepDetails(this.fromUuid)?.proofStep
    if (!proofStep || !this.recoveryPosition) {
      console.warn("Unable to move back.")
      return;
    }
    proofContext.removeStep(this.fromUuid)
    proofContext.addStep(proofStep, this.recoveryPosition)
  }

  getDescription(): string {
    return `Move step ${this.fromUuid} to ${this.toUuid} (prepend? ${this.prepend ? "yes" : "no"})`
  }
}
