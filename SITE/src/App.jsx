import { Navbar } from "./components/Navbar"
import { Hero } from "./components/Hero"
import { HomeBusinessAreas } from "./components/HomeBusinessAreas"
import { HomeSolutionSections } from "./components/HomeSolutionSections"
import { WhyZura } from "./components/WhyZura"
import { FinalCTA } from "./components/FinalCTA"
import { Footer } from "./components/Footer"
import { useLanguage } from "./i18n/LanguageContext"

function App() {
  const { lang } = useLanguage()
  const whatsappBubbleText =
    lang === "pt" ? "Quero saber mais sobre a Zura" : "I want to know more about Zura"
  return (
    <div className="min-h-screen text-[#f8f7fc] antialiased isolate relative page-gradient">
      <div className="pointer-events-none fixed inset-0 -z-10 section-accent" aria-hidden />
      <div className="pointer-events-none fixed inset-0 -z-10 bg-grid opacity-50" aria-hidden />
      <Navbar />
      <main key={lang}>
        <Hero />
        <HomeBusinessAreas />
        <HomeSolutionSections />
        <WhyZura />
        <FinalCTA />
        <Footer />
      </main>

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
