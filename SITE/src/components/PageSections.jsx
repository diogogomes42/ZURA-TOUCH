import { ArrowRight, Check, ChevronDown } from "lucide-react"
import { Button } from "./ui/button"
import { cn } from "../lib/utils"

const cardClass = "rounded-2xl border border-purple-500/20 bg-[rgba(30,27,53,0.5)] p-6"

export function CardGridSection({ title, items, columns = "md:grid-cols-3", bordered = false, className }) {
  return (
    <section
      className={cn(
        "px-4 sm:px-6 lg:px-8 py-12 sm:py-16",
        bordered && "border-t border-purple-500/10",
        className
      )}
    >
      <div className="mx-auto max-w-6xl">
        <h2 className="text-2xl sm:text-3xl font-bold text-white">{title}</h2>
        <div className={cn("mt-8 grid gap-6", columns)}>
          {items.map((item) =>
            item.href ? (
              <a
                key={item.title}
                href={item.href}
                className="group rounded-2xl border border-dashed border-purple-400/50 bg-gradient-to-br from-purple-600/20 to-pink-500/10 p-6 transition-colors hover:border-purple-300/80 hover:from-purple-600/30"
              >
                <h3 className="text-lg font-semibold text-white">{item.title}</h3>
                <p className="mt-2 text-sm text-[#c4c1d6] leading-relaxed">{item.description}</p>
                <span className="mt-4 inline-flex items-center gap-1.5 text-sm font-semibold text-purple-300 group-hover:text-purple-200">
                  {item.cta}
                  <ArrowRight size={16} className="transition-transform group-hover:translate-x-0.5" />
                </span>
              </a>
            ) : (
              <div key={item.title} className={cardClass}>
                <h3 className="text-lg font-semibold text-white">{item.title}</h3>
                <p className="mt-2 text-sm text-[#c4c1d6] leading-relaxed">{item.description}</p>
              </div>
            )
          )}
        </div>
      </div>
    </section>
  )
}

export function FaqSection({ title, items, bordered = false }) {
  return (
    <section className={cn("px-4 sm:px-6 lg:px-8 py-12 sm:py-16", bordered && "border-t border-purple-500/10")}>
      <div className="mx-auto max-w-6xl">
        <h2 className="text-center text-2xl sm:text-3xl font-bold text-white">{title}</h2>
        <div className="mt-10 w-full space-y-3">
          {items.map((item) => (
            <details key={item.question} className={cn(cardClass, "group p-0 open:border-purple-400/40")}>
              <summary className="flex cursor-pointer list-none items-center justify-between gap-4 p-5 sm:p-6 [&::-webkit-details-marker]:hidden">
                <h3 className="text-base sm:text-lg font-semibold text-white">{item.question}</h3>
                <ChevronDown
                  size={20}
                  className="shrink-0 text-purple-300 transition-transform duration-200 group-open:rotate-180"
                  aria-hidden
                />
              </summary>
              <p className="px-5 sm:px-6 pb-5 sm:pb-6 -mt-1 text-sm sm:text-base text-[#c4c1d6] leading-relaxed">
                {item.answer}
              </p>
            </details>
          ))}
        </div>
      </div>
    </section>
  )
}

export function ImageShowcase({ items }) {
  return (
    <section className="px-4 sm:px-6 lg:px-8 pb-4 sm:pb-6">
      <div className="mx-auto max-w-6xl grid md:grid-cols-2 gap-5 sm:gap-6">
        {items.map((item) => (
          <figure
            key={item.src}
            className="group relative overflow-hidden rounded-3xl border border-purple-500/25 bg-[rgba(30,27,53,0.5)] shadow-2xl shadow-purple-950/40"
          >
            <img
              src={item.src}
              alt={item.alt}
              loading="lazy"
              decoding="async"
              className="aspect-[16/10] w-full object-cover transition-transform duration-700 ease-out group-hover:scale-[1.03]"
            />
            <div className="pointer-events-none absolute inset-0 bg-gradient-to-t from-[#12102a] via-[#12102a]/30 to-transparent" aria-hidden />
            <figcaption className="absolute inset-x-0 bottom-0 p-5 sm:p-7">
              <p className="text-lg sm:text-xl font-semibold text-white">{item.title}</p>
              <p className="mt-1 text-sm text-[#c4c1d6]">{item.description}</p>
            </figcaption>
          </figure>
        ))}
      </div>
    </section>
  )
}

