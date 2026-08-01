import { useState } from "react"
import { motion } from "framer-motion"
import { useLanguage } from "../i18n/LanguageContext"

type MachineId = "big" | "charge" | "mini"

type MachineCopy = {
  id: MachineId
  pillLabel: string
  title: string
  description: string
  screen: string
  bestFor: string
  dimensions: string
  image: string
}

const copy = {
  en: {
    sectionLabel: "Machines",
    heading: "Our Machines",
    machines: [
      {
        id: "mini",
        pillLabel: "ZURA MINI",
        title: "THE ZURA MINI",
        description: "Compact footprint, full experience. Perfect for corridors and smaller spaces.",
        screen: "32\" HD touchscreen",
        bestFor: "Corridors, lobbies, pop-ups",
        dimensions: "103 x 60 x 33 cm",
        image: "/Images/zura-mini.png",
      },
      {
        id: "charge",
        pillLabel: "ZURA",
        title: "THE ZURA",
        description: "The flagship experience. Stunning visuals and smooth gameplay for any venue.",
        screen: "43\" HD touchscreen",
        bestFor: "Shopping malls, hotels, universities",
        dimensions: "180 x 85 x 44 cm",
        image: "/Images/zura.png",
      },
      {
        id: "big",
        pillLabel: "ZURA BIG",
        title: "THE ZURA BIG",
        description: "Maximum screen presence. Built for festivals, large malls, and high-traffic hubs.",
        screen: "55\" HD touchscreen",
        bestFor: "Festivals, large malls, airports",
        dimensions: "185 x 80 x 75 cm",
        image: "/Images/zura-big.png",
      },
    ] as MachineCopy[],
  },
  pt: {
    sectionLabel: "Máquinas",
    heading: "As Nossas Máquinas",
    machines: [
      {
        id: "mini",
        pillLabel: "ZURA MINI",
        title: "A ZURA MINI",
        description: "Compacta mas completa. Ideal para corredores e espaços menores.",
        screen: "Ecrã táctil HD 32\"",
        bestFor: "Corredores, lobbies, pop-ups",
        dimensions: "103 x 60 x 33 cm",
        image: "/Images/zura-mini.png",
      },
      {
        id: "charge",
        pillLabel: "ZURA",
        title: "A ZURA",
        description: "A experiência flagship. Visuais impressionantes e jogo fluido para qualquer espaço.",
        screen: "Ecrã táctil HD 43\"",
        bestFor: "Centros comerciais, hotéis, universidades",
        dimensions: "180 x 85 x 44 cm",
        image: "/Images/zura.png",
      },
      {
        id: "big",
        pillLabel: "ZURA BIG",
        title: "A ZURA BIG",
        description: "Máxima presença visual. Feita para festivais, grandes shoppings e hubs de alto tráfego.",
        screen: "Ecrã táctil HD 55\"",
        bestFor: "Festivais, grandes shoppings, aeroportos",
        dimensions: "185 x 80 x 75 cm",
        image: "/Images/zura-big.png",
      },
    ] as MachineCopy[],
  },
}

const pillBase =
  "px-4 sm:px-5 py-2 rounded-full border text-xs sm:text-sm font-semibold tracking-wide transition-all duration-300"

