import { motion } from "framer-motion"
import { useScrollAnimation } from "../lib/useScrollAnimation"
import { useLanguage } from "../i18n/LanguageContext"

export function LocationsSection() {
  const [ref, isVisible] = useScrollAnimation(0.1)
  const { t } = useLanguage()
  const badges = t("locations.badges")

  return (
    <section className="py-20 sm:py-28 px-4 sm:px-6 lg:px-8 border-t border-purple-500/10">
      <div ref={ref} className={`mx-auto max-w-6xl text-center ${isVisible ? "animate-in" : "opacity-0"}`}>
        <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white">
          {t("locations.title")}
        </h2>
        <p className="mt-4 text-lg text-[#c4c1d6]">
          {t("locations.subtitle")}
        </p>

        <div className="mt-12 flex flex-wrap justify-center gap-3 sm:gap-4">
          {badges.map((badge, i) => (
            <motion.span
              key={badge}
              initial={{ opacity: 0, scale: 0.9 }}
              whileInView={{ opacity: 1, scale: 1 }}
              viewport={{ once: true }}
              transition={{ duration: 0.35, delay: i * 0.05 }}
              whileHover={{ scale: 1.05 }}
              className="glass-card inline-flex items-center rounded-full px-5 py-2.5 text-sm font-semibold text-[#c4c1d6] hover:text-white transition-colors cursor-default"
            >
              {badge}
            </motion.span>
          ))}
        </div>
      </div>
    </section>
  )
}
