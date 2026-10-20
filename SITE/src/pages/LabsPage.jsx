import { SiteShell } from "../components/SiteShell"
import { PageHeader } from "../components/PageHeader"
import { CardGridSection, CtaPanel, FaqSection, ImageShowcase, PurposeSection, StepsSection } from "../components/PageSections"
import { useLanguage } from "../i18n/LanguageContext"

const GALLERY_IMAGES = ["/Images/labs/shopping-parede.jpg", "/Images/labs/shopping-coluna.jpg"]

export function LabsPage() {
  const { t } = useLanguage()

  return (
    <SiteShell>
      <main>
        <PageHeader pill={t("labs.pill")} title={t("labs.title")} lead={t("labs.lead")} />

        <ImageShowcase items={t("labs.gallery").map((item, i) => ({ ...item, src: GALLERY_IMAGES[i] }))} />

        <PurposeSection title={t("labs.purposesTitle")} items={t("labs.purposes")} />

        <CardGridSection
          title={t("labs.promoteTitle")}
          items={t("labs.promoteItems")}
          columns="sm:grid-cols-2 lg:grid-cols-3"
          bordered
        />

        <StepsSection title={t("labs.approachTitle")} steps={t("labs.approachSteps")} bordered />

        <FaqSection title={t("labs.faqTitle")} items={t("labs.faq")} bordered />

        <CtaPanel
          title={t("labs.partnerTitle")}
          lead={t("labs.partnerLead")}
          href="/contacto/?type=partnerships"
          label={t("labs.cta")}
        />
      </main>
    </SiteShell>
  )
}
