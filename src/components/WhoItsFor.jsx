import { useScrollAnimation } from "../lib/useScrollAnimation"
import { Wine, ShoppingBag, Heart, Bus, PartyPopper } from "lucide-react"
import { useLanguage } from "../i18n/LanguageContext"

const icons = [Wine, ShoppingBag, Heart, Bus, PartyPopper]

export function WhoItsFor() {
  const [ref, isVisible] = useScrollAnimation(0.1)
  const { t } = useLanguage()
  const audiences = t("whoItsFor.audiences")

  return (
    <section id="who-its-for" className="py-16 sm:py-20 lg:py-24 px-4 sm:px-6 lg:px-8">
      <div ref={ref} className={`mx-auto max-w-6xl ${isVisible ? "animate-in" : "opacity-0"}`}>
        <div className="text-center mb-10 sm:mb-16">
          <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white">
            {t("whoItsFor.title")}
          </h2>
          <p className="mt-4 text-lg text-[#c4c1d6] max-w-2xl mx-auto">
            {t("whoItsFor.subtitle")}
          </p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-4 sm:gap-5 lg:gap-6 [&>*:nth-child(5)]:justify-self-center lg:[&>*:nth-child(5)]:justify-self-stretch">
          {audiences.map((audience, i) => {
            const Icon = icons[i]
            return (
              <div
                key={audience.title}
                className="p-5 sm:p-6 rounded-2xl border border-purple-500/20 bg-[rgba(30,27,53,0.6)] hover:border-purple-500/30 hover:bg-purple-500/5 transition-all duration-300 active:scale-[0.99] sm:hover:scale-[1.02] backdrop-blur-sm"
              >
                <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-purple-500/20 text-purple-400">
                  <Icon size={24} strokeWidth={2} />
                </div>
                <h3 className="mt-4 text-lg font-semibold text-white">{audience.title}</h3>
                <p className="mt-1 text-xs font-medium uppercase tracking-wider text-purple-400/80">
                  {audience.subtitle}
                </p>
                <p className="mt-2 text-sm text-[#c4c1d6]">{audience.description}</p>
              </div>
            )
          })}
        </div>
      </div>
    </section>
  )
}
