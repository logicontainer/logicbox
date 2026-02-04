import type { Proof, ProofWithMetadata } from "../types/types";
import { createJSONStorage, persist } from "zustand/middleware";

import { create } from "zustand";
import jsonExamples from "@/public/examples.json"

import { v4 as uuidv4 } from "uuid";

// TODO: something better than type coercion?
const proofExamples: ProofWithMetadata[] = jsonExamples as ProofWithMetadata[]

type ProofStore = {
  proofs: ProofWithMetadata[];
  addProof: (proof: ProofWithMetadata) => void;
  addProofWithFreshId: (proof: Omit<ProofWithMetadata, "id">) => void;
  updateProofContent: (id: string, updater: (_: Proof) => Proof) => void;
  updateProofTitle: (id: string, title: string) => void;
  deleteProof: (id: string) => void;
  getProof: (id: string) => ProofWithMetadata | undefined;
  clearAll: () => void;
};

export const useProofStore = create<ProofStore>()(
  persist(
    (set, get) => ({
      proofs: proofExamples.map(proof => ({ ...proof, id: uuidv4() })),

      addProof: (proof) =>
        set((state) => {
          if (state.proofs.some(p => p.id == proof.id)) {
            throw new Error("Attempting to add proof with existing id")
          }
          return { proofs: [...state.proofs, proof], }
        }),

      addProofWithFreshId: (proof) => {
        set((state) => {
          return { proofs: [...state.proofs, { ... proof, id: uuidv4() }], }
        })
      },

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
