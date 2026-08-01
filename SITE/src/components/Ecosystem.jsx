import { Gamepad2, Building2, Sparkles } from "lucide-react"
import { useScrollAnimation } from "../lib/useScrollAnimation"
import { useLanguage } from "../i18n/LanguageContext"

const icons = [Gamepad2, Building2, Sparkles]
const keys = ["players", "venues", "brands"]

export function Ecosystem() {
  const [ref, isVisible] = useScrollAnimation(0.1)
  const { t } = useLanguage()

  return (
    <section className="py-16 sm:py-20 lg:py-24 px-4 sm:px-6 lg:px-8">
      <div ref={ref} className={`mx-auto max-w-6xl ${isVisible ? "animate-in" : "opacity-0"}`}>
        <div className="text-center mb-12 sm:mb-16">
          <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white">
            {t("ecosystem.title")}
          </h2>
          <p className="mt-4 text-lg text-[#c4c1d6] max-w-2xl mx-auto">
            {t("ecosystem.subtitle")}
          </p>
        </div>

        <div className="grid md:grid-cols-3 gap-6 lg:gap-8">
          {keys.map((key, i) => {
            const Icon = icons[i]
            const block = t(`ecosystem.${key}`)
            return (
              <div
                key={key}
                className="rounded-2xl border border-purple-500/20 bg-[rgba(30,27,53,0.6)] p-6 sm:p-8 backdrop-blur-sm"
              >
                <div className="inline-flex items-center justify-center w-12 h-12 rounded-xl bg-gradient-to-br from-purple-500/20 to-pink-500/20 border border-purple-500/30">
                  <Icon size={22} className="text-purple-300" />
                </div>
                <h3 className="mt-5 text-xl font-bold text-white">{block.title}</h3>
                <p className="mt-3 text-sm text-[#c4c1d6] leading-relaxed">{block.description}</p>
                <ul className="mt-5 space-y-2">
                  {block.points.map((point) => (
                    <li key={point} className="flex items-start gap-2 text-sm text-[#9b97b3]">
                      <span className="mt-1.5 h-1.5 w-1.5 shrink-0 rounded-full bg-purple-400" />
                      {point}
                    </li>
                  ))}
                </ul>
              </div>
            )
          })}
        </div>
      </div>
    </section>
  )
}
