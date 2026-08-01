import { Check, Building2 } from "lucide-react"
import { motion } from "framer-motion"
import { Button } from "./ui/button"
import { useScrollAnimation } from "../lib/useScrollAnimation"
import { useLanguage } from "../i18n/LanguageContext"

export function HomeVenues() {
  const [ref, isVisible] = useScrollAnimation(0.08)
  const { t } = useLanguage()
  const benefits = t("homeVenues.benefits")

  return (
    <section id="venues" className="py-20 sm:py-28 lg:py-32 px-4 sm:px-6 lg:px-8 relative overflow-hidden">
      <div className="absolute inset-0 pointer-events-none bg-[radial-gradient(ellipse_50%_50%_at_0%_50%,rgba(168,85,247,0.08),transparent)]" aria-hidden />
      <div ref={ref} className={`mx-auto max-w-6xl relative ${isVisible ? "animate-in" : "opacity-0"}`}>
        <div className="grid lg:grid-cols-2 gap-12 lg:gap-20 items-center">
          <div>
            <span className="inline-flex items-center gap-2 rounded-full border border-purple-500/25 bg-purple-500/10 px-4 py-1.5 text-xs font-semibold uppercase tracking-wider text-purple-300">
              <Building2 size={14} />
              {t("homeVenues.badge")}
            </span>
            <h2 className="mt-6 text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white leading-tight">
              {t("homeVenues.title")}
            </h2>
            <ul className="mt-8 space-y-4">
              {benefits.map((benefit, i) => (
                <motion.li
                  key={benefit}
                  initial={{ opacity: 0, x: -16 }}
                  whileInView={{ opacity: 1, x: 0 }}
                  viewport={{ once: true }}
                  transition={{ duration: 0.4, delay: i * 0.08 }}
                  className="flex items-center gap-3 text-[#c4c1d6]"
                >
                  <span className="flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-purple-500/20 border border-purple-500/30">
                    <Check size={14} className="text-purple-300" strokeWidth={2.5} />
                  </span>
                  <span className="text-base sm:text-lg">{benefit}</span>
                </motion.li>
              ))}
            </ul>
            <Button asChild variant="primary" size="lg" className="mt-10 font-bold neon-glow">
              <a href="/contacto/?type=venue">{t("homeVenues.cta")}</a>
            </Button>
          </div>

          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            whileInView={{ opacity: 1, scale: 1 }}
            viewport={{ once: true, amount: 0.3 }}
            transition={{ duration: 0.7, ease: [0.16, 1, 0.3, 1] }}
            className="glass-panel rounded-3xl p-8 sm:p-10 flex items-center justify-center min-h-[280px]"
          >
            <img
              src="/assets/mascot/cool-mascot.png"
              alt=""
              className="w-48 sm:w-64 lg:w-72 h-auto drop-shadow-2xl"
              loading="lazy"
              aria-hidden
            />
          </motion.div>
        </div>
      </div>
    </section>
  )
}
