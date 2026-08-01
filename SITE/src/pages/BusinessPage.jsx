import { BusinessModel } from "../components/BusinessModel"
import { SiteShell } from "../components/SiteShell"
import { PageContactCta } from "../components/PageContactCta"

export function BusinessPage() {
  return (
    <SiteShell>
      <main className="pt-24 sm:pt-28">
        <BusinessModel />
        <PageContactCta />
      </main>
    </SiteShell>
  )
}