export function MachinesShowcase() {
  const { lang } = useLanguage()
  const locale = lang === "pt" ? "pt" : "en"
  const localeCopy = copy[locale]

  const [activeId, setActiveId] = useState<MachineId>("mini")

  const activeMachine = (localeCopy.machines as MachineCopy[]).find((m) => m.id === activeId) ?? localeCopy.machines[0]
  const machineImageScale = activeId === "big" ? 1.2 : 1

  return (
    <section className="relative pt-4 sm:pt-8 pb-16 sm:pb-20 lg:pb-24 px-4 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-6xl">
        <motion.div
          className="mb-10 sm:mb-14 flex flex-col gap-4 sm:gap-6 text-center sm:text-left"
          initial={{ opacity: 0, y: 30 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, amount: 0.4 }}
          transition={{ duration: 0.8, ease: [0.16, 1, 0.3, 1] }}
        >
          <p className="text-[11px] sm:text-xs font-semibold uppercase tracking-[0.25em] text-purple-300/80">
            {localeCopy.sectionLabel}
          </p>
          <h2 className="mt-3 text-3xl sm:text-4xl lg:text-5xl font-semibold tracking-tight text-white">
            {localeCopy.heading}
          </h2>
        </motion.div>

        <motion.div
          className="grid lg:grid-cols-[minmax(0,1.1fr)_minmax(0,1.2fr)] gap-10 lg:gap-16 items-start"
          initial={{ opacity: 0, y: 40, scale: 0.97 }}
          whileInView={{ opacity: 1, y: 0, scale: 1 }}
          viewport={{ once: true, amount: 0.3 }}
          transition={{ duration: 0.85, ease: [0.16, 1, 0.3, 1] }}
        >
          <div className="lg:pt-8">
            <div className="inline-flex items-center gap-2 rounded-full bg-black/40 border border-white/5 p-1 mb-8">
              {(localeCopy.machines as MachineCopy[]).map((machine) => {
                const isActive = machine.id === activeId
                return (
                  <motion.button
                    key={machine.id}
                    type="button"
                    onClick={() => setActiveId(machine.id)}
                    className={`${pillBase} ${
                      isActive
                        ? "bg-gradient-to-r from-purple-500 to-pink-500 text-white border-purple-400 shadow-[0_0_30px_rgba(168,85,247,0.5)]"
                        : "bg-transparent text-[#c4c1d6] border-purple-500/30 hover:border-purple-400/70 hover:text-white"
                    }`}
                    whileHover={{ scale: 1.06 }}
                    whileTap={{ scale: 0.95 }}
                    animate={{ scale: isActive ? 1.08 : 1 }}
                    transition={{ type: "spring", stiffness: 420, damping: 26 }}
                  >
                    {machine.pillLabel}
                  </motion.button>
                )
              })}
            </div>

            <motion.div
              key={activeMachine.id}
              initial={{ opacity: 0, x: -24 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.35, ease: "easeOut" }}
            >
              <p className="text-xs font-semibold tracking-[0.18em] uppercase text-purple-300/90 mb-3">
                {activeMachine.pillLabel}
              </p>
              <h3 className="text-3xl sm:text-4xl font-semibold tracking-tight text-white">
                {activeMachine.title}
              </h3>
              <p className="mt-4 text-sm sm:text-base text-[#c4c1d6] max-w-xl">
                {activeMachine.description}
              </p>

              <div className="mt-6 grid grid-cols-1 sm:grid-cols-3 gap-4 sm:gap-5">
                <motion.div
                  key={`screen-${activeMachine.id}`}
                  initial={{ opacity: 0, y: 10, scale: 0.96 }}
                  animate={{ opacity: 1, y: 0, scale: 1 }}
                  transition={{ duration: 0.35, ease: "easeOut" }}
                  className="rounded-2xl border border-purple-500/40 bg-[rgba(30,27,53,0.85)] px-4 py-3"
                >
                  <p className="text-[11px] font-semibold tracking-wide text-purple-200">
                    {locale === "pt" ? "Ecrã" : "Screen"}
                  </p>
                  <p className="mt-1 text-sm text-white">{activeMachine.screen}</p>
                </motion.div>
                <motion.div
                  key={`bestFor-${activeMachine.id}`}
                  initial={{ opacity: 0, y: 10, scale: 0.96 }}
                  animate={{ opacity: 1, y: 0, scale: 1 }}
                  transition={{ duration: 0.35, ease: "easeOut", delay: 0.03 }}
                  className="rounded-2xl border border-purple-500/40 bg-[rgba(30,27,53,0.85)] px-4 py-3"
                >
                  <p className="text-[11px] font-semibold tracking-wide text-purple-200">
                    {locale === "pt" ? "Ideal para" : "Best for"}
                  </p>
                  <p className="mt-1 text-sm text-white">{activeMachine.bestFor}</p>
                </motion.div>
                <motion.div
                  key={`dimensions-${activeMachine.id}`}
                  initial={{ opacity: 0, y: 10, scale: 0.96 }}
                  animate={{ opacity: 1, y: 0, scale: 1 }}
                  transition={{ duration: 0.35, ease: "easeOut", delay: 0.06 }}
                  className="rounded-2xl border border-purple-500/40 bg-[rgba(30,27,53,0.85)] px-4 py-3"
                >
                  <p className="text-[11px] font-semibold tracking-wide text-purple-200">
                    {locale === "pt" ? "Dimensões" : "Dimensions"}
                  </p>
                  <p className="mt-1 text-sm text-white">{activeMachine.dimensions}</p>
                </motion.div>
              </div>

              <p className="mt-5 text-[11px] text-[#9b97b3]">
                {locale === "pt"
                  ? "Especificações sujeitas a ajustes. Configurações personalizadas disponíveis para grandes contas."
                  : "Specs subject to adjustments. Custom configurations available for key accounts."}
              </p>
            </motion.div>
          </div>

          <div className="relative">
            <div
              className="absolute inset-0 -m-10 rounded-[3rem] bg-gradient-to-br from-purple-500/25 via-pink-500/10 to-transparent blur-3xl pointer-events-none"
              aria-hidden
            />
            <motion.img
              key={activeMachine.image}
              src={activeMachine.image}
              alt={activeMachine.title}
              loading="lazy"
              decoding="async"
              className="relative z-10 mx-auto max-h-[460px] sm:max-h-[500px] lg:max-h-[540px] w-auto object-contain drop-shadow-[0_40px_80px_rgba(0,0,0,0.8)]"
              initial={{ opacity: 0, y: 24, scale: 0.94 }}
              animate={{ opacity: 1, y: 0, scale: machineImageScale }}
              transition={{ duration: 0.4, ease: "easeOut" }}
            />
          </div>
        </motion.div>
      </div>
    </section>
  )
}
