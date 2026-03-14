import { Button } from "./ui/button"
import { Check } from "lucide-react"
import { motion, useScroll, useTransform } from "framer-motion"
import { useLanguage } from "../i18n/LanguageContext"

export function Hero() {
  const { t, lang } = useLanguage()

  const bullets = t("hero.bullets")

  const { scrollYProgress } = useScroll()
  const titleY = useTransform(scrollYProgress, [0, 0.35], [0, -70])
  const imageY = useTransform(scrollYProgress, [0, 0.35], [0, -40])
  const imageScale = useTransform(scrollYProgress, [0, 0.45], [1.02, 0.9])

  return (
    <section className="relative min-h-[85dvh] sm:min-h-screen flex items-center pt-20 sm:pt-24 pb-12 sm:pb-20 px-4 sm:px-6 lg:px-8 overflow-hidden">
      <div className="noise-overlay" aria-hidden />
      <div
        className="absolute inset-0 pointer-events-none opacity-[0.03] gradient-shift"
        style={{
          background:
            "radial-gradient(circle at 30% 50%, rgba(168, 85, 247, 0.45) 0%, transparent 55%), radial-gradient(circle at 70% 50%, rgba(236, 72, 153, 0.35) 0%, transparent 55%)",
        }}
      />

      <div className="mx-auto max-w-7xl w-full relative">
        <div className="grid lg:grid-cols-2 gap-12 lg:gap-20 items-center">
          <motion.div
            className="text-center lg:text-left order-2 lg:order-1"
            style={{ y: titleY }}
            initial={{ opacity: 0, y: 40 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.85, ease: [0.16, 1, 0.3, 1] }}
          >
            <p className="text-[11px] sm:text-xs font-semibold uppercase tracking-[0.25em] text-[#9b97b3]">
              {lang === "pt" ? "Vending Inteligente" : "Smart Vending"}
            </p>
            <h1 className="mt-3 text-3xl min-[400px]:text-4xl sm:text-5xl md:text-6xl lg:text-7xl font-bold tracking-tight leading-[1.15]">
              <span className="block text-[#9b97b3] font-semibold text-[0.8em]">
                {t("hero.tagline")}
              </span>
              <span className="block mt-3 gradient-text text-white">
                {t("hero.title")}
              </span>
            </h1>
            <p className="mt-6 text-lg sm:text-xl text-[#c4c1d6] max-w-xl mx-auto lg:mx-0 leading-relaxed">
              {t("hero.description")}
            </p>

            {bullets?.length > 0 && (
              <ul className="mt-6 flex flex-wrap justify-center lg:justify-start gap-x-6 gap-y-2 text-sm text-[#9b97b3]">
                {bullets.map((bullet) => (
                  <li key={bullet} className="flex items-center gap-2">
                    <Check size={16} className="shrink-0 text-purple-400" strokeWidth={2.4} />
                    <span>{bullet}</span>
                  </li>
                ))}
              </ul>
            )}

            <div className="mt-8 flex flex-col sm:flex-row justify-center lg:justify-start gap-3 sm:gap-4">
              <motion.div
                animate={{
                  boxShadow: [
                    "0 0 0 rgba(168,85,247,0)",
                    "0 0 40px rgba(168,85,247,0.75)",
                    "0 0 0 rgba(168,85,247,0)",
                  ],
                }}
                transition={{
                  duration: 3,
                  repeat: Infinity,
                  repeatDelay: 4,
                  ease: "easeInOut",
                }}
                className="rounded-full"
              >
                <Button
                  asChild
                  variant="primary"
                  size="lg"
                  className="px-10 sm:px-12 text-lg font-bold btn-glow-pulse neon-glow transition-all duration-300 hover:scale-[1.04]"
                >
                  <a href="#contact">{t("hero.cta")}</a>
                </Button>
              </motion.div>
              <Button
                asChild
                variant="outline"
                size="lg"
                className="px-8 text-sm sm:text-base font-semibold border-purple-500/50 text-purple-200 hover:bg-purple-500/10 hover:border-purple-400"
              >
                <a href="#how-it-works">
                  {lang === "pt" ? "Ver Como Funciona" : "See How It Works"}
                </a>
              </Button>
            </div>
          </motion.div>

          <motion.div
            className="flex justify-center lg:justify-end order-1 lg:order-2 w-full"
            style={{ y: imageY, scale: imageScale }}
            initial={{ opacity: 0, x: 40 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.85, ease: [0.16, 1, 0.3, 1], delay: 0.12 }}
          >
            <div className="relative mx-auto lg:mx-0">
              <div
                className="absolute inset-0 -m-16 rounded-full blur-3xl pointer-events-none"
                style={{
                  background:
                    "radial-gradient(circle, rgba(168, 85, 247, 0.35) 0%, rgba(59, 130, 246, 0.16) 35%, rgba(236, 72, 153, 0.12) 55%, transparent 75%)",
                }}
              />
              <motion.img
                src="/assets/mascot/hero-mascot.png"
                alt="Zura Touch mascot"
                fetchPriority="high"
                decoding="async"
                className="relative w-88 max-w-[98vw] sm:w-[28rem] md:w-[36rem] lg:w-[44rem] xl:w-[50rem] h-auto drop-shadow-[0_40px_80px_rgba(0,0,0,0.9)]"
                initial={{ opacity: 0, y: 30, scale: 0.95 }}
                animate={{ opacity: 1, y: [0, -14, 0], scale: 1 }}
                transition={{
                  opacity: { duration: 0.85, ease: [0.16, 1, 0.3, 1] },
                  y: { duration: 5.5, repeat: Infinity, ease: "easeInOut" },
                  scale: { duration: 0.85, ease: [0.16, 1, 0.3, 1] },
                }}
              />
            </div>
          </motion.div>
        </div>
      </div>
    </section>
  )
}
