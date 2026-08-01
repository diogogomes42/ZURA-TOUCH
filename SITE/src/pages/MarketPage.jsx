import { MarketOpportunity } from "../components/MarketOpportunity"
import { SiteShell } from "../components/SiteShell"
import { PageContactCta } from "../components/PageContactCta"

export function MarketPage() {
  return (
    <SiteShell>
      <main className="pt-24 sm:pt-28">
        <MarketOpportunity />
        <PageContactCta />
      </main>
    </SiteShell>
  )
}

