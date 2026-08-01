import { Contact } from "../components/Contact"
import { SiteShell } from "../components/SiteShell"

export function ContactPage() {
  return (
    <SiteShell>
      <main className="pt-24 sm:pt-28">
        <Contact />
      </main>
    </SiteShell>
  )
}

