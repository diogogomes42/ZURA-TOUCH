import { Solution } from "../components/Solution"
import { SiteShell } from "../components/SiteShell"
import { PageContactCta } from "../components/PageContactCta"

export function SolutionPage() {
  return (
    <SiteShell>
      <main className="pt-24 sm:pt-28">
        <Solution />
        <PageContactCta />
      </main>
    </SiteShell>
  )
}

