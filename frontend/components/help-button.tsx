import { QuestionMarkIcon } from "@radix-ui/react-icons";
import { Button } from "./ui/button";
import Link from "next/link";

export default function HelpButton({ className }: { className?: string}) {
  return (
    <Button variant={"outline"} className={className}>
      <QuestionMarkIcon className="h-4 w-4"/>
    </Button>
  )
}
