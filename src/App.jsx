import { useState, useEffect, lazy, Suspense } from "react"
import { Navbar } from "./components/Navbar"
import { Hero } from "./components/Hero"

const Problem = lazy(() => import("./components/Problem").then((m) => ({ default: m.Problem })))
const Solution = lazy(() => import("./components/Solution").then((m) => ({ default: m.Solution })))
const MarketOpportunity = lazy(() => import("./components/MarketOpportunity").then((m) => ({ default: m.MarketOpportunity })))
const MachinesShowcase = lazy(() => import("./components/MachinesShowcase").then((m) => ({ default: m.MachinesShowcase })))
const BusinessModel = lazy(() => import("./components/BusinessModel").then((m) => ({ default: m.BusinessModel })))
const HowItWorks = lazy(() => import("./components/HowItWorks").then((m) => ({ default: m.HowItWorks })))
const WhoItsFor = lazy(() => import("./components/WhoItsFor").then((m) => ({ default: m.WhoItsFor })))
const TrustedStrip = lazy(() => import("./components/TrustedStrip").then((m) => ({ default: m.TrustedStrip })))
const FinalCTA = lazy(() => import("./components/FinalCTA").then((m) => ({ default: m.FinalCTA })))
const Contact = lazy(() => import("./components/Contact").then((m) => ({ default: m.Contact })))
const Footer = lazy(() => import("./components/Footer").then((m) => ({ default: m.Footer })))
const LegalModal = lazy(() => import("./components/LegalModal").then((m) => ({ default: m.LegalModal })))

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
        <Suspense fallback={null}>
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
        </Suspense>
      </main>

      {legalType && (
        <Suspense fallback={null}>
          <LegalModal type={legalType} onClose={closeLegal} />
        </Suspense>
      )}
    </div>
  )
}

export default App
