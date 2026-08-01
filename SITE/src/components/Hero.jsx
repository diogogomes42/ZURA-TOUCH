import { Button } from "./ui/button"
import { motion, useScroll, useTransform } from "framer-motion"
import { useLanguage } from "../i18n/LanguageContext"

export function Hero() {
  const { t } = useLanguage()

  const { scrollYProgress } = useScroll()
  const titleY = useTransform(scrollYProgress, [0, 0.35], [0, -70])
  const imageY = useTransform(scrollYProgress, [0, 0.35], [0, -40])
  const imageScale = useTransform(scrollYProgress, [0, 0.45], [1, 0.92])

  return (
    <section className="relative min-h-[90dvh] sm:min-h-screen flex items-center pt-20 sm:pt-24 pb-12 sm:pb-20 px-4 sm:px-6 lg:px-8 overflow-hidden">
      <div className="noise-overlay" aria-hidden />
      <div
        className="absolute inset-0 pointer-events-none opacity-[0.04] gradient-shift"
        style={{
          background:
            "radial-gradient(circle at 25% 40%, rgba(168, 85, 247, 0.5) 0%, transparent 50%), radial-gradient(circle at 75% 60%, rgba(236, 72, 153, 0.4) 0%, transparent 50%)",
        }}
      />

      <div className="mx-auto max-w-7xl w-full relative">
        <div className="grid lg:grid-cols-2 gap-10 lg:gap-16 items-center">
          <motion.div
            className="text-center lg:text-left order-2 lg:order-1"
            style={{ y: titleY }}
            initial={{ opacity: 0, y: 40 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.85, ease: [0.16, 1, 0.3, 1] }}
          >
            <h1 className="text-4xl min-[400px]:text-5xl sm:text-6xl md:text-7xl lg:text-8xl font-extrabold tracking-tight leading-[1.05]">
              <span className="hero-headline">{t("hero.headline")}</span>
            </h1>
            <p className="mt-5 text-lg sm:text-xl md:text-2xl font-medium text-white/90 tracking-tight">
              {t("hero.subtitle")}
            </p>
            <p className="mt-5 text-base sm:text-lg text-[#c4c1d6] max-w-xl mx-auto lg:mx-0 leading-relaxed">
              {t("hero.description")}
            </p>

            <div className="mt-10 flex flex-col sm:flex-row justify-center lg:justify-start gap-3 sm:gap-4">
              <motion.div
                animate={{
                  boxShadow: [
                    "0 0 0 rgba(168,85,247,0)",
                    "0 0 48px rgba(168,85,247,0.6)",
                    "0 0 0 rgba(168,85,247,0)",
                  ],
                }}
                transition={{ duration: 3, repeat: Infinity, repeatDelay: 4, ease: "easeInOut" }}
                className="rounded-full"
              >
                <Button
                  asChild
                  variant="primary"
                  size="lg"
                  className="px-8 sm:px-10 text-base font-bold btn-glow-pulse neon-glow transition-all duration-300 hover:scale-[1.04]"
                >
                  <a href="/o-jogo/">{t("hero.ctaDiscover")}</a>
                </Button>
              </motion.div>
              <Button
                asChild
                variant="outline"
                size="lg"
                className="px-8 text-base font-semibold border-purple-500/50 text-purple-200 hover:bg-purple-500/10 hover:border-purple-400 backdrop-blur-sm"
              >
                <a href="/marcas/">{t("hero.ctaPartner")}</a>
              </Button>
            </div>
          </motion.div>

          <motion.div
            className="flex justify-center lg:justify-end order-1 lg:order-2 w-full"
            style={{ y: imageY, scale: imageScale }}
            initial={{ opacity: 0, x: 40 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.85, ease: [0.16, 1, 0.3, 1], delay: 0.15 }}
          >
            <div className="relative mx-auto lg:mx-0 w-full max-w-lg lg:max-w-none">
              <div
                className="absolute inset-0 -m-12 sm:-m-20 rounded-full blur-3xl pointer-events-none"
                style={{
                  background:
                    "radial-gradient(circle, rgba(168, 85, 247, 0.4) 0%, rgba(236, 72, 153, 0.2) 40%, transparent 70%)",
                }}
              />
              <motion.img
                src="/Images/zura.png"
                alt="Zura Touch machine"
                fetchPriority="high"
                decoding="async"
                className="relative z-10 mx-auto w-full max-h-[50vh] sm:max-h-[55vh] lg:max-h-[65vh] object-contain drop-shadow-[0_40px_80px_rgba(0,0,0,0.8)]"
                initial={{ opacity: 0, y: 20, scale: 0.96 }}
                animate={{ opacity: 1, y: [0, -10, 0], scale: 1 }}
                transition={{
                  opacity: { duration: 0.85 },
                  y: { duration: 6, repeat: Infinity, ease: "easeInOut" },
                  scale: { duration: 0.85 },
                }}
              />
            </div>
          </motion.div>
        </div>
      </div>
    </section>
  )
}
