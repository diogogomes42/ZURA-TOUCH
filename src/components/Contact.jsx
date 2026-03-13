import { useState } from "react"
import { Button } from "./ui/button"
import { Input } from "./ui/input"
import { Textarea } from "./ui/textarea"
import { Card, CardContent } from "./ui/card"
import { useLanguage } from "../i18n/LanguageContext"

export function Contact() {
  const [status, setStatus] = useState("idle")
  const { t, lang } = useLanguage()

  const handleSubmit = async (e) => {
    e.preventDefault()
    const form = e.currentTarget

    const formData = new FormData(form)
    const payload = {
      name: formData.get("name") || "",
      email: formData.get("email") || "",
      company: formData.get("company") || "",
      message: formData.get("message") || "",
    }

    try {
      await fetch(
        "https://script.google.com/macros/s/AKfycbyoLuIRiQmlFJWdbjiXxtTL4rwCciUfrdUabdQlp0pxP_X9N8EyAL1nAxqZRdaUxCl3pA/exec",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          // Using no-cors so the request is sent even if CORS headers are not configured
          mode: "no-cors",
          body: JSON.stringify(payload),
        }
      )

      setStatus("submitted")
      form.reset()
    } catch (error) {
      console.error("Error submitting contact form:", error)
      setStatus("idle")
      // opcional: aqui poderias mostrar uma mensagem de erro ao utilizador
    }
  }

  return (
    <section
      id="contact"
      className="relative overflow-hidden py-16 sm:py-20 lg:py-24 px-4 sm:px-6 lg:px-8"
    >
      <div className="mx-auto max-w-2xl relative">
        <Card className="relative border-purple-500/30 bg-[rgba(30,27,53,0.9)] shadow-2xl shadow-purple-500/10 backdrop-blur-sm">
          <CardContent className="p-5 sm:p-8 lg:p-12">
            <div className="mb-8 text-center">
              <p className="text-[11px] sm:text-xs font-semibold uppercase tracking-[0.25em] text-purple-300/80">
                {lang === "pt" ? "Contacto" : "Contact"}
              </p>
              <h3 className="mt-3 text-2xl sm:text-3xl font-semibold tracking-tight text-white">
                {lang === "pt" ? "Fale connosco sobre o seu espaço" : "Tell us about your venue"}
              </h3>
              <p className="mt-2 text-sm text-[#c4c1d6] max-w-xl mx-auto">
                {lang === "pt"
                  ? "Partilhe o tipo de espaço, localização e o que gostaria de vender. Voltamos em menos de 24 horas."
                  : "Share your venue type, location, and what you'd like to sell. We'll get back to you within 24 hours."}
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
                  <Textarea id="message" name="message" placeholder={t("contact.messagePlaceholder")} required className="mt-2" />
                </div>
                <Button type="submit" variant="primary" size="lg" className="w-full sm:w-auto">
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
