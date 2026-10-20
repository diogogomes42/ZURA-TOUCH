import { MachinesShowcase } from "../components/MachinesShowcase"
import { SiteShell } from "../components/SiteShell"
import { PageHeader } from "../components/PageHeader"
import { CardGridSection, CtaPanel, FaqSection } from "../components/PageSections"
import { useLanguage } from "../i18n/LanguageContext"

export function MachinesPage() {
  const { t } = useLanguage()

  return (
    <SiteShell>
      <main>
        <PageHeader pill={t("machines.pill")} title={t("machines.title")} lead={t("machines.lead")} />
        <MachinesShowcase />
        <CardGridSection
          title={t("machines.capabilitiesTitle")}
          items={t("machines.capabilities")}
          columns="sm:grid-cols-2 lg:grid-cols-3"
          bordered
        />
        <CardGridSection
          title={t("machines.customTitle")}
          items={t("machines.customItems")}
          columns="sm:grid-cols-2"
          bordered
        />
        <FaqSection title={t("machines.faqTitle")} items={t("machines.faq")} bordered />
        <CtaPanel
          title={t("machines.quoteTitle")}
          lead={t("machines.quoteLead")}
          href="/contacto/?type=machines"
          label={t("machines.cta")}
        />
      </main>
    </SiteShell>
  )
}
