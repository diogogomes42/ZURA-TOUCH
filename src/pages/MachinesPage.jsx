import { MachinesShowcase } from "../components/MachinesShowcase"
import { SiteShell } from "../components/SiteShell"
import { PageContactCta } from "../components/PageContactCta"
import { useLanguage } from "../i18n/LanguageContext"

export function MachinesPage() {
  const { lang } = useLanguage()
  const copy =
    lang === "pt"
      ? {
          title: "As nossas máquinas",
          lead: "Explora a gama ZURA e escolhe a solução ideal para o teu espaço.",
        }
      : {
          title: "Our machines",
          lead: "Explore the ZURA range and choose the best fit for your venue.",
        }

  return (
    <SiteShell>
      <main className="pt-28 sm:pt-32">
        <section className="px-4 sm:px-6 lg:px-8">
          <div className="mx-auto max-w-6xl">
            <h1 className="text-3xl sm:text-4xl lg:text-5xl font-extrabold text-white">
              {copy.title}
            </h1>
            <p className="mt-4 text-base sm:text-lg text-[#c4c1d6] leading-relaxed max-w-3xl">
              {copy.lead}
            </p>
          </div>
        </section>

        <MachinesShowcase />

        <PageContactCta />
      </main>
    </SiteShell>
  )
}

