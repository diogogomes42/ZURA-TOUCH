import { useState, useEffect, useRef } from "react"
import { Button } from "./ui/button"
import { Menu, X, ChevronDown } from "lucide-react"
import { cn } from "../lib/utils"
import { useLanguage } from "../i18n/LanguageContext"

const languages = [
  { code: "pt", flag: "/assets/flags/pt.png", label: "Português" },
  { code: "en", flag: "/assets/flags/en.png", label: "English" },
]

export function Navbar() {
  const [isScrolled, setIsScrolled] = useState(false)
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false)
  const [isLangOpen, setIsLangOpen] = useState(false)
  const langRefDesktop = useRef(null)
  const langRefMobile = useRef(null)
  const { lang, setLang, t } = useLanguage()
  const currentLang = languages.find((l) => l.code === lang) ?? languages[0]

  useEffect(() => {
    const handleClickOutside = (e) => {
      const inside = langRefDesktop.current?.contains(e.target) || langRefMobile.current?.contains(e.target)
      if (!inside) setIsLangOpen(false)
    }
    document.addEventListener("click", handleClickOutside)
    return () => document.removeEventListener("click", handleClickOutside)
  }, [])

  const navLinks = [
    { href: "#problem", label: t("nav.problem") },
    { href: "#solution", label: t("nav.solution") },
    { href: "#market", label: t("nav.market") },
    { href: "#business-model", label: t("nav.business") },
    { href: "#how-it-works", label: t("nav.howItWorks") },
    { href: "#who-its-for", label: t("nav.whoItsFor") },
  ]

  useEffect(() => {
    const handleScroll = () => setIsScrolled(window.scrollY > 20)
    window.addEventListener("scroll", handleScroll)
    return () => window.removeEventListener("scroll", handleScroll)
  }, [])

  return (
    <header
      className={cn(
        "fixed top-0 left-0 right-0 z-50 transition-all duration-300 pt-[env(safe-area-inset-top)]",
        isScrolled ? "bg-[var(--bg-base)]/90 backdrop-blur-md border-b border-purple-500/10" : "bg-transparent"
      )}
    >
      <nav className="mx-auto flex max-w-6xl items-center justify-between px-4 py-4 sm:px-6 lg:px-8">
        <a href="#" className="flex items-center">
          <img src="/logo.png" alt="Zura Touch" className="h-12 w-auto sm:h-14" />
        </a>

        <div className="hidden lg:flex lg:items-center lg:gap-6">
          {navLinks.map((link) => (
            <a
              key={link.href}
              href={link.href}
              className="relative text-sm font-medium text-[#c4c1d6] transition-colors hover:text-white
                         after:content-[''] after:absolute after:left-0 after:-bottom-1 after:h-[2px]
                         after:w-0 after:bg-gradient-to-r from-purple-400 to-pink-400
                         after:transition-all after:duration-300 hover:after:w-full"
            >
              {link.label}
            </a>
          ))}
          <div className="relative ml-2" ref={langRefDesktop}>
            <button
              type="button"
              onClick={(e) => { e.stopPropagation(); setIsLangOpen((o) => !o) }}
              className="flex items-center gap-1.5 px-2 py-1.5 rounded border-2 border-purple-500/30 bg-purple-500/10 hover:bg-purple-500/20 transition-colors"
              aria-haspopup="listbox"
              aria-expanded={isLangOpen}
              aria-label="Selecionar idioma"
            >
              <img src={currentLang.flag} alt={currentLang.code} className="w-6 h-4 object-cover rounded-sm" />
              <ChevronDown size={14} className={cn("text-[#c4c1d6] transition-transform", isLangOpen && "rotate-180")} />
            </button>
            {isLangOpen && (
              <div className="absolute top-full right-0 mt-1 py-1 rounded-lg border border-purple-500/20 bg-[var(--bg-base)] shadow-xl shadow-black/20 min-w-[120px] z-50">
                {languages.map((l) => (
                  <button
                    key={l.code}
                    type="button"
                    onClick={() => { setLang(l.code); setIsLangOpen(false) }}
                    className={cn(
                      "w-full flex items-center gap-2 px-3 py-2 text-left text-sm transition-colors",
                      lang === l.code ? "bg-purple-500/20 text-white" : "text-[#c4c1d6] hover:bg-purple-500/10 hover:text-white"
                    )}
                  >
                    <img src={l.flag} alt="" className="w-5 h-3.5 object-cover rounded-sm" />
                    {l.label}
                  </button>
                ))}
              </div>
            )}
          </div>
          <Button asChild variant="primary" size="sm" className="font-bold">
            <a href="#contact">{t("nav.cta")}</a>
          </Button>
        </div>

        <button
          type="button"
          className="lg:hidden rounded-lg p-3 -m-1 min-w-[44px] min-h-[44px] flex items-center justify-center text-[#c4c1d6] hover:text-white hover:bg-purple-500/10 active:bg-purple-500/20 touch-manipulation"
          onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
          aria-label="Toggle menu"
          aria-expanded={isMobileMenuOpen}
        >
          {isMobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </nav>

      {isMobileMenuOpen && (
        <div className="lg:hidden border-t border-purple-500/10 bg-[var(--bg-base)]/95 backdrop-blur-md">
          <div className="flex flex-col gap-1 px-4 py-4">
            {navLinks.map((link) => (
              <a
                key={link.href}
                href={link.href}
                className="block rounded-lg px-4 py-3.5 min-h-[44px] text-sm font-medium text-[#c4c1d6] hover:bg-purple-500/10 hover:text-white active:bg-purple-500/15 touch-manipulation"
                onClick={() => setIsMobileMenuOpen(false)}
              >
                {link.label}
              </a>
            ))}
            <div className="relative mt-3 pt-3 border-t border-purple-500/10" ref={langRefMobile}>
              <button
                type="button"
                onClick={(e) => { e.stopPropagation(); setIsLangOpen((o) => !o) }}
                className="flex items-center justify-between w-full gap-2 px-4 py-3 rounded-lg border-2 border-purple-500/30 bg-purple-500/10 hover:bg-purple-500/20 transition-colors"
              >
                <span className="flex items-center gap-2 text-sm font-medium text-[#c4c1d6]">
                  <img src={currentLang.flag} alt="" className="w-6 h-4 object-cover rounded-sm" />
                  {currentLang.label}
                </span>
                <ChevronDown size={18} className={cn("text-[#c4c1d6] transition-transform shrink-0", isLangOpen && "rotate-180")} />
              </button>
              {isLangOpen && (
                <div className="mt-1 py-1 rounded-lg border border-purple-500/20 bg-[var(--bg-base)] shadow-xl shadow-black/20 overflow-hidden">
                  {languages.map((l) => (
                    <button
                      key={l.code}
                      type="button"
                      onClick={() => { setLang(l.code); setIsLangOpen(false) }}
                      className={cn(
                        "w-full flex items-center gap-2 px-4 py-3 text-left text-sm transition-colors",
                        lang === l.code ? "bg-purple-500/20 text-white" : "text-[#c4c1d6] hover:bg-purple-500/10 hover:text-white"
                      )}
                    >
                      <img src={l.flag} alt="" className="w-5 h-3.5 object-cover rounded-sm" />
                      {l.label}
                    </button>
                  ))}
                </div>
              )}
            </div>
            <Button asChild variant="primary" className="mt-2 font-bold">
              <a href="#contact" onClick={() => setIsMobileMenuOpen(false)}>{t("nav.cta")}</a>
            </Button>
          </div>
        </div>
      )}
    </header>
  )
}
