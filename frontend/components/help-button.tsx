import { QuestionMarkIcon } from "@radix-ui/react-icons";
import { Button } from "./ui/button";
import Link from "next/link";

export default function HelpButton() {
  return (
    <Button variant={"outline"}>
      <Link href={"/help-page.html"}>
        <QuestionMarkIcon />
      </Link>
    </Button>
  )
}
