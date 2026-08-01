import { Button } from "./ui/button"
import { useLanguage } from "../i18n/LanguageContext"

export function PageContactCta({ audience = "venue" }) {
  const { t } = useLanguage()
  const href = audience === "brand" ? "/contacto/?type=brand" : "/contacto/?type=venue"
  const label =
    audience === "brand" ? t("brands.cta") : t("venues.cta")

  return (
    <div className="w-full px-4 sm:px-6 lg:px-8 mt-8 sm:mt-10 pb-12 sm:pb-16 lg:pb-20">
      <div className="mx-auto w-full max-w-6xl">
        <Button
          asChild
          variant="primary"
          size="lg"
          className="flex w-full justify-center font-bold !min-h-[52px] sm:!min-h-[52px]"
        >
          <a href={href} className="w-full text-center py-3 sm:py-3.5">
            {label}
          </a>
        </Button>
      </div>
    </div>
  )
}
