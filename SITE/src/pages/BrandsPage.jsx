import { SiteShell } from "../components/SiteShell"
import { PageHeader } from "../components/PageHeader"
import { PageContactCta } from "../components/PageContactCta"
import { useLanguage } from "../i18n/LanguageContext"

export function BrandsPage() {
  const { t } = useLanguage()
  const provideItems = t("brands.provideItems")
  const receiveItems = t("brands.receiveItems")
  const formats = t("brands.formats")

  return (
    <SiteShell>
      <main>
        <PageHeader pill={t("brands.pill")} title={t("brands.title")} lead={t("brands.lead")} />

        <section className="px-4 sm:px-6 lg:px-8 pb-12">
          <div className="mx-auto max-w-6xl grid lg:grid-cols-2 gap-10">
            <div>
              <h2 className="text-2xl sm:text-3xl font-bold text-white">{t("brands.provideTitle")}</h2>
              <div className="mt-8 space-y-4">
                {provideItems.map((item) => (
                  <div key={item.title} className="rounded-2xl border border-purple-500/20 bg-[rgba(30,27,53,0.5)] p-6">
                    <h3 className="text-lg font-semibold text-white">{item.title}</h3>
                    <p className="mt-2 text-sm text-[#c4c1d6] leading-relaxed">{item.description}</p>
                  </div>
                ))}
              </div>
            </div>
            <div>
              <h2 className="text-2xl sm:text-3xl font-bold text-white">{t("brands.receiveTitle")}</h2>
              <div className="mt-8 space-y-4">
                {receiveItems.map((item) => (
                  <div key={item.title} className="rounded-2xl border border-purple-500/20 bg-[rgba(30,27,53,0.5)] p-6">
                    <h3 className="text-lg font-semibold text-white">{item.title}</h3>
                    <p className="mt-2 text-sm text-[#c4c1d6] leading-relaxed">{item.description}</p>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </section>

        <section className="px-4 sm:px-6 lg:px-8 py-12 sm:py-16 border-t border-purple-500/10">
          <div className="mx-auto max-w-6xl">
            <h2 className="text-2xl sm:text-3xl font-bold text-white">{t("brands.formatsTitle")}</h2>
            <div className="mt-8 grid md:grid-cols-3 gap-6">
              {formats.map((format) => (
                <div key={format.title} className="rounded-2xl border border-purple-500/20 bg-[rgba(30,27,53,0.5)] p-6">
                  <h3 className="text-lg font-semibold text-white">{format.title}</h3>
                  <p className="mt-2 text-sm text-[#c4c1d6] leading-relaxed">{format.description}</p>
                </div>
              ))}
            </div>
          </div>
        </section>

        <PageContactCta audience="brand" />
      </main>
    </SiteShell>
  )
}
