import { useLanguage } from "../i18n/LanguageContext"

export function LegalContent({ type }) {
  const { t, lang } = useLanguage()
  const title = t(`${type}.title`)
  const lastUpdated = t(`${type}.lastUpdated`)
  const intro = t(`${type}.intro`)
  const sections = t(`${type}.sections`)
  const contact = t(`${type}.contact`)

  return (
    <div className="p-6 sm:p-8 lg:p-10">
      <h1 className="text-2xl sm:text-3xl font-bold text-white pr-12">{title}</h1>
      <p className="mt-2 text-sm text-[#9b97b3]">
        {lastUpdated}:{" "}
        {new Date().toLocaleDateString(lang === "pt" ? "pt-PT" : "en-GB", {
          day: "numeric",
          month: "long",
          year: "numeric",
        })}
      </p>

      <p className="mt-6 text-[#c4c1d6] leading-relaxed">{intro}</p>

      <div className="mt-8 space-y-6">
        {sections?.map((section, i) => (
          <div key={i}>
            <h2 className="text-lg font-semibold text-white">{section.title}</h2>
            <p className="mt-2 text-[#c4c1d6] leading-relaxed">{section.content}</p>
          </div>
        ))}
      </div>

      <p className="mt-8 text-sm text-[#c4c1d6]">{contact}</p>
    </div>
  )
}

