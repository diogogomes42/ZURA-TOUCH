import { MachinesShowcase } from "../components/MachinesShowcase"
import { SiteShell } from "../components/SiteShell"
import { PageHeader } from "../components/PageHeader"
import { PageContactCta } from "../components/PageContactCta"
import { useLanguage } from "../i18n/LanguageContext"

export function MachinesPage() {
  const { t } = useLanguage()

  return (
    <SiteShell>
      <main>
        <PageHeader pill={t("machines.pill")} title={t("machines.title")} lead={t("machines.lead")} />
        <MachinesShowcase />
        <PageContactCta audience="venue" />
      </main>
    </SiteShell>
  )
}
