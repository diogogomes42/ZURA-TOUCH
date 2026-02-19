import { useScrollAnimation } from "../lib/useScrollAnimation"
import { Button } from "./ui/button"
import { Handshake, Megaphone, Building2 } from "lucide-react"
import { useLanguage } from "../i18n/LanguageContext"

const icons = [Handshake, Megaphone, Building2]

export function FeaturePromos() {
  const [ref, isVisible] = useScrollAnimation(0.1)
  const { t } = useLanguage()
  const promos = t("featurePromos.items")

  return (
    <section className="py-16 sm:py-20 lg:py-24 px-4 sm:px-6 lg:px-8 relative overflow-hidden">
      <div ref={ref} className={`mx-auto max-w-6xl ${isVisible ? "animate-in" : "opacity-0"}`}>
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-5 sm:gap-6 md:gap-8 lg:gap-10 [&>*:nth-child(3)]:justify-self-center md:[&>*:nth-child(3)]:justify-self-stretch">
          {promos.map((promo, i) => {
            const Icon = icons[i]
            return (
              <div
                key={promo.badge}
                className="group relative p-6 sm:p-8 rounded-2xl border border-purple-500/20 bg-gradient-to-br from-purple-500/10 to-transparent hover:border-purple-500/40 hover:from-purple-500/15 transition-all duration-300 active:scale-[0.99] sm:hover:scale-[1.02]"
              >
                <p className="text-xs font-bold uppercase tracking-[0.2em] text-purple-400/90">
                  {promo.badge}
                </p>
                <div className="mt-4 flex h-12 w-12 items-center justify-center rounded-xl bg-purple-500/20 text-purple-400 group-hover:bg-purple-500/30 transition-colors">
                  <Icon size={24} strokeWidth={2} />
                </div>
                <h3 className="mt-6 text-xl font-bold text-white">{promo.title}</h3>
                <p className="mt-3 text-sm text-[#c4c1d6]">{promo.description}</p>
                <Button asChild variant="primary" size="sm" className="mt-6">
                  <a href="#contact">{promo.cta} →</a>
                </Button>
              </div>
            )
          })}
        </div>
      </div>
    </section>
  )
}
