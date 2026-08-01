import { SiteShell } from "../components/SiteShell"
import { PageHeader } from "../components/PageHeader"
import { PageContactCta } from "../components/PageContactCta"
import { useLanguage } from "../i18n/LanguageContext"

export function VenuesPage() {
  const { t } = useLanguage()
  const whyItems = t("venues.whyItems")
  const types = t("venues.types")

  return (
    <SiteShell>
      <main>
        <PageHeader pill={t("venues.pill")} title={t("venues.title")} lead={t("venues.lead")} />

        <section className="px-4 sm:px-6 lg:px-8 pb-12">
          <div className="mx-auto max-w-6xl">
            <h2 className="text-2xl sm:text-3xl font-bold text-white">{t("venues.whyTitle")}</h2>
            <div className="mt-8 grid sm:grid-cols-2 gap-6">
              {whyItems.map((item) => (
                <div key={item.title} className="rounded-2xl border border-purple-500/20 bg-[rgba(30,27,53,0.5)] p-6">
                  <h3 className="text-lg font-semibold text-white">{item.title}</h3>
                  <p className="mt-2 text-sm text-[#c4c1d6] leading-relaxed">{item.description}</p>
                </div>
              ))}
            </div>
          </div>
        </section>

        <section className="px-4 sm:px-6 lg:px-8 py-12 sm:py-16 border-t border-purple-500/10">
          <div className="mx-auto max-w-6xl">
            <h2 className="text-2xl sm:text-3xl font-bold text-white">{t("venues.typesTitle")}</h2>
            <div className="mt-8 grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
              {types.map((type) => (
                <div key={type.title} className="rounded-2xl border border-purple-500/20 bg-[rgba(30,27,53,0.5)] p-6">
                  <h3 className="text-lg font-semibold text-white">{type.title}</h3>
                  <p className="mt-2 text-sm text-[#c4c1d6] leading-relaxed">{type.description}</p>
                </div>
              ))}
            </div>
          </div>
        </section>

        <PageContactCta audience="venue" />
      </main>
    </SiteShell>
  )
}
