import { SiteShell } from "../components/SiteShell"
import { PageHeader } from "../components/PageHeader"
import { PageContactCta } from "../components/PageContactCta"
import {
  CardGridSection,
  CheckListSection,
  FaqSection,
  ImageShowcase,
  SplitCardSection,
  StepsSection,
} from "../components/PageSections"
import { useLanguage } from "../i18n/LanguageContext"

const GALLERY_IMAGES = [
  "/Images/experiences/activacao-marca.jpg",
  "/Images/experiences/evento-corporativo.jpg",
  "/Images/experiences/feira-emprego.jpg",
  "/Images/experiences/casamento.jpg",
]

export function BrandsPage() {
  const { t } = useLanguage()

  return (
    <SiteShell>
      <main>
        <PageHeader pill={t("brands.pill")} title={t("brands.title")} lead={t("brands.lead")} />

        <ImageShowcase
          items={t("brands.gallery").map((item, i) => ({ ...item, src: GALLERY_IMAGES[i] }))}
        />

        <CardGridSection
          title={t("brands.occasionsTitle")}
          items={t("brands.occasions")}
          columns="sm:grid-cols-2 lg:grid-cols-3"
        />

        <SplitCardSection
          title={t("brands.customTitle")}
          items={t("brands.customItems")}
          image="/Images/zura.png"
          imageAlt="Zura Touch interactive machine"
          bordered
        />

        <CheckListSection title={t("brands.engagementTitle")} points={t("brands.engagementItems")} bordered />

        <StepsSection title={t("brands.processTitle")} steps={t("brands.processSteps")} bordered />

        <FaqSection title={t("brands.faqTitle")} items={t("brands.faq")} bordered />

        <PageContactCta href="/contacto/?type=brand-experiences" label={t("brands.cta")} />
      </main>
    </SiteShell>
  )
}
