import { SiteShell } from "../components/SiteShell"
import { LegalContent } from "../components/LegalContent"

export function TermsPage() {
  return (
    <SiteShell>
      <main className="pt-28 sm:pt-32 pb-16 sm:pb-20 lg:pb-24 px-4 sm:px-6 lg:px-8">
        <div className="mx-auto max-w-3xl rounded-2xl border border-purple-500/20 bg-[var(--bg-base)] shadow-2xl">
          <LegalContent type="terms" />
        </div>
      </main>
    </SiteShell>
  )
}

