import type { Proof, ProofWithMetadata } from "../types/types";
import { createJSONStorage, persist } from "zustand/middleware";

import { create } from "zustand";
import jsonExamples from "@/examples/examples.json"

// TODO: something better than type coercion?
const proofExamples: ProofWithMetadata[] = jsonExamples as ProofWithMetadata[]

type ProofStore = {
  proofs: ProofWithMetadata[];
  addProof: (proof: ProofWithMetadata) => void;
  updateProofContent: (id: string, updater: (_: Proof) => Proof) => void;
  updateProofTitle: (id: string, title: string) => void;
  deleteProof: (id: string) => void;
  getProof: (id: string) => ProofWithMetadata | undefined;
  clearAll: () => void;
};

export const useProofStore = create<ProofStore>()(
  persist(
    (set, get) => ({
      proofs: proofExamples,

      addProof: (proof) =>
        set((state) => ({
          proofs: [...state.proofs, proof],
        })),

      updateProofContent: (id, updater) =>
        set((state) => {
          const existing = state.proofs.find((proof) => proof.id === id);
          if (!existing) return state;
          const proofs = state.proofs.map((proof) => {
            if (proof.id === id) {
              return { ...proof, proof: updater(proof.proof) };
            }
            return proof;
          });
          return {
            proofs,
          };
        }),

      updateProofTitle: (id, title) =>
        set((state) => {
          const existing = state.proofs.find((proof) => proof.id === id);
          if (!existing) return state;
          const proofs = state.proofs.map((proof) => {
            if (proof.id === id) {
              return { ...proof, title };
            }
            return proof;
          });
          return {
            proofs,
          };
        }),

      deleteProof: (id) =>
        set((state) => {
          const rest = state.proofs.filter((proof) => proof.id !== id);
          return { proofs: rest };
        }),

      getProof: (id) => get().proofs.find((proof) => proof.id === id),

      clearAll: () => set(() => ({ proofs: [] })),
    }),
    {
      name: "proofs-storage",
      storage: createJSONStorage(() => localStorage),
      // partialize: (state) => ({ proofs: state.proofs }),
    },
  ),
);
