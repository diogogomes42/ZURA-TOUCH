import { useState, useEffect } from "react"
import { Navbar } from "./components/Navbar"
import { Hero } from "./components/Hero"
import { Problem } from "./components/Problem"
import { Solution } from "./components/Solution"
import { MarketOpportunity } from "./components/MarketOpportunity"
import { MachinesShowcase } from "./components/MachinesShowcase"
import { BusinessModel } from "./components/BusinessModel"
import { HowItWorks } from "./components/HowItWorks"
import { WhoItsFor } from "./components/WhoItsFor"
import { TrustedStrip } from "./components/TrustedStrip"
import { FinalCTA } from "./components/FinalCTA"
import { Contact } from "./components/Contact"
import { Footer } from "./components/Footer"
import { LegalModal } from "./components/LegalModal"
import { useLanguage } from "./i18n/LanguageContext"

function App() {
  const { lang } = useLanguage()
  const whatsappBubbleText =
    lang === "pt" ? "Quero saber mais sobre a Zura" : "I want to know more about Zura"
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
      <main key={lang}>
        <Hero />
        <Problem />
        <Solution />
        <MarketOpportunity />
        <MachinesShowcase />
        <BusinessModel />
        <HowItWorks />
        <WhoItsFor />
        <TrustedStrip />
        <FinalCTA />
        <Contact />
        <Footer />
      </main>

      {legalType && (
        <LegalModal key={lang} type={legalType} onClose={closeLegal} />
      )}

      <a
        className="whatsapp-fab"
        href="https://wa.me/351931708796"
        target="_blank"
        rel="noopener noreferrer"
        aria-label="WhatsApp"
        title="WhatsApp"
      >
        <span className="whatsapp-bubble" aria-hidden="true">
          {whatsappBubbleText}
        </span>
        <img
          src="/assets/whatsapp/whatsapp-icon.png"
          alt=""
          className="whatsapp-icon"
          aria-hidden="true"
        />
      </a>
    </div>
  )
}

export default App
