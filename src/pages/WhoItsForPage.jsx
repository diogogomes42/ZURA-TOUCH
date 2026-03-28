import { WhoItsFor } from "../components/WhoItsFor"
import { SiteShell } from "../components/SiteShell"
import { PageContactCta } from "../components/PageContactCta"

export function WhoItsForPage() {
  return (
    <SiteShell>
      <main className="pt-24 sm:pt-28">
        <WhoItsFor />
        <PageContactCta />
      </main>
    </SiteShell>
  )
}

