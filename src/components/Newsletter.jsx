import { useState } from "react"
import { Button } from "./ui/button"
import { Input } from "./ui/input"
import { useLanguage } from "../i18n/LanguageContext"

export function Newsletter() {
  const [email, setEmail] = useState("")
  const [status, setStatus] = useState("idle")
  const { t } = useLanguage()

  const handleSubmit = (e) => {
    e.preventDefault()
    setStatus("submitted")
  }

  return (
    <div className="rounded-2xl border border-purple-500/20 bg-gradient-to-br from-purple-500/10 to-pink-500/5 p-8 sm:p-12">
      <h3 className="text-xl sm:text-2xl font-bold text-white">
        {t("newsletter.title")}
      </h3>
      <p className="mt-2 text-sm text-[#c4c1d6]">
        {t("newsletter.description")}
      </p>
      {status === "submitted" ? (
        <p className="mt-4 text-sm text-purple-400">{t("newsletter.thanks")}</p>
      ) : (
        <form onSubmit={handleSubmit} className="mt-6 flex flex-col sm:flex-row gap-3">
          <Input
            type="email"
            placeholder={t("newsletter.placeholder")}
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            className="flex-1 min-w-0"
          />
          <Button type="submit" variant="primary" className="shrink-0">
            {t("newsletter.cta")}
          </Button>
        </form>
      )}
    </div>
  )
}
