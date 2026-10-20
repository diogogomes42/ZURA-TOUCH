import { Check } from "lucide-react"
import { motion } from "framer-motion"
import { Button } from "./ui/button"
import { useScrollAnimation } from "../lib/useScrollAnimation"
import { useLanguage } from "../i18n/LanguageContext"

const sections = [
  {
    id: "brand-experiences",
    key: "brandExperiences",
    accent: "purple",
    photos: [
      { src: "/Images/experiences/evento-corporativo.jpg", alt: "Zura Touch at a corporate event" },
      { src: "/Images/experiences/casamento.jpg", alt: "Zura Touch at a wedding" },
    ],
  },
  {
    id: "interactive-machines",
    key: "interactiveMachines",
    accent: "cyan",
    image: "/Images/zura-mini.png",
    imageAlt: "Zura Touch interactive machine",
  },
  {
    id: "zura-touch-labs",
    key: "zuraTouchLabs",
    accent: "amber",
    photos: [
      { src: "/Images/labs/shopping-coluna.jpg", alt: "Zura Touch machine in a shopping centre" },
      { src: "/Images/labs/shopping-parede.jpg", alt: "Wall-mounted Zura Touch screen in a shopping centre" },
    ],
  },
]

const accentStyles = {
  purple: {
    badge: "border-purple-500/25 bg-purple-500/10 text-purple-300",
    gradient: "bg-[radial-gradient(ellipse_50%_50%_at_0%_50%,rgba(168,85,247,0.1),transparent)]",
    check: "bg-purple-500/20 border-purple-500/30 text-purple-300",
  },
  cyan: {
    badge: "border-cyan-500/25 bg-cyan-500/10 text-cyan-300",
    gradient: "bg-[radial-gradient(ellipse_50%_50%_at_100%_50%,rgba(34,211,238,0.08),transparent)]",
    check: "bg-cyan-500/20 border-cyan-500/30 text-cyan-300",
  },
  amber: {
    badge: "border-amber-500/25 bg-amber-500/10 text-amber-200",
    gradient: "bg-[radial-gradient(ellipse_50%_50%_at_0%_50%,rgba(245,158,11,0.08),transparent)]",
    check: "bg-amber-500/20 border-amber-500/30 text-amber-200",
  },
}

export function HomeSolutionSections() {
  const [ref, isVisible] = useScrollAnimation(0.05)
  const { t } = useLanguage()

  return (
    <div id="solutions" ref={ref} className={isVisible ? "animate-in" : "opacity-0"}>
      {sections.map(({ id, key, accent, image, imageAlt, photos }, index) => {
        const block = t(`homeSolutions.${key}`)
        const bullets = block.bullets
        const styles = accentStyles[accent]
        const reverse = index % 2 === 1

        return (
          <section
            key={id}
            id={id}
            className="py-20 sm:py-28 lg:py-32 px-4 sm:px-6 lg:px-8 relative overflow-hidden scroll-mt-24"
          >
            <div className={`absolute inset-0 pointer-events-none ${styles.gradient}`} aria-hidden />
            <div className="mx-auto max-w-6xl relative">
              <div className={`grid lg:grid-cols-2 gap-12 lg:gap-20 items-center ${reverse ? "lg:[direction:rtl]" : ""}`}>
                <div className={reverse ? "lg:[direction:ltr]" : ""}>
                  <span className={`inline-flex items-center rounded-full border px-4 py-1.5 text-xs font-semibold uppercase tracking-wider ${styles.badge}`}>
                    {block.badge}
                  </span>
                  <h2 className="mt-6 text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white leading-tight">
                    {block.title}
                  </h2>
                  <p className="mt-5 text-base sm:text-lg text-[#c4c1d6] leading-relaxed">{block.description}</p>
                  <ul className="mt-8 space-y-4">
                    {bullets.map((item, i) => (
                      <motion.li
                        key={item}
                        initial={{ opacity: 0, x: -12 }}
                        whileInView={{ opacity: 1, x: 0 }}
                        viewport={{ once: true }}
                        transition={{ duration: 0.35, delay: i * 0.06 }}
                        className="flex items-start gap-3 text-[#c4c1d6]"
                      >
                        <span className={`flex h-7 w-7 shrink-0 items-center justify-center rounded-full border ${styles.check}`}>
                          <Check size={14} strokeWidth={2.5} />
                        </span>
                        <span className="text-base sm:text-lg pt-0.5">{item}</span>
                      </motion.li>
                    ))}
                  </ul>
                  <Button asChild variant="primary" size="lg" className="mt-10 font-bold neon-glow">
                    <a href={block.ctaHref}>{block.cta}</a>
                  </Button>
                </div>

                <motion.div
                  className={`flex justify-center ${reverse ? "lg:[direction:ltr]" : ""}`}
                  initial={{ opacity: 0, scale: 0.96 }}
                  whileInView={{ opacity: 1, scale: 1 }}
                  viewport={{ once: true, amount: 0.3 }}
                  transition={{ duration: 0.55, ease: "easeOut" }}
                >
                  {photos ? (
                    <div className="relative w-full max-w-xl pb-16 sm:pb-20 pr-6 sm:pr-10">
                      <div className="absolute inset-0 -m-8 rounded-full bg-gradient-to-br from-purple-500/20 via-transparent to-pink-500/10 blur-2xl pointer-events-none" />
                      <img
                        src={photos[0].src}
                        alt={photos[0].alt}
                        loading="lazy"
                        decoding="async"
                        className="relative z-10 aspect-[16/10] w-full rounded-3xl border border-purple-500/25 object-cover shadow-2xl shadow-black/50"
                      />
                      <img
                        src={photos[1].src}
                        alt={photos[1].alt}
                        loading="lazy"
                        decoding="async"
                        className="absolute bottom-0 right-0 z-20 aspect-[16/10] w-[55%] rounded-2xl border-4 border-[#12102a] object-cover shadow-2xl shadow-black/60"
                      />
                    </div>
                  ) : (
                    <div className="relative w-full max-w-md">
                      <div className="absolute inset-0 -m-8 rounded-full bg-gradient-to-br from-purple-500/20 via-transparent to-pink-500/10 blur-2xl pointer-events-none" />
                      <img
                        src={image}
                        alt={imageAlt}
                        loading="lazy"
                        decoding="async"
                        className="relative z-10 mx-auto w-full max-h-[320px] sm:max-h-[380px] object-contain drop-shadow-[0_32px_64px_rgba(0,0,0,0.45)]"
                      />
                    </div>
                  )}
                </motion.div>
              </div>
            </div>
          </section>
        )
      })}
    </div>
  )
}
