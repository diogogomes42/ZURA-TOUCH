import { useState, useEffect } from "react"
import { Navbar } from "./components/Navbar"
import { Hero } from "./components/Hero"
import { Problem } from "./components/Problem"
import { Solution } from "./components/Solution"
import { FeaturePromos } from "./components/FeaturePromos"
import { MarketOpportunity } from "./components/MarketOpportunity"
import { BusinessModel } from "./components/BusinessModel"
import { HowItWorks } from "./components/HowItWorks"
import { WhoItsFor } from "./components/WhoItsFor"
import { TrustedStrip } from "./components/TrustedStrip"
import { FinalCTA } from "./components/FinalCTA"
import { Contact } from "./components/Contact"
import { Footer } from "./components/Footer"
import { LegalModal } from "./components/LegalModal"

function App() {
  const [legalType, setLegalType] = useState(() => {
    if (typeof window !== "undefined") {
      const hash = window.location.hash.slice(1)
      if (hash === "privacy" || hash === "terms") return hash
    }
    return null
  })

  useEffect(() => {
    const handleHashChange = () => {
      const hash = window.location.hash.slice(1)
      if (hash === "privacy" || hash === "terms") setLegalType(hash)
      else setLegalType(null)
    }
    window.addEventListener("hashchange", handleHashChange)
    return () => window.removeEventListener("hashchange", handleHashChange)
  }, [])

  const closeLegal = () => {
    window.history.replaceState(null, "", window.location.pathname || "/")
    setLegalType(null)
  }

  return (
    <div className="min-h-screen text-[#f8f7fc] antialiased isolate relative page-gradient">
      {/* Soft radial accents - seamless across entire page */}
      <div className="pointer-events-none fixed inset-0 -z-10 section-accent" aria-hidden />
      <div className="pointer-events-none fixed inset-0 -z-10 bg-grid opacity-50" aria-hidden />
      <Navbar />
      <main>
        <Hero />
        <Problem />
        <Solution />
        <FeaturePromos />
        <MarketOpportunity />
        <BusinessModel />
        <HowItWorks />
        <WhoItsFor />
        <TrustedStrip />
        <FinalCTA />
        <Contact />
        <Footer />
      </main>

      {legalType && (
        <LegalModal type={legalType} onClose={closeLegal} />
      )}
    </div>
  )
}

export default App
