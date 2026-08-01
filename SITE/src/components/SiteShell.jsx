import { Navbar } from "./Navbar"
import { Footer } from "./Footer"
import { useLanguage } from "../i18n/LanguageContext"

export function SiteShell({ children }) {
  const { lang } = useLanguage()
  const whatsappBubbleText =
    lang === "pt" ? "Quero saber mais sobre a Zura" : "I want to know more about Zura"

  return (
    <div className="min-h-screen text-[#f8f7fc] antialiased isolate relative page-gradient">
      <div className="pointer-events-none fixed inset-0 -z-10 section-accent" aria-hidden />
      <div className="pointer-events-none fixed inset-0 -z-10 bg-grid opacity-50" aria-hidden />

      <Navbar />
      {children}
      <Footer />

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
          className="whatsapp-icon"
          src="/assets/whatsapp/whatsapp-icon.png"
          alt=""
          aria-hidden="true"
          loading="lazy"
        />
      </a>
    </div>
  )
}

