import React, { useState } from "react";

import { Command } from "@/lib/commands";
import { useProof } from "./ProofProvider";

interface HistoryContextProps {
  history: Command[];
  now: number;
  canUndo: boolean;
  canRedo: boolean;
  addToHistory: (command: Command) => unknown;
  undo: () => unknown;
  redo: () => unknown;
}

const HistoryContext = React.createContext<HistoryContextProps>({
  history: [],
  now: 0,
  canUndo: false,
  canRedo: false,
  addToHistory: () => {
    throw new Error("");
  },
  undo: () => {},
  redo: () => {},
});

export function useHistory() {
  const context = React.useContext(HistoryContext);
  if (!context) {
    throw new Error("useHistory must be used within a HistoryProvider");
  }
  return context;
}

export function HistoryProvider({ children }: React.PropsWithChildren<object>) {
  const proofContext = useProof();

  const [history, setHistory] = useState<Command[]>([]);
  const [now, setNow] = useState<number>(0);

  React.useEffect(() => {
    // clear the history if the proof id changes
    setHistory([])
    setNow(0)
  }, [proofContext.proof.id])

  const addToHistory = (command: Command, deferExecution: boolean = false) => {
    const newHistory = [...history.slice(0, now), command];
    if (!deferExecution) {
      executeStep(newHistory);
    }
    setHistory(newHistory);
  };

  const executeStep = (history: Command[]) => {
    if (now + 1 <= history.length) {
      const step = history[now];
      console.log("Executing step", step);
      step.execute(proofContext);
      setNow(now + 1);
    }
  };

  const undo = () => {
    if (now - 1 >= 0) {
      history[now - 1].undo(proofContext);
      setNow(now - 1);
    }
  };

  const redo = () => {
    executeStep(history);
  };

  const canUndo = now != 0;
  const canRedo = now != history.length;

  return (
    <HistoryContext.Provider
      value={{ history, now, canUndo, canRedo, addToHistory, undo, redo }}
    >
      {children}
    </HistoryContext.Provider>
  );
}
