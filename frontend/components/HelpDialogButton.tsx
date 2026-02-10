import React from "react"
import { Pagination, PaginationContent, PaginationItem, PaginationLink, PaginationEllipsis, PaginationPrevious, PaginationNext } from "./ui/pagination"
import { Carousel, CarouselApi, CarouselContent, CarouselItem } from "./ui/carousel"
import { Button } from "./ui/button"
import { HelpCircleIcon } from "lucide-react"
import { Sheet, SheetContent, SheetFooter, SheetHeader, SheetTitle, SheetTrigger } from "./ui/sheet"
import { InlineMath } from "react-katex"

function HelpTitle({ children }: { children: React.ReactNode }) {
  return <h2 className="text-lg font-semibold">{children}</h2>
}

function HelpVideo({ src }: { src: string }) {
  return <video src={src} autoPlay loop className="rounded-lg"/>
}

function HelpDescription({ children }: { children: React.ReactNode }) {
  return <div>{children}</div>
}

function HelpPagination({
  count,
  current,
  scrollTo
}: {
  count: number,
  current: number,
  scrollTo: (_: number) => void
}) {
  const item = (idx: number) => <PaginationItem key={`pag-item-${idx}`}>
    <PaginationLink onClick={_ => scrollTo(idx)} isActive={current === idx}>{idx + 1}</PaginationLink>
  </PaginationItem>

  return <Pagination>
    <PaginationContent>
      <PaginationItem>
        <PaginationPrevious onClick={_ => scrollTo(current - 1)}/>
      </PaginationItem>
      {(() => {
        switch (current) {
          case 0: case 1: case 2: return <>
            {Array.from({ length: 4 }).map((_, idx) => item(idx))}
            <PaginationItem key={`pag-elippsis-${current}`}><PaginationEllipsis/></PaginationItem>
          </>
            
          case count - 3: case count - 2: case count - 1: return <>
            <PaginationItem key={`pag-elippsis-${current}`}><PaginationEllipsis/></PaginationItem>
            {Array.from({ length: 4 }).map((_, idx) => item(count - idx - 1)).reverse()}
          </>

          default:
            return <>
            <PaginationItem key={`pag-elippsis-${current}-1`}><PaginationEllipsis/></PaginationItem>
            {Array.from({ length: 3 }).map((_, idx) => item(current + idx - 1))}
            <PaginationItem key={`pag-elippsis-${current}-2`}><PaginationEllipsis/></PaginationItem>
            </>
        }
      })()}
      <PaginationItem>
        <PaginationNext onClick={_ => scrollTo(current + 1)}/>
      </PaginationItem>
    </PaginationContent>
  </Pagination>
}

function HelpCarousel({ selectedPage, pages }: { selectedPage: number, pages: React.ReactNode[] }) {
  const [api, setApi] = React.useState<CarouselApi | undefined>(undefined)
  const [currentlyShownPage, setCurrentlyShownPage] = React.useState<number>(0)

  React.useEffect(() => {
    if (!api) return

    if (selectedPage !== currentlyShownPage) {
      api.scrollTo(selectedPage)
    }

    api.on("select", () => {
      setCurrentlyShownPage(api.selectedScrollSnap())
    })
  }, [api, selectedPage, currentlyShownPage])

  return <Carousel 
    setApi={setApi} 
    className="w-[calc(min(650px,100vw)-50px)]"
  >
    <CarouselContent >
      {pages.map((p, idx) => 
        <CarouselItem key={`help-page-${idx}`}>
          <div className="flex flex-col gap-1 p-4">
            {p}
          </div>
        </CarouselItem>)
      }
    </CarouselContent>
  </Carousel>
}

