import { useScrollAnimation } from "../lib/useScrollAnimation"
import { useLanguage } from "../i18n/LanguageContext"

export function HowItWorksPreview() {
  const [ref, isVisible] = useScrollAnimation(0.1)
  const { t } = useLanguage()
  const steps = t("howItWorks.steps")

  return (
    <section className="py-16 sm:py-20 lg:py-24 px-4 sm:px-6 lg:px-8 relative">
      <div ref={ref} className={`mx-auto max-w-6xl ${isVisible ? "animate-in" : "opacity-0"}`}>
        <div className="text-center mb-12 sm:mb-16">
          <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white">
            {t("howItWorks.title")}
          </h2>
          <p className="mt-4 text-lg text-[#c4c1d6] max-w-2xl mx-auto">
            {t("howItWorks.subtitle")}
          </p>
        </div>

        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {steps.map((step, i) => (
            <div key={step.title} className="relative">
              <div className="rounded-2xl border border-purple-500/20 bg-[rgba(30,27,53,0.5)] p-6 h-full">
                <span className="inline-flex items-center justify-center w-8 h-8 rounded-full bg-gradient-to-r from-purple-500 to-pink-500 text-sm font-bold text-white">
                  {i + 1}
                </span>
                <h3 className="mt-4 text-lg font-semibold text-white">{step.title}</h3>
                <p className="mt-2 text-sm text-[#c4c1d6] leading-relaxed">{step.description}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}