export function SplitCardSection({ title, items, image, imageAlt, bordered = false }) {
  return (
    <section className={cn("px-4 sm:px-6 lg:px-8 py-12 sm:py-16", bordered && "border-t border-purple-500/10")}>
      <div className="mx-auto max-w-6xl grid lg:grid-cols-[minmax(0,1.4fr)_minmax(0,1fr)] gap-10 lg:gap-16 items-center">
        <div>
          <h2 className="text-2xl sm:text-3xl font-bold text-white">{title}</h2>
          <div className="mt-8 grid sm:grid-cols-2 gap-6">
            {items.map((item) => (
              <div key={item.title} className={cardClass}>
                <h3 className="text-lg font-semibold text-white">{item.title}</h3>
                <p className="mt-2 text-sm text-[#c4c1d6] leading-relaxed">{item.description}</p>
              </div>
            ))}
          </div>
        </div>
        <div className="relative flex justify-center">
          <div
            className="absolute inset-0 -m-6 rounded-full blur-3xl pointer-events-none bg-[radial-gradient(circle,rgba(168,85,247,0.3)_0%,rgba(236,72,153,0.12)_45%,transparent_70%)]"
            aria-hidden
          />
          <img
            src={image}
            alt={imageAlt}
            loading="lazy"
            decoding="async"
            className="relative z-10 w-full max-w-xs sm:max-w-sm max-h-[420px] object-contain drop-shadow-[0_40px_80px_rgba(0,0,0,0.8)]"
          />
        </div>
      </div>
    </section>
  )
}

export function PurposeSection({ title, items, bordered = false }) {
  return (
    <section className={cn("px-4 sm:px-6 lg:px-8 py-12 sm:py-16", bordered && "border-t border-purple-500/10")}>
      <div className="mx-auto max-w-6xl">
        <h2 className="text-2xl sm:text-3xl font-bold text-white">{title}</h2>
        <div className="mt-8 grid md:grid-cols-2 gap-6">
          {items.map((item, i) => (
            <div key={item.title} className={cn(cardClass, "p-6 sm:p-8")}>
              <span className="inline-flex items-center gap-2 rounded-full border border-purple-500/30 bg-purple-500/10 px-3 py-1 text-xs font-semibold uppercase tracking-wider text-purple-300">
                {String(i + 1).padStart(2, "0")} · {item.label}
              </span>
              <h3 className="mt-5 text-xl sm:text-2xl font-bold text-white">{item.title}</h3>
              <p className="mt-3 text-sm sm:text-base text-[#c4c1d6] leading-relaxed">{item.description}</p>
              <ul className="mt-6 space-y-3">
                {item.points.map((point) => (
                  <li key={point} className="flex items-start gap-3 text-sm sm:text-base text-[#c4c1d6]">
                    <Check size={18} className="shrink-0 text-purple-400 mt-0.5" />
                    <span>{point}</span>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

export function StepsSection({ title, steps, bordered = false }) {
  return (
    <section className={cn("px-4 sm:px-6 lg:px-8 py-12 sm:py-16", bordered && "border-t border-purple-500/10")}>
      <div className="mx-auto max-w-6xl">
        <h2 className="text-2xl sm:text-3xl font-bold text-white">{title}</h2>
        <div className="mt-8 grid md:grid-cols-3 gap-6">
          {steps.map((step, i) => (
            <div key={step.title} className={cardClass}>
              <span className="inline-flex w-8 h-8 items-center justify-center rounded-full bg-gradient-to-r from-purple-500 to-pink-500 text-sm font-bold text-white">
                {i + 1}
              </span>
              <h3 className="mt-4 text-lg font-semibold text-white">{step.title}</h3>
              <p className="mt-2 text-sm text-[#c4c1d6] leading-relaxed">{step.description}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

export function CheckListSection({ title, points, bordered = false }) {
  return (
    <section className={cn("px-4 sm:px-6 lg:px-8 py-12 sm:py-16", bordered && "border-t border-purple-500/10")}>
      <div className="mx-auto max-w-6xl">
        <h2 className="text-2xl sm:text-3xl font-bold text-white">{title}</h2>
        <ul className="mt-8 grid sm:grid-cols-2 gap-x-10 gap-y-4">
          {points.map((point) => (
            <li key={point} className="flex items-start gap-3 text-[#c4c1d6]">
              <Check size={20} className="shrink-0 text-purple-400 mt-0.5" />
              <span>{point}</span>
            </li>
          ))}
        </ul>
      </div>
    </section>
  )
}

export function CtaPanel({ title, lead, href, label }) {
  return (
    <section className="px-4 sm:px-6 lg:px-8 py-12 sm:py-16 pb-16 sm:pb-20 lg:pb-24">
      <div className="mx-auto max-w-6xl rounded-3xl border border-purple-500/30 bg-gradient-to-br from-purple-500/15 via-[rgba(30,27,53,0.7)] to-pink-500/10 p-8 sm:p-12 text-center">
        <h2 className="text-2xl sm:text-3xl lg:text-4xl font-bold tracking-tight text-white">{title}</h2>
        <p className="mt-4 text-base sm:text-lg text-[#c4c1d6] max-w-2xl mx-auto leading-relaxed">{lead}</p>
        <Button asChild variant="primary" size="lg" className="mt-8 px-10 font-bold neon-glow">
          <a href={href}>{label}</a>
        </Button>
      </div>
    </section>
  )
}
