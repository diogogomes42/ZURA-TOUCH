import { HowItWorks } from "../components/HowItWorks"
import { SiteShell } from "../components/SiteShell"
import { PageContactCta } from "../components/PageContactCta"

export function HowItWorksPage() {
  return (
    <SiteShell>
      <main className="pt-24 sm:pt-28">
        <HowItWorks />
        <PageContactCta />
      </main>
    </SiteShell>
  )
}

