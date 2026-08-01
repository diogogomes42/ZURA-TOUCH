export function Marquee({ text = "SMART VENDING ✦ INTERACTIVE ✦ REVENUE ✦ NIGHTLIFE ✦", className = "" }) {
  const content = `${text} `
  const repeated = content.repeat(12)
  return (
    <div className={`overflow-hidden border-y border-purple-500/10 py-3 bg-purple-500/5 ${className}`}>
      <div className="marquee-track flex w-max gap-24">
        <span className="inline-block shrink-0 text-sm font-semibold uppercase tracking-[0.25em] text-purple-400/90">
          {repeated}
        </span>
        <span className="inline-block shrink-0 text-sm font-semibold uppercase tracking-[0.25em] text-purple-400/90" aria-hidden>
          {repeated}
        </span>
      </div>
    </div>
  )
}