const HELP_PAGES = [
  <>
    <HelpTitle>Create a new proof</HelpTitle>
    <HelpVideo src="assets/videos/13. create-proof.mp4"/>
    <HelpDescription>
      Create a new proof by clicking on the ➕ icon on the front page. Then enter the name of the proof, and the type of logic the proof is in.

      <table className="table-auto text-xs mt-4">
        <thead>
          <tr>
            <th className="border px-4 py-2">Type of logic</th>
            <th className="border px-4 py-2">Description</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td className="border px-4 py-2">Propositional logic</td>
            <td className="border px-4 py-2">with atomic formulas <InlineMath math="p, q, r, \dots"/> and the logical connectives <InlineMath math="\land, \lor"/> and <InlineMath math="\rightarrow"/></td>
          </tr>
          <tr>
            <td className="border px-4 py-2">Propositional logic</td>
            <td className="border px-4 py-2">with quantifiers <InlineMath math="\forall, \exists"/>, predicates <InlineMath math="P, Q, \dots"/>, functions <InlineMath math="f(x, y), g(z)"/> and equality <InlineMath math="x_0 = y"/></td>
          </tr>
          <tr>
            <td className="border px-4 py-2">Arithmetic</td>
            <td className="border px-4 py-2">with addition <InlineMath math="+"/>, multiplication <InlineMath math="*"/>, <InlineMath math="0"/> and <InlineMath math="1"/></td>
          </tr>
        </tbody>
      </table>

      Note: when a proof is created, it contains a single line with no formula or rule specified
    </HelpDescription>
  </>,
  <>
    <HelpTitle>Add a line</HelpTitle>
    <HelpVideo src="assets/videos/01. add-line.mp4"/>
    <HelpDescription>
      Add a line to your proof by right-clicking on an existing step and clicking on ⬆️ to add a line above or on ⬇️ to add a line below.
    </HelpDescription>
  </>,
  <>
    <HelpTitle>Enter a formula</HelpTitle>
    <HelpVideo src="assets/videos/02. edit-formula.mp4"/>
    <HelpDescription>
      Modify the formula of a line by double-clicking on it, and entering the new value.
      <br/><br/>
      The full syntax is described <a className="underline text-blue-950" href="https://github.com/logicontainer/logicbox-howto#syntax" target="_blank">here</a>. Some examples are
      <table className="table-auto text-xs mt-4">
        <thead>
          <tr>
            <th className="border px-4 py-2">Code</th>
            <th className="border px-4 py-2">Parsed As</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td className="border px-4 py-2"><code className="bg-accent p-1 rounded">p and r -&gt; q or !s</code></td>
            <td className="border px-4 py-2"><InlineMath math="(p \land r) \rightarrow (q \lor (\lnot s))"/></td>
          </tr>
          <tr>
            <td className="border px-4 py-2"><code className="bg-accent p-1 rounded">forall x exists y (P(x, y) or x = y)</code></td>
            <td className="border px-4 py-2"><InlineMath math="\forall x \exists y (P(x, y) \lor x = y)"/></td>
          </tr>
          <tr>
            <td className="border px-4 py-2"><code className="bg-accent p-1 rounded">forall n (n = 0 or exists k n = k + 1)</code></td>
            <td className="border px-4 py-2"><InlineMath math="\forall n (n = 0 \lor \exists k (n = k + 1))"/></td>
          </tr>
        </tbody>
      </table>
    </HelpDescription>
  </>,
  <>
    <HelpTitle>Choose a rule</HelpTitle>
    <HelpVideo src="assets/videos/03. choose-rule.mp4"/>
    <HelpDescription>
      Choose a rule by clicking on the rule and selecting a new one from the side-panel. By hovering on a rule, you may see its definition.
    </HelpDescription>
  </>,
  <>
    <HelpTitle>Pick lines to refer to</HelpTitle>
    <HelpVideo src="assets/videos/04. pick-refs.mp4"/>
    <HelpDescription>
      To refer to a line, click on a reference, then click on the line/box which you would like to refer to.
    </HelpDescription>
  </>,
  <>
    <HelpTitle>Inspect errors</HelpTitle>
    <HelpVideo src="assets/videos/05. inspect-single-line.mp4"/>
    <HelpDescription>
      You may inspect the errors currently on a line/box by clicking on it, and viewing the errors in the side-panel.
      If no line/box is currently selected, the errors pertaining to the currently hovered element will be shown.
    </HelpDescription>
    <HelpVideo src="assets/videos/06. inspect-multiple-lines.mp4"/>
  </>,
  <>
    <HelpTitle>Add a box</HelpTitle>
    <HelpVideo src="assets/videos/07. add-box.mp4"/>
    <HelpDescription>
      Add a box to the proof by right-clicking on an existing step and clicking on ⬆️ to add a box above or on ⬇️ to add a box below.
    </HelpDescription>
  </>,
  <>
    <HelpTitle>Remove a step</HelpTitle>
    <HelpVideo src="assets/videos/08. delete-line.mp4"/>
    <HelpDescription>
      You may remove a line by right-clicking on it and selecting {"'"}Delete{"'"}.
      If you remove a box, you will remove all steps it contains.
    </HelpDescription>
    <HelpVideo src="assets/videos/09. delete-box.mp4"/>
  </>,
  <>
    <HelpTitle>Move a step</HelpTitle>
    <HelpVideo src="assets/videos/10. move-line.mp4"/>
    <HelpDescription>
      You can move a line/box by dragging it to its new location.
    </HelpDescription>
  </>,
  <>
    <HelpTitle>Edit fresh variable in a box (only in predicate logic/arithmetic)</HelpTitle>
    <HelpVideo src="assets/videos/11. edit-fresh-var-ctx-menu.mp4"/>
    <HelpDescription>
      You may add/edit a fresh variable by right-clicking on a box and choosing {"'"}Edit fresh variable{"'"}.
      Alternatively, you can double-click on the box.
    </HelpDescription>
    <HelpVideo src="assets/videos/12. edit-fresh-var-dbl-click.mp4"/>
  </>,
]

export function HelpDialogButton({ className }: { className?: string }) {
  const [selectedPage, setSelectedPage] = React.useState(0)

  const scrollTo = (idx: number) => {
    if (idx >= 0 && idx < HELP_PAGES.length) 
      setSelectedPage(idx)
  }

  return (
    <Sheet>
      <SheetTrigger asChild>
        <Button variant="outline" className={className}>
          <HelpCircleIcon className="h-4 w-4" />
        </Button>
      </SheetTrigger>
      <SheetContent 
        className="min-w-[calc(min(650px,100vw))] flex flex-col"
        onKeyDown={e => {
          switch (e.key) {
            case "ArrowLeft": scrollTo(selectedPage - 1); break
            case "ArrowRight": scrollTo(selectedPage + 1); break
          }
        }}
      >
        <SheetHeader>
          <SheetTitle>How to use LogicBox</SheetTitle>
        </SheetHeader>
        
        <div className="flex-1 overflow-auto">
          <HelpCarousel pages={HELP_PAGES} selectedPage={selectedPage}/>
        </div>

        <SheetFooter className="flex justify-center pt-4">
          <HelpPagination
            current={selectedPage}
            count={HELP_PAGES.length}
            scrollTo={scrollTo}
          />
        </SheetFooter>
      </SheetContent>
    </Sheet>
  )
}
