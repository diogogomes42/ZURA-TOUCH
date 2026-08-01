import { createContext, useContext, useState, useEffect } from "react"
import { translations } from "./translations"

const STORAGE_KEY = "zura-touch-lang"

const LanguageContext = createContext(null)

export function LanguageProvider({ children }) {
  const [lang, setLangState] = useState(() => {
    if (typeof window !== "undefined") {
      const stored = localStorage.getItem(STORAGE_KEY)
      if (stored === "pt" || stored === "en") return stored

      // Auto-detect language for first-time visitors:
      // - Prefer Portuguese when browser/OS languages include "pt" (Portugal or other lusophone locales).
      // - Fallback to English otherwise.
      const navLangs = (navigator.languages?.length ? navigator.languages : [navigator.language]).filter(Boolean)
      const hasPortuguese = navLangs.some((l) => String(l).toLowerCase().startsWith("pt"))
      if (hasPortuguese) return "pt"
    }
    return "en"
  })

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, lang)
    document.documentElement.lang = lang === "pt" ? "pt-PT" : "en"
    const { title, description } = translations[lang].meta
    if (document.title !== title) {
      document.title = title
    }
    if (description) {
      let metaDescription = document.querySelector('meta[name="description"]')
      if (!metaDescription) {
        metaDescription = document.createElement("meta")
        metaDescription.setAttribute("name", "description")
        document.head.appendChild(metaDescription)
      }
      metaDescription.setAttribute("content", description)

      for (const [property, value] of [
        ["og:title", title],
        ["og:description", description],
        ["twitter:title", title],
        ["twitter:description", description],
      ]) {
        let tag = document.querySelector(`meta[property="${property}"], meta[name="${property}"]`)
        if (!tag) {
          tag = document.createElement("meta")
          tag.setAttribute(property.startsWith("twitter:") ? "name" : "property", property)
          document.head.appendChild(tag)
        }
        tag.setAttribute("content", value)
      }
    }
  }, [lang])

  const setLang = (newLang) => {
    if (newLang !== "pt" && newLang !== "en") return
    if (newLang === lang) return

    // Persist immediately, then force a full refresh to avoid any stale UI state
    // where some sections/cards don't re-render correctly after a language switch.
    try {
      localStorage.setItem(STORAGE_KEY, newLang)
    } catch {}

    if (typeof window !== "undefined") {
      window.location.reload()
      return
    }

    setLangState(newLang)
  }

  const t = (path) => {
    const keys = path.split(".")
    let value = translations[lang]
    for (const key of keys) {
      value = value?.[key]
    }
    return value ?? path
  }

  return (
    <LanguageContext.Provider value={{ lang, setLang, t }}>
      {children}
    </LanguageContext.Provider>
  )
}

export function useLanguage() {
  const ctx = useContext(LanguageContext)
  if (!ctx) throw new Error("useLanguage must be used within LanguageProvider")
  return ctx
}
