import { useScrollAnimation } from "../lib/useScrollAnimation"
import { useLanguage } from "../i18n/LanguageContext"

export function TrustedStrip() {
  const [ref, isVisible] = useScrollAnimation(0.1)
  const { t } = useLanguage()
  const badges = t("trustedStrip.badges")

  return (
    <section className="py-12 sm:py-16 px-4 sm:px-6 lg:px-8">
      <div ref={ref} className={`mx-auto max-w-6xl text-center ${isVisible ? "animate-in" : "opacity-0"}`}>
        <h2 className="text-xl sm:text-2xl md:text-3xl font-bold tracking-tight text-white">
          {t("trustedStrip.title")}
        </h2>
        <p className="mt-3 text-[#c4c1d6] max-w-2xl mx-auto">
          {t("trustedStrip.description")}
        </p>
        <div className="mt-6 sm:mt-10 flex flex-wrap justify-center gap-3 sm:gap-6 md:gap-8 lg:gap-12 text-[#9b97b3] text-xs sm:text-sm">
          {badges.flatMap((badge, i) => [
            <span key={badge} className="text-sm font-semibold uppercase tracking-wider">{badge}</span>,
            ...(i < badges.length - 1 ? [<span key={`sep-${i}`} className="text-purple-400">✦</span>] : []),
          ])}
        </div>
      </div>
    </section>
  )
}
