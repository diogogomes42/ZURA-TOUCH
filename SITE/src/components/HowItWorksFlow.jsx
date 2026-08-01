import { motion } from "framer-motion"
import { CreditCard, CircleDot, Gift, Sparkles, ChevronDown, ArrowRight } from "lucide-react"
import { useScrollAnimation } from "../lib/useScrollAnimation"
import { useLanguage } from "../i18n/LanguageContext"

const stepIcons = [CreditCard, CircleDot, Gift, Sparkles]

function StepConnector({ vertical = false }) {
  if (vertical) {
    return (
      <div className="flex justify-center py-2 md:hidden" aria-hidden>
        <motion.div
          animate={{ y: [0, 4, 0] }}
          transition={{ duration: 1.5, repeat: Infinity, ease: "easeInOut" }}
        >
          <ChevronDown size={20} className="text-purple-400/70" />
        </motion.div>
      </div>
    )
  }
  return (
    <div className="hidden md:flex items-center justify-center px-1 lg:px-2 shrink-0" aria-hidden>
      <motion.div
        animate={{ x: [0, 4, 0] }}
        transition={{ duration: 1.5, repeat: Infinity, ease: "easeInOut" }}
      >
        <ArrowRight size={20} className="text-purple-400/60" />
      </motion.div>
    </div>
  )
}

export function HowItWorksFlow() {
  const [ref, isVisible] = useScrollAnimation(0.08)
  const { t } = useLanguage()
  const steps = t("howItWorks.steps")

  return (
    <section className="py-20 sm:py-28 lg:py-32 px-4 sm:px-6 lg:px-8">
      <div ref={ref} className={`mx-auto max-w-7xl ${isVisible ? "animate-in" : "opacity-0"}`}>
        <div className="text-center mb-14 sm:mb-20">
          <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white">
            {t("howItWorks.title")}
          </h2>
          <p className="mt-4 text-lg text-[#c4c1d6] max-w-2xl mx-auto">
            {t("howItWorks.subtitle")}
          </p>
        </div>

        {/* Desktop: horizontal flow */}
        <div className="hidden md:flex items-stretch justify-center gap-0">
          {steps.map((step, i) => {
            const Icon = stepIcons[i]
            return (
              <div key={step.title} className="flex items-stretch">
                <motion.div
                  initial={{ opacity: 0, y: 24 }}
                  whileInView={{ opacity: 1, y: 0 }}
                  viewport={{ once: true, amount: 0.4 }}
                  transition={{ duration: 0.55, delay: i * 0.12, ease: [0.16, 1, 0.3, 1] }}
                  whileHover={{ y: -4, transition: { duration: 0.2 } }}
                  className="glass-card rounded-2xl p-6 lg:p-7 w-[200px] lg:w-[220px] flex flex-col hover-lift"
                >
                  <div className="flex items-center gap-3 mb-4">
                    <span className="flex h-8 w-8 items-center justify-center rounded-full bg-gradient-to-r from-purple-500 to-pink-500 text-xs font-bold text-white">
                      {i + 1}
                    </span>
                    <Icon size={20} className="text-purple-300" strokeWidth={1.75} />
                  </div>
                  <h3 className="text-base lg:text-lg font-semibold text-white leading-snug">{step.title}</h3>
                  <p className="mt-2 text-xs lg:text-sm text-[#9b97b3] leading-relaxed flex-1">{step.description}</p>
                </motion.div>
                {i < steps.length - 1 && <StepConnector />}
              </div>
            )
          })}
        </div>

        {/* Mobile: vertical flow */}
        <div className="md:hidden space-y-0 max-w-sm mx-auto">
          {steps.map((step, i) => {
            const Icon = stepIcons[i]
            return (
              <div key={step.title}>
                <motion.div
                  initial={{ opacity: 0, x: -20 }}
                  whileInView={{ opacity: 1, x: 0 }}
                  viewport={{ once: true, amount: 0.4 }}
                  transition={{ duration: 0.5, delay: i * 0.08 }}
                  className="glass-card rounded-2xl p-6"
                >
                  <div className="flex items-center gap-3 mb-3">
                    <span className="flex h-8 w-8 items-center justify-center rounded-full bg-gradient-to-r from-purple-500 to-pink-500 text-xs font-bold text-white">
                      {i + 1}
                    </span>
                    <Icon size={20} className="text-purple-300" />
                  </div>
                  <h3 className="text-lg font-semibold text-white">{step.title}</h3>
                  <p className="mt-2 text-sm text-[#c4c1d6] leading-relaxed">{step.description}</p>
                </motion.div>
                {i < steps.length - 1 && <StepConnector vertical />}
              </div>
            )
          })}
        </div>
      </div>
    </section>
  )
}
