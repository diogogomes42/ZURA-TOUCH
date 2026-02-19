import { Button } from "./ui/button"
import { Check } from "lucide-react"
import { useLanguage } from "../i18n/LanguageContext"

export function Hero() {
  const { t } = useLanguage()
  const heroStats = t("hero.stats")

  return (
    <section className="relative min-h-[85dvh] sm:min-h-screen flex items-center pt-20 sm:pt-24 pb-12 sm:pb-20 px-4 sm:px-6 lg:px-8 overflow-hidden">
        <div className="noise-overlay" aria-hidden />
        <div
          className="absolute inset-0 pointer-events-none opacity-[0.03] gradient-shift"
          style={{
            background: "radial-gradient(circle at 30% 50%, rgba(168, 85, 247, 0.4) 0%, transparent 50%), radial-gradient(circle at 70% 50%, rgba(236, 72, 153, 0.3) 0%, transparent 50%)",
          }}
        />

        <div className="mx-auto max-w-7xl w-full relative">
          <div className="grid lg:grid-cols-2 gap-12 lg:gap-20 items-center">
            <div className="text-center lg:text-left hero-fade-in order-2 lg:order-1">
              <h1 className="text-3xl min-[400px]:text-4xl sm:text-5xl md:text-6xl lg:text-7xl font-bold tracking-tight leading-[1.2]">
                <span className="block text-[#9b97b3] font-semibold text-[0.85em]">
                  {t("hero.tagline")}
                </span>
                <span className="block mt-3 gradient-text text-white">
                  {t("hero.title")}
                </span>
              </h1>
              <p className="mt-6 text-lg sm:text-xl text-[#c4c1d6] max-w-xl mx-auto lg:mx-0 leading-relaxed">
                {t("hero.description")}
              </p>

              <ul className="mt-6 flex flex-wrap justify-center lg:justify-start gap-x-6 gap-y-2 text-sm text-[#9b97b3]">
                {t("hero.bullets").map((bullet) => (
                  <li key={bullet} className="flex items-center gap-2">
                    <Check size={16} className="shrink-0 text-purple-500" strokeWidth={2.5} />
                    <span>{bullet}</span>
                  </li>
                ))}
              </ul>

              <div className="mt-8 flex justify-center lg:justify-start">
                <Button
                  asChild
                  variant="primary"
                  size="lg"
                  className="px-10 sm:px-14 text-lg sm:text-xl font-bold btn-glow-pulse neon-glow transition-all duration-300 hover:scale-[1.05]"
                >
                  <a href="#contact">{t("hero.cta")}</a>
                </Button>
              </div>
            </div>

            <div className="flex justify-center lg:justify-end order-1 lg:order-2 hero-fade-in" style={{ animationDelay: "0.15s" }}>
              <div className="relative">
                <div
                  className="absolute inset-0 -m-16 rounded-full blur-3xl pointer-events-none"
                  style={{
                    background: "radial-gradient(circle, rgba(168, 85, 247, 0.25) 0%, rgba(59, 130, 246, 0.12) 35%, rgba(236, 72, 153, 0.08) 50%, transparent 70%)",
                  }}
                />
                <img
                  src="/assets/mascot/hero-mascot.png"
                  alt="Zura Touch mascot"
                  className="relative w-64 max-w-[85vw] sm:w-80 md:w-[28rem] lg:w-[40rem] xl:w-[48rem] h-auto mascot-float mascot-glow"
                />
              </div>
            </div>
          </div>

          <div className="mt-10 sm:mt-16 pt-8 sm:pt-12 border-t border-purple-500/10 hero-fade-in" style={{ animationDelay: "0.3s" }}>
            <div className="grid grid-cols-3 gap-4 sm:gap-8">
              {heroStats.map((stat) => (
                <div key={stat.label} className="text-center">
                  <p className="text-xl sm:text-3xl md:text-4xl font-extrabold gradient-text">
                    {stat.value}
                  </p>
                  <p className="mt-1 text-xs sm:text-sm font-medium uppercase tracking-wider text-[#9b97b3]">
                    {stat.label}
                  </p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>
  )
}
