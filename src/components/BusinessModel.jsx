import { useScrollAnimation } from "../lib/useScrollAnimation"
import { Handshake, Calendar, Building2 } from "lucide-react"
import { useLanguage } from "../i18n/LanguageContext"

const icons = [Handshake, Calendar, Building2]

export function BusinessModel() {
  const [ref, isVisible] = useScrollAnimation(0.1)
  const { t } = useLanguage()
  const models = t("businessModel.items")

  return (
    <section id="business-model" className="py-16 sm:py-20 lg:py-24 px-4 sm:px-6 lg:px-8">
      <div ref={ref} className={`mx-auto max-w-6xl ${isVisible ? "animate-in" : "opacity-0"}`}>
        <div className="text-center mb-10 sm:mb-16">
          <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white">
            {t("businessModel.title")}
          </h2>
          <p className="mt-4 text-lg text-[#c4c1d6] max-w-2xl mx-auto">
            {t("businessModel.subtitle")}
          </p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-5 sm:gap-6 md:gap-8 [&>*:nth-child(3)]:justify-self-center md:[&>*:nth-child(3)]:justify-self-stretch">
          {models.map((model, i) => {
            const Icon = icons[i]
            return (
              <div
                key={model.title}
                className="border-gradient-top relative p-6 sm:p-8 rounded-2xl border-2 border-purple-500/30 bg-[rgba(30,27,53,0.7)] hover:border-purple-500/50 hover:bg-[rgba(30,27,53,0.85)] transition-all duration-300 active:scale-[0.99] sm:hover:scale-[1.02] shadow-lg shadow-black/20 hover:shadow-xl hover:shadow-purple-500/5 backdrop-blur-sm"
              >
                <div className="flex h-14 w-14 items-center justify-center rounded-xl bg-gradient-to-br from-purple-600 to-pink-600 text-white">
                  <Icon size={28} strokeWidth={2} />
                </div>
                <h3 className="mt-6 text-xl font-bold text-white">{model.title}</h3>
                <p className="mt-1 text-sm font-medium text-purple-400">{model.tagline}</p>
                <p className="mt-4 text-[#c4c1d6]">{model.description}</p>
              </div>
            )
          })}
        </div>
      </div>
    </section>
  )
}
