import {
  Gamepad2,
  Gift,
  Wifi,
  MapPin,
  Cpu,
  Layers,
} from "lucide-react"
import { motion } from "framer-motion"
import { useScrollAnimation } from "../lib/useScrollAnimation"
import { useLanguage } from "../i18n/LanguageContext"

const icons = [Gamepad2, Gift, Wifi, MapPin, Cpu, Layers]

export function WhyZura() {
  const [ref, isVisible] = useScrollAnimation(0.08)
  const { t } = useLanguage()
  const items = t("whyZura.items")

  return (
    <section className="py-20 sm:py-28 lg:py-32 px-4 sm:px-6 lg:px-8">
      <div ref={ref} className={`mx-auto max-w-6xl ${isVisible ? "animate-in" : "opacity-0"}`}>
        <div className="text-center mb-14 sm:mb-20">
          <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white">
            {t("whyZura.title")}
          </h2>
          <p className="mt-4 text-lg text-[#c4c1d6] max-w-2xl mx-auto">
            {t("whyZura.subtitle")}
          </p>
        </div>

        <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-5 lg:gap-6">
          {items.map((item, i) => {
            const Icon = icons[i]
            return (
              <motion.div
                key={item.title}
                initial={{ opacity: 0, y: 28 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, amount: 0.2 }}
                transition={{ duration: 0.5, delay: i * 0.07, ease: [0.16, 1, 0.3, 1] }}
                whileHover={{ y: -5, transition: { duration: 0.2 } }}
                className="glass-card group rounded-2xl p-6 sm:p-7 hover-lift border-gradient-top"
              >
                <div className="inline-flex h-11 w-11 items-center justify-center rounded-xl bg-gradient-to-br from-purple-500/20 to-pink-500/10 border border-purple-500/25 group-hover:shadow-[0_0_24px_rgba(168,85,247,0.2)] transition-shadow">
                  <Icon size={22} className="text-purple-300" strokeWidth={1.75} />
                </div>
                <h3 className="mt-4 text-lg font-semibold text-white">{item.title}</h3>
                <p className="mt-2 text-sm text-[#9b97b3] leading-relaxed">{item.description}</p>
              </motion.div>
            )
          })}
        </div>
      </div>
    </section>
  )
}
