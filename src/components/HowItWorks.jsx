import { useScrollAnimation } from "../lib/useScrollAnimation"
import { Wrench, Users, Coins } from "lucide-react"
import { useLanguage } from "../i18n/LanguageContext"

const icons = [Wrench, Users, Coins]
const numbers = ["01", "02", "03"]

export function HowItWorks() {
  const [ref, isVisible] = useScrollAnimation(0.1)
  const { t } = useLanguage()
  const steps = t("howItWorks.steps")

  return (
    <section id="how-it-works" className="py-16 sm:py-20 lg:py-24 px-4 sm:px-6 lg:px-8">
      <div ref={ref} className={`mx-auto max-w-6xl ${isVisible ? "animate-in" : "opacity-0"}`}>
        <div className="grid items-center gap-6 sm:gap-8 lg:grid-cols-[minmax(0,1.3fr)_minmax(0,0.7fr)] lg:gap-8">
          <div>
          <div className="text-center mb-10 sm:mb-16">
            <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white">
              {t("howItWorks.title")}
            </h2>
            <p className="mt-4 text-lg text-[#c4c1d6] max-w-2xl mx-auto">
              {t("howItWorks.subtitle")}
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 xl:grid-cols-3 gap-5 sm:gap-6 lg:gap-6 [&>*:nth-child(3)]:justify-self-center xl:[&>*:nth-child(3)]:justify-self-stretch">
            {steps.map((step, i) => {
              const Icon = icons[i]
              return (
                <div
                  key={step.title}
                  className={`relative ${isVisible ? (i === 0 ? "animate-in" : i === 1 ? "animate-in-delay-1" : "animate-in-delay-2") : "opacity-0"}`}
                >
                  {i < steps.length - 1 && (
                    <div className="hidden md:block absolute top-16 left-[60%] w-[80%] h-px bg-gradient-to-r from-purple-500/50 to-transparent" />
                  )}
                  <div className="p-6 sm:p-8 rounded-2xl border border-purple-500/20 bg-[rgba(30,27,53,0.6)] hover:border-purple-500/30 transition-all h-full backdrop-blur-sm">
                    <p className="text-4xl font-bold text-purple-500/50">{numbers[i]}</p>
                    <div className="mt-4 flex h-12 w-12 items-center justify-center rounded-xl bg-purple-500/20 text-purple-400">
                      <Icon size={24} strokeWidth={2} />
                    </div>
                    <h3 className="mt-6 text-xl font-semibold text-white">{step.title}</h3>
                    <p className="mt-3 text-[#c4c1d6]">{step.description}</p>
                  </div>
                </div>
              )
            })}
          </div>
          </div>

          <div className="flex justify-center lg:justify-end lg:-ml-2 xl:-ml-4 lg:mt-16">
            <img
              src="/assets/mascot/coding-mascot.png"
              alt=""
              className="w-80 h-auto sm:w-[24rem] lg:w-[28rem] xl:w-[30rem] lg:translate-y-12"
              loading="lazy"
              aria-hidden
            />
          </div>
        </div>
      </div>
    </section>
  )
}
