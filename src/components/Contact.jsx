import { useState } from "react"
import { Button } from "./ui/button"
import { Input } from "./ui/input"
import { Textarea } from "./ui/textarea"
import { Card, CardContent } from "./ui/card"
import { useLanguage } from "../i18n/LanguageContext"

export function Contact() {
  const [status, setStatus] = useState("idle")
  const { t } = useLanguage()

  const handleSubmit = (e) => {
    e.preventDefault()
    setStatus("submitted")
  }

  return (
    <section
      id="contact"
      className="relative overflow-hidden py-16 sm:py-20 lg:py-24 px-4 sm:px-6 lg:px-8"
    >
      <div className="mx-auto max-w-2xl relative">
        <Card className="relative border-purple-500/30 bg-gradient-to-br from-purple-500/10 via-[rgba(30,27,53,0.8)] to-pink-500/10 shadow-2xl shadow-purple-500/10 backdrop-blur-sm">
          <CardContent className="p-5 sm:p-8 lg:p-12">
            {status === "submitted" ? (
              <div className="py-12 text-center">
                <p className="text-xl font-semibold text-white">{t("contact.thanks")}</p>
                <p className="mt-2 text-[#c4c1d6]">{t("contact.thanksSub")}</p>
              </div>
            ) : (
              <form onSubmit={handleSubmit} className="space-y-6">
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
