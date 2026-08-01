import { SiteShell } from "../components/SiteShell"
import { PageContactCta } from "../components/PageContactCta"
import { useLanguage } from "../i18n/LanguageContext"

export function ServicesPage() {
  const { lang } = useLanguage()

  const copy =
    lang === "pt"
      ? {
          pill: "Serviços",
          title: "Serviços Zura Touch",
          lead: "Nós tratamos de tudo para que a tua máquina esteja sempre a vender.",
          bullets: [
            "Instalação e configuração no local",
            "Reposição e gestão de stock",
            "Manutenção e suporte",
            "Relatórios e otimização de performance",
          ],
        }
      : {
          pill: "Services",
          title: "Zura Touch Services",
          lead: "We handle everything so your machine keeps selling every day.",
          bullets: [
            "On-site installation and setup",
            "Restocking and inventory management",
            "Maintenance and support",
            "Reports and performance optimization",
          ],
        }

  return (
    <SiteShell>
      <main className="pt-28 sm:pt-32">
        <div className="px-4 sm:px-6 lg:px-8 pb-8 sm:pb-12">
          <div className="mx-auto max-w-4xl">
            <div className="inline-flex items-center rounded-full border border-purple-500/20 bg-purple-500/10 px-3 py-1 text-xs font-semibold tracking-wide text-[#c4c1d6]">
              {copy.pill}
            </div>
            <h1 className="mt-4 text-3xl sm:text-4xl lg:text-5xl font-extrabold text-white">
              {copy.title}
            </h1>
            <p className="mt-4 text-base sm:text-lg text-[#c4c1d6] leading-relaxed">
              {copy.lead}
            </p>

            <ul className="mt-8 space-y-3">
              {copy.bullets.map((b) => (
                <li key={b} className="flex gap-3 text-[#c4c1d6]">
                  <span className="mt-1.5 h-2 w-2 shrink-0 rounded-full bg-gradient-to-r from-purple-400 to-pink-400" />
                  <span className="leading-relaxed">{b}</span>
                </li>
              ))}
            </ul>
          </div>
        </div>
        <PageContactCta />
      </main>
    </SiteShell>
  )
}

