import { Placement, InitBoxServerCommand as TInitBoxServerCommand, InitLineServerCommand as TInitLineServerCommand, RemoveStepServerCommand as TRemoveStepServerCommand, UpdateLineServerCommand as TUpdateLineServerCommand } from "@/types/types";

export abstract class ServerCommand {
  abstract marshall (): object;
}

export class InitLineServerCommand extends ServerCommand {
  private newLineUuid: string;
  private neighbourUuid: string;
  private placement: Placement;
  constructor(newLineUuid: string, neighbourUuid: string, placement: Placement) {
    super();
    this.newLineUuid = newLineUuid;
    this.neighbourUuid = neighbourUuid;
    this.placement = placement;
  }

  marshall (): TInitLineServerCommand {
    return {
      commandName: "initLine",
      options: {
        newLineUuid: this.newLineUuid,
        neighbourUuid: this.neighbourUuid,
        placement: this.placement
      }
    }
  }
}


export class InitBoxServerCommand extends ServerCommand {
  private newBoxUuid: string;
  private newLineUuid: string;
  private neighbourUuid: string;
  private placement: Placement;
  constructor(newBoxUuid: string, newLineUuid: string, neighbourUuid: string, placement: Placement) {
    super();
    this.newBoxUuid = newBoxUuid;
    this.newLineUuid = newLineUuid;
    this.neighbourUuid = neighbourUuid;
    this.placement = placement;
  }

  marshall (): TInitBoxServerCommand {
    return {
      commandName: "initBox",
      options: {
        newBoxUuid: this.newBoxUuid,
        newLineUuid: this.newLineUuid,
        neighbourUuid: this.neighbourUuid,
        placement: this.placement
      }
    }
  }
}


export class RemoveStepServerCommand extends ServerCommand {
  private uuid: string;
  constructor(uuid: string) {
    super();
    this.uuid = uuid;
  }

  marshall (): TRemoveStepServerCommand {
    return {
      commandName: "removeStep",
      options: {
        uuid: this.uuid
      }
    }
  }
}


export class UpdateLineServerCommand extends ServerCommand {
  private lineUuid: string;
  private formula: string | null;
  private rule: string | null;
  private refs: string[] | null;
  constructor(lineUuid: string, formula: string | null, rule: string | null, refs: string[] | null) {
    super();
    this.lineUuid = lineUuid;
    this.formula = formula;
    this.rule = rule;
    this.refs = refs;
  }

  marshall (): TUpdateLineServerCommand {
    return {
      commandName: "updateLine",
      options: {
        lineUuid: this.lineUuid,
        formula: this.formula,
        rule: this.rule,
        refs: this.refs
      }
    }
  }
}