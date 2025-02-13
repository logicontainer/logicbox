import { ProofContextProps } from "@/contexts/ProofProvider";
import { v4 as uuidv4 } from 'uuid';

export abstract class Command {
  private commandUuid: string;
  constructor() {
    this.commandUuid = uuidv4();
  }
  getCommandUuid (): string { return this.commandUuid };
  abstract execute (proofContext: ProofContextProps): void;
  abstract undo (proofContext: ProofContextProps): void;
  abstract getDescription (): string;
}
export class AddLineCommand extends Command {
  private nearLineWithUuid: string;
  private newLineUuid: string;
  private prepend: boolean;
  constructor(uuid: string, prepend: boolean = false) {
    super();
    this.nearLineWithUuid = uuid;
    this.newLineUuid = uuidv4();
    this.prepend = prepend;
  }

  execute (proofContext: ProofContextProps): void {
    console.log("Execute AddLineCommand near line with uuid " + this.nearLineWithUuid)
    proofContext.addLine({
      "stepType": "line",
      "uuid": this.newLineUuid,
      "formula": "p and r",
      "latexFormula": this.newLineUuid.slice(0, 5),
      "justification": {
        "name": "assumption",
        "refs": []
      },
    }, this.nearLineWithUuid, this.prepend)
  }

  undo (proofContext: ProofContextProps): void {
    console.log("Undoing AddLineCommand near line with uuid " + this.newLineUuid)
    proofContext.removeLine(this.newLineUuid)
  }
  getDescription (): string {
    return `Add line ${this.prepend ? "before" : "after"} line with uuid ${this.nearLineWithUuid}`;
  }
}