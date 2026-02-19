import { useEffect } from "react"
import { X } from "lucide-react"
import { useLanguage } from "../i18n/LanguageContext"
import { cn } from "../lib/utils"

export function LegalModal({ type, onClose }) {
  const { t, lang } = useLanguage()
  const title = t(`${type}.title`)
  const lastUpdated = t(`${type}.lastUpdated`)
  const intro = t(`${type}.intro`)
  const sections = t(`${type}.sections`)
  const contact = t(`${type}.contact`)

  useEffect(() => {
    const handleEscape = (e) => {
      if (e.key === "Escape") onClose()
    }
    document.addEventListener("keydown", handleEscape)
    document.body.style.overflow = "hidden"
    return () => {
      document.removeEventListener("keydown", handleEscape)
      document.body.style.overflow = ""
    }
  }, [onClose])

  return (
    <div
      className="fixed inset-0 z-[100] flex items-start justify-center overflow-y-auto bg-black/60 backdrop-blur-sm p-4 sm:p-6 pt-24"
      onClick={onClose}
    >
      <div
        className={cn(
          "relative w-full max-w-2xl rounded-2xl border border-purple-500/20 bg-[var(--bg-base)] shadow-2xl",
          "text-[#f8f7fc]"
        )}
        onClick={(e) => e.stopPropagation()}
      >
        <button
          type="button"
          onClick={onClose}
          className="absolute top-4 right-4 p-2 rounded-lg text-[#9b97b3] hover:text-white hover:bg-purple-500/10 transition-colors"
          aria-label={lang === "pt" ? "Fechar" : "Close"}
        >
          <X size={24} />
        </button>

        <div className="p-6 sm:p-8 lg:p-10">
          <h1 className="text-2xl sm:text-3xl font-bold text-white pr-12">
            {title}
          </h1>
          <p className="mt-2 text-sm text-[#9b97b3]">
            {lastUpdated}: {new Date().toLocaleDateString(lang === "pt" ? "pt-PT" : "en-GB", { day: "numeric", month: "long", year: "numeric" })}
          </p>

          <p className="mt-6 text-[#c4c1d6] leading-relaxed">
            {intro}
          </p>

          <div className="mt-8 space-y-6">
            {sections?.map((section, i) => (
              <div key={i}>
                <h2 className="text-lg font-semibold text-white">
                  {section.title}
                </h2>
                <p className="mt-2 text-[#c4c1d6] leading-relaxed">
                  {section.content}
                </p>
              </div>
            ))}
          </div>

          <p className="mt-8 text-sm text-[#c4c1d6]">
            {contact}
          </p>
        </div>
      </div>
    </div>
  )
}
