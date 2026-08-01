import { useState, useEffect } from "react"
import { Button } from "./ui/button"
import { Input } from "./ui/input"
import { Textarea } from "./ui/textarea"
import { Card, CardContent } from "./ui/card"
import { cn } from "../lib/utils"
import { useLanguage } from "../i18n/LanguageContext"

const AUDIENCE_TYPES = ["venue", "brand", "other"]

export function Contact() {
  const [status, setStatus] = useState("idle")
  const [audience, setAudience] = useState("venue")
  const { t } = useLanguage()

  useEffect(() => {
    const params = new URLSearchParams(window.location.search)
    const type = params.get("type")
    if (type === "venue" || type === "brand" || type === "other") {
      setAudience(type)
    }
  }, [])

  const messagePlaceholder =
    audience === "venue"
      ? t("contact.messagePlaceholderVenue")
      : audience === "brand"
        ? t("contact.messagePlaceholderBrand")
        : t("contact.messagePlaceholderOther")

  const handleSubmit = async (e) => {
    e.preventDefault()
    const form = e.currentTarget

    const formData = new FormData(form)
    const payload = {
      name: formData.get("name") || "",
      email: formData.get("email") || "",
      company: formData.get("company") || "",
      audience: formData.get("audience") || audience,
      message: formData.get("message") || "",
    }

    setStatus("submitting")

    try {
      fetch(
        "https://script.google.com/macros/s/AKfycbyoLuIRiQmlFJWdbjiXxtTL4rwCciUfrdUabdQlp0pxP_X9N8EyAL1nAxqZRdaUxCl3pA/exec",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          mode: "no-cors",
          body: JSON.stringify(payload),
        }
      ).catch((error) => {
        console.error("Error submitting contact form:", error)
      })

      setStatus("submitted")
      form.reset()
    } catch (error) {
      console.error("Error submitting contact form:", error)
      setStatus("idle")
    }
  }

  return (
    <section id="contact" className="relative overflow-hidden py-16 sm:py-20 lg:py-24 px-4 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-2xl relative">
        <Card className="relative border-purple-500/30 bg-[rgba(30,27,53,0.9)] shadow-2xl shadow-purple-500/10 backdrop-blur-sm">
          <CardContent className="p-5 sm:p-8 lg:p-12">
            <div className="mb-8 text-center">
              <p className="text-[11px] sm:text-xs font-semibold uppercase tracking-[0.25em] text-purple-300/80">
                {t("nav.contact")}
              </p>
              <h3 className="mt-3 text-2xl sm:text-3xl font-semibold tracking-tight text-white">
                {t("contact.title")}
              </h3>
              <p className="mt-2 text-sm text-[#c4c1d6] max-w-xl mx-auto">
                {t("contact.subtitle")}
              </p>
            </div>

            {status === "submitted" ? (
              <div className="py-12 text-center">
                <p className="text-xl font-semibold text-white">{t("contact.thanks")}</p>
                <p className="mt-2 text-[#c4c1d6]">{t("contact.thanksSub")}</p>
              </div>
            ) : (
              <form id="contact-form" onSubmit={handleSubmit} className="space-y-6">
                <div>
                  <p className="block text-sm font-medium text-[#c4c1d6] mb-3">{t("contact.audienceLabel")}</p>
                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-2">
                    {AUDIENCE_TYPES.map((type) => (
                      <button
                        key={type}
                        type="button"
                        onClick={() => setAudience(type)}
                        className={cn(
                          "rounded-lg border px-3 py-2.5 text-sm font-medium transition-colors touch-manipulation",
                          audience === type
                            ? "border-purple-400 bg-purple-500/20 text-white"
                            : "border-purple-500/20 text-[#c4c1d6] hover:border-purple-400/50 hover:text-white"
                        )}
                      >
                        {t(`contact.audience${type.charAt(0).toUpperCase() + type.slice(1)}`)}
                      </button>
                    ))}
                  </div>
                  <input type="hidden" name="audience" value={audience} />
                </div>
                <div>
                  <label htmlFor="name" className="block text-sm font-medium text-[#c4c1d6]">{t("contact.name")}</label>
                  <Input id="name" name="name" placeholder={t("contact.namePlaceholder")} required className="mt-2" />
                </div>
                <div>
                  <label htmlFor="email" className="block text-sm font-medium text-[#c4c1d6]">{t("contact.email")}</label>
                  <Input id="email" name="email" type="email" placeholder={t("contact.emailPlaceholder")} required className="mt-2" />
                </div>
                <div>
                  <label htmlFor="company" className="block text-sm font-medium text-[#c4c1d6]">{t("contact.company")}</label>
                  <Input id="company" name="company" placeholder={t("contact.companyPlaceholder")} className="mt-2" />
                </div>
                <div>
                  <label htmlFor="message" className="block text-sm font-medium text-[#c4c1d6]">{t("contact.message")}</label>
                  <Textarea id="message" name="message" placeholder={messagePlaceholder} required className="mt-2" />
                </div>
                <Button
                  type="submit"
                  variant="primary"
                  size="lg"
                  className="w-full sm:w-auto"
                  disabled={status === "submitting"}
                >
                  {t("contact.submit")}
                </Button>
              </form>
            )}
          </CardContent>
        </Card>
      </div>
    </section>
  )
}
