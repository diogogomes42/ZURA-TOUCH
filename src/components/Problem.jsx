import { useScrollAnimation } from "../lib/useScrollAnimation"
import { Moon, Frown, TrendingDown } from "lucide-react"
import { useLanguage } from "../i18n/LanguageContext"

const icons = [Moon, Frown, TrendingDown]

export function Problem() {
  const [ref, isVisible] = useScrollAnimation(0.1)
  const { t } = useLanguage()
  const problems = t("problem.items")

  return (
    <section
      id="problem"
      className="py-16 sm:py-20 lg:py-24 px-4 sm:px-6 lg:px-8 relative overflow-hidden"
    >
      <div ref={ref} className={`relative mx-auto max-w-6xl ${isVisible ? "animate-in" : "opacity-0"}`}>
        <div className="text-center mb-10 sm:mb-16">
          <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white">
            {t("problem.title")}
          </h2>
          <p className="mt-4 text-base text-[#9b97b3] max-w-xl mx-auto">
            {t("problem.subtitle")}
          </p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-5 sm:gap-6 md:gap-8 relative [&>*:nth-child(3)]:justify-self-center md:[&>*:nth-child(3)]:justify-self-stretch">
          {problems.map((problem, i) => {
            const Icon = icons[i]
            return (
              <div
                key={problem.title}
                className="group relative p-6 sm:p-8 rounded-2xl border border-red-500/20 bg-red-500/5 hover:border-red-500/35 hover:bg-red-500/8 transition-all duration-300 active:scale-[0.99] sm:hover:scale-[1.02]"
              >
                {i < problems.length - 1 && (
                  <div className="hidden md:block absolute top-1/2 -right-4 w-8 h-px bg-gradient-to-r from-red-500/40 to-transparent glow-pulse" />
                )}
                <div className="flex h-14 w-14 items-center justify-center rounded-xl bg-red-500/20 text-red-400 group-hover:bg-red-500/30 transition-colors">
                  <Icon size={28} strokeWidth={2} />
                </div>
                <h3 className="mt-6 text-xl font-semibold text-white">{problem.title}</h3>
                <p className="mt-3 text-sm text-[#c4c1d6] leading-relaxed">{problem.description}</p>
              </div>
            )
          })}
        </div>
      </div>
    </section>
  )
}
