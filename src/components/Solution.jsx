import { useScrollAnimation } from "../lib/useScrollAnimation"
import { Sparkles, Gamepad2, DollarSign } from "lucide-react"
import { useLanguage } from "../i18n/LanguageContext"

const icons = [Sparkles, Gamepad2, DollarSign]

export function Solution() {
  const [ref, isVisible] = useScrollAnimation(0.1)
  const { t } = useLanguage()
  const pillars = t("solution.items")

  return (
    <section id="solution" className="py-16 sm:py-20 lg:py-24 px-4 sm:px-6 lg:px-8 relative overflow-hidden">
      <div ref={ref} className={`mx-auto max-w-6xl ${isVisible ? "animate-in" : "opacity-0"}`}>
        <div className="text-center mb-10 sm:mb-16">
          <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white">
            {t("solution.title")}
          </h2>
          <p className="mt-4 text-lg text-[#c4c1d6] max-w-2xl mx-auto">
            {t("solution.subtitle")}
          </p>
        </div>

        <div className="flex flex-col lg:flex-row gap-8 sm:gap-12 lg:gap-20 items-center">
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-5 sm:gap-8 md:gap-10 flex-1 w-full [&>*:nth-child(3)]:justify-self-center md:[&>*:nth-child(3)]:justify-self-stretch">
            {pillars.map((pillar, i) => {
              const Icon = icons[i]
              return (
                <div
                  key={pillar.title}
                  className="icon-glow group p-6 sm:p-8 rounded-2xl border border-purple-500/20 bg-purple-500/5 hover:border-purple-500/35 transition-all duration-300 active:scale-[0.99] sm:hover:scale-[1.02]"
                >
                  <div className="icon-glow-target flex h-14 w-14 items-center justify-center rounded-xl bg-gradient-to-br from-purple-500/30 to-pink-500/30 text-purple-300 transition-shadow duration-300">
                    <Icon size={28} strokeWidth={2} />
                  </div>
                  <h3 className="mt-6 text-xl font-semibold text-white">{pillar.title}</h3>
                  <p className="mt-3 text-[#c4c1d6]">{pillar.description}</p>
                </div>
              )
            })}
          </div>
          <div className="flex justify-center lg:shrink-0 lg:items-center">
            <img
              src="/assets/mascot/thumbs-up-mascot.png"
              alt=""
              className="w-32 h-auto sm:w-40 lg:w-48 opacity-90"
              loading="lazy"
              aria-hidden
            />
          </div>
        </div>
      </div>
    </section>
  )
}
