import { useEffect } from "react"
import { X } from "lucide-react"
import { useLanguage } from "../i18n/LanguageContext"
import { cn } from "../lib/utils"
import { LegalContent } from "./LegalContent"

export function LegalModal({ type, onClose }) {
  const { lang } = useLanguage()

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

        <LegalContent type={type} />
      </div>
    </div>
  )
}
