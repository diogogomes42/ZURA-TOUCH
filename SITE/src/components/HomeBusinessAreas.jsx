import { Sparkles, Cpu, FlaskConical, ArrowUpRight } from "lucide-react"
import { motion } from "framer-motion"
import { useScrollAnimation } from "../lib/useScrollAnimation"
import { useLanguage } from "../i18n/LanguageContext"
import { Button } from "./ui/button"

const areaConfig = [
  {
    key: "brandExperiences",
    Icon: Sparkles,
    accent: "from-purple-500/30 to-pink-500/15",
    border: "border-purple-500/35 hover:border-purple-400/60",
    glow: "group-hover:shadow-[0_0_32px_rgba(168,85,247,0.22)]",
    iconClass: "text-purple-300",
    anchor: "#brand-experiences",
  },
  {
    key: "interactiveMachines",
    Icon: Cpu,
    accent: "from-cyan-500/25 to-blue-500/10",
    border: "border-cyan-500/35 hover:border-cyan-400/60",
    glow: "group-hover:shadow-[0_0_32px_rgba(34,211,238,0.18)]",
    iconClass: "text-cyan-300",
    anchor: "#interactive-machines",
  },
  {
    key: "zuraTouchLabs",
    Icon: FlaskConical,
    accent: "from-amber-500/20 to-violet-500/15",
    border: "border-amber-500/30 hover:border-amber-400/55",
    glow: "group-hover:shadow-[0_0_32px_rgba(245,158,11,0.15)]",
    iconClass: "text-amber-200",
    anchor: "#zura-touch-labs",
  },
]

export function HomeBusinessAreas() {
  const [ref, isVisible] = useScrollAnimation(0.08)
  const { t } = useLanguage()

  return (
    <section className="py-20 sm:py-28 lg:py-32 px-4 sm:px-6 lg:px-8 relative">
      <div className="absolute inset-0 pointer-events-none bg-[radial-gradient(ellipse_60%_40%_at_50%_100%,rgba(168,85,247,0.06),transparent)]" aria-hidden />
      <div ref={ref} className={`mx-auto max-w-6xl relative ${isVisible ? "animate-in" : "opacity-0"}`}>
        <div className="text-center mb-14 sm:mb-20">
          <p className="text-[11px] sm:text-xs font-semibold uppercase tracking-[0.25em] text-purple-300/80">
            {t("homeBusinessAreas.sectionLabel")}
          </p>
          <h2 className="mt-4 text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white">
            {t("homeBusinessAreas.title")}
          </h2>
          <p className="mt-5 text-lg sm:text-xl text-[#c4c1d6] max-w-3xl mx-auto leading-relaxed">
            {t("homeBusinessAreas.description")}
          </p>
        </div>

        <div className="grid md:grid-cols-3 gap-6 lg:gap-8">
          {areaConfig.map(({ key, Icon, accent, border, glow, iconClass, anchor }, i) => {
            const block = t(`homeBusinessAreas.${key}`)
            return (
              <motion.article
                key={key}
                initial={{ opacity: 0, y: 32 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, amount: 0.3 }}
                transition={{ duration: 0.6, delay: i * 0.1, ease: [0.16, 1, 0.3, 1] }}
                whileHover={{ y: -6, transition: { duration: 0.25 } }}
                className={`glass-card group rounded-3xl p-8 sm:p-10 hover-lift border ${border} ${glow} flex flex-col`}
              >
                <div
                  className={`inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-gradient-to-br ${accent} border border-white/10 transition-shadow duration-300`}
                >
                  <Icon size={26} className={iconClass} strokeWidth={1.75} />
                </div>
                <h3 className="mt-6 text-xl sm:text-2xl font-bold text-white">{block.title}</h3>
                <p className="mt-4 text-sm sm:text-base text-[#c4c1d6] leading-relaxed flex-1">{block.description}</p>
                <Button
                  asChild
                  variant="ghost"
                  className="mt-8 w-fit px-0 text-purple-200 hover:text-white hover:bg-transparent group/btn"
                >
                  <a href={anchor} className="inline-flex items-center gap-2 text-sm font-semibold">
                    {block.cta}
                    <ArrowUpRight size={16} className="transition-transform group-hover/btn:translate-x-0.5 group-hover/btn:-translate-y-0.5" />
                  </a>
                </Button>
              </motion.article>
            )
          })}
        </div>
      </div>
    </section>
  )
}
