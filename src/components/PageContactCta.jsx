import { Button } from "./ui/button"
import { useLanguage } from "../i18n/LanguageContext"

export function PageContactCta() {
  const { lang } = useLanguage()
  return (
    <div className="w-full px-4 sm:px-6 lg:px-8 mt-8 sm:mt-10 pb-12 sm:pb-16 lg:pb-20">
      <div className="mx-auto w-full max-w-6xl">
        <Button
          asChild
          variant="primary"
          size="lg"
          className="flex w-full justify-center font-bold !min-h-[52px] sm:!min-h-[52px]"
        >
          <a href="/contacto/" className="w-full text-center py-3 sm:py-3.5">
            {lang === "pt" ? "Falar connosco" : "Contact us"}
          </a>
        </Button>
      </div>
    </div>
  )
}
