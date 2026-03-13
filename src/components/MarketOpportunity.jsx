import { useScrollAnimation } from "../lib/useScrollAnimation"
import { TrendingUp } from "lucide-react"
import { useLanguage } from "../i18n/LanguageContext"

export function MarketOpportunity() {
  const [ref, isVisible] = useScrollAnimation(0.1)
  const { t } = useLanguage()
  const stats = t("market.stats")

  return (
    <section
      id="market"
      className="py-16 sm:py-20 lg:py-24 px-4 sm:px-6 lg:px-8 relative overflow-hidden"
    >
      <div ref={ref} className={`relative mx-auto max-w-6xl ${isVisible ? "animate-in" : "opacity-0"}`}>
        <div className="text-center mb-10 sm:mb-16">
          <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white">
            {t("market.title")}
          </h2>
          <p className="mt-4 text-lg text-[#c4c1d6] max-w-2xl mx-auto">
            {t("market.subtitle")}
          </p>
        </div>

        <div className="relative mb-12 sm:mb-20">
          <div
            className="absolute -inset-4 rounded-3xl blur-2xl opacity-30 pointer-events-none -z-10"
            style={{
              background: "radial-gradient(ellipse 80% 60% at 50% 50%, rgba(168, 85, 247, 0.15) 0%, transparent 70%)",
            }}
          />
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6 relative">
            {stats.map((stat) => (
              <div
                key={stat.label}
                className="p-5 sm:p-6 rounded-2xl border border-purple-500/20 bg-[rgba(30,27,53,0.6)] text-center hover-lift hover:border-purple-500/30 backdrop-blur-sm flex flex-col justify-center min-h-[130px] sm:min-h-[150px]"
              >
                <p className="text-3xl sm:text-4xl lg:text-[2.5rem] font-extrabold gradient-text tracking-tight">
                  {stat.value}
                </p>
                <p className="mt-2 text-sm text-[#c4c1d6]">{stat.label}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  )
}
