import { SiteShell } from "../components/SiteShell"
import { PageHeader } from "../components/PageHeader"
import { PageContactCta } from "../components/PageContactCta"
import { useLanguage } from "../i18n/LanguageContext"
import { Check } from "lucide-react"

export function GamePage() {
  const { t } = useLanguage()
  const howSteps = t("game.howSteps")
  const prizes = t("game.prizes")
  const experiencePoints = t("game.experiencePoints")

  return (
    <SiteShell>
      <main>
        <PageHeader pill={t("game.pill")} title={t("game.title")} lead={t("game.lead")} />

        <section className="px-4 sm:px-6 lg:px-8 pb-12">
          <div className="mx-auto max-w-6xl">
            <h2 className="text-2xl sm:text-3xl font-bold text-white">{t("game.howTitle")}</h2>
            <div className="mt-8 grid md:grid-cols-3 gap-6">
              {howSteps.map((step, i) => (
                <div key={step.title} className="rounded-2xl border border-purple-500/20 bg-[rgba(30,27,53,0.5)] p-6">
                  <span className="inline-flex w-8 h-8 items-center justify-center rounded-full bg-gradient-to-r from-purple-500 to-pink-500 text-sm font-bold text-white">
                    {i + 1}
                  </span>
                  <h3 className="mt-4 text-lg font-semibold text-white">{step.title}</h3>
                  <p className="mt-2 text-sm text-[#c4c1d6] leading-relaxed">{step.description}</p>
                </div>
              ))}
            </div>
          </div>
        </section>

        <section className="px-4 sm:px-6 lg:px-8 py-12 sm:py-16 border-t border-purple-500/10">
          <div className="mx-auto max-w-6xl">
            <h2 className="text-2xl sm:text-3xl font-bold text-white">{t("game.prizesTitle")}</h2>
            <div className="mt-8 grid md:grid-cols-3 gap-6">
              {prizes.map((prize) => (
                <div key={prize.title} className="rounded-2xl border border-purple-500/20 bg-[rgba(30,27,53,0.5)] p-6">
                  <h3 className="text-lg font-semibold text-white">{prize.title}</h3>
                  <p className="mt-2 text-sm text-[#c4c1d6] leading-relaxed">{prize.description}</p>
                </div>
              ))}
            </div>
          </div>
        </section>

        <section className="px-4 sm:px-6 lg:px-8 py-12 sm:py-16">
          <div className="mx-auto max-w-4xl">
            <h2 className="text-2xl sm:text-3xl font-bold text-white">{t("game.experienceTitle")}</h2>
            <ul className="mt-8 space-y-4">
              {experiencePoints.map((point) => (
                <li key={point} className="flex items-start gap-3 text-[#c4c1d6]">
                  <Check size={20} className="shrink-0 text-purple-400 mt-0.5" />
                  <span>{point}</span>
                </li>
              ))}
            </ul>
          </div>
        </section>

        <PageContactCta audience="venue" />
      </main>
    </SiteShell>
  )
}
