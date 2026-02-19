import { createContext, useContext, useState, useEffect } from "react"
import { translations } from "./translations"

const STORAGE_KEY = "zura-touch-lang"

const LanguageContext = createContext(null)

export function LanguageProvider({ children }) {
  const [lang, setLangState] = useState(() => {
    if (typeof window !== "undefined") {
      const stored = localStorage.getItem(STORAGE_KEY)
      if (stored === "pt" || stored === "en") return stored
    }
    return "en"
  })

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, lang)
    document.documentElement.lang = lang === "pt" ? "pt-PT" : "en"
    const title = translations[lang].meta.title
    if (document.title !== title) {
      document.title = title
    }
  }, [lang])

  const setLang = (newLang) => {
    if (newLang === "pt" || newLang === "en") setLangState(newLang)
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
