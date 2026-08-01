import { Gamepad2, Building2, Handshake } from "lucide-react"
import { motion } from "framer-motion"
import { useScrollAnimation } from "../lib/useScrollAnimation"
import { useLanguage } from "../i18n/LanguageContext"

const icons = [Gamepad2, Building2, Handshake]
const keys = ["players", "venues", "brands"]

export function WhatIsZura() {
  const [ref, isVisible] = useScrollAnimation(0.08)
  const { t } = useLanguage()

  return (
    <section className="py-20 sm:py-28 lg:py-32 px-4 sm:px-6 lg:px-8 relative">
      <div className="absolute inset-0 pointer-events-none bg-[radial-gradient(ellipse_60%_40%_at_50%_100%,rgba(168,85,247,0.06),transparent)]" aria-hidden />
      <div ref={ref} className={`mx-auto max-w-6xl relative ${isVisible ? "animate-in" : "opacity-0"}`}>
        <div className="text-center mb-14 sm:mb-20">
          <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white">
            {t("whatIs.title")}
          </h2>
          <p className="mt-5 text-lg sm:text-xl text-[#c4c1d6] max-w-3xl mx-auto leading-relaxed">
            {t("whatIs.description")}
          </p>
        </div>

        <div className="grid md:grid-cols-3 gap-6 lg:gap-8">
          {keys.map((key, i) => {
            const Icon = icons[i]
            const block = t(`ecosystem.${key}`)
            return (
              <motion.div
                key={key}
                initial={{ opacity: 0, y: 32 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, amount: 0.3 }}
                transition={{ duration: 0.6, delay: i * 0.1, ease: [0.16, 1, 0.3, 1] }}
                whileHover={{ y: -6, transition: { duration: 0.25 } }}
                className="glass-card group rounded-3xl p-8 sm:p-10 hover-lift border-gradient-top"
              >
                <div className="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-gradient-to-br from-purple-500/25 to-pink-500/15 border border-purple-500/30 group-hover:shadow-[0_0_30px_rgba(168,85,247,0.25)] transition-shadow duration-300">
                  <Icon size={26} className="text-purple-300" strokeWidth={1.75} />
                </div>
                <h3 className="mt-6 text-xl sm:text-2xl font-bold text-white">{block.title}</h3>
                <p className="mt-4 text-sm sm:text-base text-[#c4c1d6] leading-relaxed">{block.description}</p>
              </motion.div>
            )
          })}
        </div>
      </div>
    </section>
  )
}
