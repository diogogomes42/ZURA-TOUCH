import { Mail, MapPin, Instagram, Linkedin } from "lucide-react"
import { useLanguage } from "../i18n/LanguageContext"

export function Footer() {
  const { t } = useLanguage()

  const navLinks = [
    { href: "#problem", label: t("nav.problem") },
    { href: "#solution", label: t("nav.solution") },
    { href: "#market", label: t("nav.market") },
    { href: "#business-model", label: t("businessModel.title") },
    { href: "#how-it-works", label: t("nav.howItWorks") },
    { href: "#who-its-for", label: t("nav.whoItsFor") },
    { href: "#contact", label: t("nav.contact") },
  ]

  return (
    <footer className="border-t border-purple-500/10">
      <div className="py-12 sm:py-16 px-4 sm:px-6 lg:px-8">
        <div className="mx-auto max-w-6xl">
          <div className="grid gap-10 sm:gap-12 md:grid-cols-2 lg:grid-cols-4">
            <div>
              <a href="#" className="inline-block">
                <img src="/logo.png" alt="Zura Touch" className="h-12 w-auto" />
              </a>
              <p className="mt-4 text-sm text-[#c4c1d6]">
                {t("footer.description")}
              </p>
              <div className="mt-4 flex gap-4">
                <a
                  href="https://www.instagram.com/zuratouch/"
                  target="_blank"
                  rel="noreferrer"
                  className="text-[#9b97b3] hover:text-purple-400 transition-colors"
                  aria-label="Instagram"
                >
                  <Instagram size={20} />
                </a>
                <a
                  href="https://linkedin.com/in/zura-touch-aba8ba3b2"
                  target="_blank"
                  rel="noreferrer"
                  className="text-[#9b97b3] hover:text-purple-400 transition-colors"
                  aria-label="LinkedIn"
                >
                  <Linkedin size={20} />
                </a>
              </div>
            </div>

            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-[#9b97b3]">{t("footer.navigation")}</p>
              <ul className="mt-4 space-y-2">
                {navLinks.map((link) => (
                  <li key={link.href}>
                    <a href={link.href} className="block py-1.5 text-sm text-[#c4c1d6] hover:text-white transition-colors touch-manipulation">
                      {link.label}
                    </a>
                  </li>
                ))}
              </ul>
            </div>

            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-[#9b97b3]">{t("footer.contact")}</p>
              <ul className="mt-4 space-y-3">
                <li>
                  <a
                    href="mailto:info@zuratouch.pt"
                    className="flex items-center gap-2 text-sm text-[#c4c1d6] hover:text-white transition-colors"
                  >
                    <Mail size={16} />
                    info@zuratouch.pt
                  </a>
                </li>
                <li>
                  <a
                    href="tel:+351931708796"
                    className="text-sm text-[#c4c1d6] hover:text-white transition-colors"
                  >
                    +351 931 708 796
                  </a>
                </li>
                <li>
                  <span className="flex items-center gap-2 text-sm text-[#c4c1d6]">
                    <MapPin size={16} />
                    Porto, Portugal
                  </span>
                </li>
              </ul>
            </div>

            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-[#9b97b3]">{t("footer.legal")}</p>
              <ul className="mt-4 space-y-2">
                <li>
                  <a href="#privacy" className="text-sm text-[#c4c1d6] hover:text-white transition-colors">
                    {t("footer.privacy")}
                  </a>
                </li>
                <li>
                  <a href="#terms" className="text-sm text-[#c4c1d6] hover:text-white transition-colors">
                    {t("footer.terms")}
                  </a>
                </li>
              </ul>
            </div>
          </div>

          <div className="mt-12 pt-8 border-t border-purple-500/10">
            <p className="text-sm text-[#9b97b3]">
              © {new Date().getFullYear()} Zura Touch. {t("footer.copyright")}
            </p>
          </div>
        </div>
      </div>
    </footer>
  )
}
