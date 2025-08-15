import React from "react";
import { useProofStore } from "@/store/proofStore";
import AutosizeInput from "react-input-autosize";
import { cn } from "@/lib/utils";

export function EditableProofTitle({
  proofId
}: {
  proofId: string
}) {
  const title = useProofStore(state => state.getProof)(proofId)?.title ?? null
  const updateProofTitle = useProofStore(state => state.updateProofTitle)

  // this is `null` iff the title is not being edited
  const [inputFieldValue, setInputFieldValue] = React.useState<string | null>(null)

  const ref = React.useRef<HTMLInputElement>(null);

  if (!title) {
    console.warn(`Couldn't get title of proof with id ${proofId}`)
    return null;
  }

  const onBlur = (_: React.FocusEvent<HTMLInputElement>) => {
    if (inputFieldValue)
      updateProofTitle(proofId, inputFieldValue)

    setInputFieldValue(null)
  }

  const handleInputRefChange = (r: HTMLInputElement | null) => {
    ref.current = r;
  };

  return <AutosizeInput
    inputRef={handleInputRefChange}
    value={inputFieldValue ?? title}
    onClick={e => e.stopPropagation()}
    onChange={e => setInputFieldValue(e.target.value)}
    onBlur={onBlur}
    onKeyDown={e => {
      if (e.key === "Enter") {
        ref.current?.blur()
      }
    }}
    placeholder="Proof title..."
    className="text-lg/5 overflow-auto min-h-5"
    inputClassName={cn(
      "py-[2px] rounded bg-transparent font-bold outline-none focus:underline",
      inputFieldValue && "underline",
    )}
  />
}
