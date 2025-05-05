"use client";

import { createContext, useContext, useRef, useState } from "react";

export interface CurrentProofIdProps {
  proofId: string;
  setProofId: (proofId: string) => void;
}

const CurrentProofIdContext = createContext<CurrentProofIdProps | null>(null);

export const CurrentProofIdProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const [proofId, setProofId] = useState<string>("");

  return (
    <CurrentProofIdContext.Provider value={{ proofId, setProofId }}>
      {children}
    </CurrentProofIdContext.Provider>
  );
};

export const useCurrentProofId = () => {
  const ctx = useContext(CurrentProofIdContext);
  if (!ctx)
    throw new Error(
      "useCurrentProofId must be used within CurrentProofIdProvider"
    );
  return ctx;
};
