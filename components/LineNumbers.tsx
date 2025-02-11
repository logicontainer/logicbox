export function LineNumbers () {
  const lineCount = 5
  return (
    <div className="flex-col items-start">
      {[...Array(5)].map((e, i) =>
        <p key={i} className="text-sm/10 text-left text-gray-800 align-baseline">{i}.</p>)}
    </div>
  )
}