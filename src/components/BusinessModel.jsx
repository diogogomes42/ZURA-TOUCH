import { motion } from "framer-motion"
import { staggerContainer, fadeInUpItem } from "../lib/motionConfig"
import { Button } from "./ui/button"
import { Handshake, Megaphone, Building2 } from "lucide-react"
import { useLanguage } from "../i18n/LanguageContext"

const icons = [Handshake, Megaphone, Building2]

export function BusinessModel() {
  const { t } = useLanguage()
  const models = t("businessModel.items")

  return (
    <section id="business-model" className="py-16 sm:py-20 lg:py-24 px-4 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-6xl">
        <motion.div
          className="text-center mb-10 sm:mb-16"
          initial={{ opacity: 0, y: 22 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, amount: 0.35, margin: "-80px" }}
          transition={{ duration: 0.75, ease: [0.16, 1, 0.3, 1] }}
        >
          <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white">
            {t("businessModel.title")}
          </h2>
          <p className="mt-4 text-lg text-[#c4c1d6] max-w-2xl mx-auto">
            {t("businessModel.subtitle")}
          </p>
        </motion.div>

        <motion.div
          className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-5 sm:gap-6 md:gap-8 lg:gap-10 [&>*:nth-child(3)]:justify-self-center md:[&>*:nth-child(3)]:justify-self-stretch"
          variants={staggerContainer}
          initial="hidden"
          whileInView="show"
          viewport={{ once: true, amount: 0.35, margin: "-80px" }}
        >
          {models.map((model, i) => {
            const Icon = icons[i]
            return (
              <motion.div
                key={model.badge || model.title}
                variants={fadeInUpItem}
                whileHover={{ y: -6, scale: 1.03, boxShadow: "0 24px 70px rgba(0,0,0,0.65)" }}
                whileTap={{ scale: 0.97, y: 0 }}
                transition={{ type: "spring", stiffness: 420, damping: 26 }}
                className="group relative p-6 sm:p-8 rounded-2xl border border-purple-500/20 bg-gradient-to-br from-purple-500/10 to-transparent hover:border-purple-500/40 hover:from-purple-500/15 transition-all duration-300"
              >
                <p className="text-xs font-bold uppercase tracking-[0.2em] text-purple-400/90">
                  {model.badge}
                </p>
                <div className="mt-4 flex h-12 w-12 items-center justify-center rounded-xl bg-purple-500/20 text-purple-400 group-hover:bg-purple-500/30 transition-colors transform-gpu group-hover:-translate-y-0.5 group-hover:scale-105">
                  <Icon size={24} strokeWidth={2} />
                </div>
                <h3 className="mt-6 text-xl font-bold text-white">{model.title}</h3>
                <p className="mt-3 text-sm text-[#c4c1d6]">{model.description}</p>
                <Button asChild variant="primary" size="sm" className="mt-6">
                  <a href="#contact">{model.cta} →</a>
                </Button>
              </motion.div>
            )
          })}
        </motion.div>
      </div>
    </section>
  )
}
