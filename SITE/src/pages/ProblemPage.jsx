import { Problem } from "../components/Problem"
import { SiteShell } from "../components/SiteShell"
import { PageContactCta } from "../components/PageContactCta"

export function ProblemPage() {
  return (
    <SiteShell>
      <main className="pt-24 sm:pt-28">
        <Problem />
        <PageContactCta />
      </main>
    </SiteShell>
  )
}

