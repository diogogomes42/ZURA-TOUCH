import { Button } from "./ui/button"
import { ArrowRight } from "lucide-react"
import { useScrollAnimation } from "../lib/useScrollAnimation"
import { useLanguage } from "../i18n/LanguageContext"

export function AudienceCards() {
  const [ref, isVisible] = useScrollAnimation(0.1)
  const { t } = useLanguage()

  const cards = [
    { key: "venues", href: "/espacos/", accent: "from-purple-500/20 to-purple-500/5" },
    { key: "brands", href: "/marcas/", accent: "from-pink-500/20 to-pink-500/5" },
  ]

  return (
    <section className="py-16 sm:py-20 lg:py-24 px-4 sm:px-6 lg:px-8">
      <div ref={ref} className={`mx-auto max-w-6xl ${isVisible ? "animate-in" : "opacity-0"}`}>
        <div className="text-center mb-12">
          <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white">
            {t("audienceCards.title")}
          </h2>
          <p className="mt-4 text-lg text-[#c4c1d6] max-w-2xl mx-auto">
            {t("audienceCards.subtitle")}
          </p>
        </div>

        <div className="grid md:grid-cols-2 gap-6 lg:gap-8">
          {cards.map(({ key, href, accent }) => {
            const card = t(`audienceCards.${key}`)
            return (
              <div
                key={key}
                className={`rounded-2xl border border-purple-500/20 bg-gradient-to-br ${accent} p-8 sm:p-10 flex flex-col`}
              >
                <span className="text-[11px] font-semibold uppercase tracking-[0.2em] text-purple-300">
                  {card.badge}
                </span>
                <h3 className="mt-4 text-2xl sm:text-3xl font-bold text-white">{card.title}</h3>
                <p className="mt-4 text-[#c4c1d6] leading-relaxed flex-1">{card.description}</p>
                <Button asChild variant="primary" size="lg" className="mt-8 w-full sm:w-auto font-bold">
                  <a href={href} className="inline-flex items-center gap-2">
                    {card.cta}
                    <ArrowRight size={18} />
                  </a>
                </Button>
              </div>
            )
          })}
        </div>
      </div>
    </section>
  )
}
