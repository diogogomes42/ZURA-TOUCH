import { Button } from "./ui/button"
import { useScrollAnimation } from "../lib/useScrollAnimation"
import { useLanguage } from "../i18n/LanguageContext"

export function FinalCTA() {
  const [ref, isVisible] = useScrollAnimation(0.1)
  const { t } = useLanguage()

  return (
    <section className="py-16 sm:py-20 lg:py-28 px-4 sm:px-6 lg:px-8 relative overflow-hidden">
      <div className="absolute inset-0 bg-gradient-to-br from-purple-600/25 via-pink-600/15 to-transparent" />
      <div className="absolute inset-0 bg-grid opacity-40" />

      <div ref={ref} className={`relative mx-auto max-w-6xl flex flex-col lg:flex-row gap-10 sm:gap-16 lg:gap-24 items-center ${isVisible ? "animate-in" : "opacity-0"}`}>
        <div className="flex-1 text-center lg:text-left">
          <h2 className="text-3xl sm:text-5xl lg:text-6xl font-bold tracking-tight text-white">
            {t("finalCTA.title")}
            {t("finalCTA.titleAccent") && (
              <span className="block mt-3 gradient-text">{t("finalCTA.titleAccent")}</span>
            )}
          </h2>
          <p className="mt-8 text-lg sm:text-xl text-[#c4c1d6] max-w-2xl mx-auto lg:mx-0">
            {t("finalCTA.description")}
          </p>
          <div className="mt-8 sm:mt-12 flex justify-center lg:justify-start">
            <Button
              asChild
              variant="primary"
              size="lg"
              className="text-base font-bold neon-glow btn-glow-pulse transition-all duration-300 hover:scale-[1.03]"
            >
              <a href="#contact">{t("finalCTA.partnerUp")}</a>
            </Button>
          </div>
        </div>

        <div className="flex justify-center lg:shrink-0">
          <img
            src="/assets/mascot/cool-mascot.png"
            alt=""
            className="w-60 h-auto sm:w-72 lg:w-96"
            loading="lazy"
            aria-hidden
          />
        </div>
      </div>
    </section>
  )
}
