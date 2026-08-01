import { Button } from "./ui/button"
import { useScrollAnimation } from "../lib/useScrollAnimation"
import { useLanguage } from "../i18n/LanguageContext"

export function FinalCTA() {
  const [ref, isVisible] = useScrollAnimation(0.1)
  const { t } = useLanguage()

  return (
    <section className="py-20 sm:py-28 lg:py-36 px-4 sm:px-6 lg:px-8 relative overflow-hidden">
      <div className="absolute inset-0 pointer-events-none bg-[radial-gradient(ellipse_70%_60%_at_50%_50%,rgba(168,85,247,0.12),transparent)]" aria-hidden />
      <div ref={ref} className={`relative mx-auto max-w-4xl text-center ${isVisible ? "animate-in" : "opacity-0"}`}>
        <h2 className="text-3xl sm:text-5xl lg:text-6xl font-bold tracking-tight text-white">
          {t("finalCTA.title")}
        </h2>
        <div className="mt-10 sm:mt-12 flex flex-col sm:flex-row justify-center gap-3 sm:gap-4">
          <Button
            asChild
            variant="primary"
            size="lg"
            className="text-base font-bold neon-glow btn-glow-pulse transition-all duration-300 hover:scale-[1.03] px-10"
          >
            <a href="/contacto/?type=venue">{t("finalCTA.ctaInstall")}</a>
          </Button>
          <Button
            asChild
            variant="outline"
            size="lg"
            className="text-base font-semibold border-purple-500/50 text-purple-200 hover:bg-purple-500/10 px-10 backdrop-blur-sm"
          >
            <a href="/contacto/?type=brand">{t("finalCTA.ctaPartner")}</a>
          </Button>
        </div>
      </div>
    </section>
  )
}
