import { useState } from "react"
import { motion } from "framer-motion"
import { useLanguage } from "../i18n/LanguageContext"

type MachineId = "big" | "charge" | "mini"

type MachineCopy = {
  id: MachineId
  pillLabel: string
  title: string
  description: string
  capacity: string
  selection: string
  dimensions: string
  image: string
}

const copy = {
  en: {
    sectionLabel: "Machines",
    heading: "Our Machines",
    subheading: "Choose the ZURA that fits your venue.",
    machines: [
      {
        id: "big",
        pillLabel: "ZURA BIG",
        title: "THE ZURA BIG",
        description: "Maximum capacity. Maximum pulls. The machine that runs the room.",
        capacity: "Capacity: 504 units",
        selection: "Selection: 42 SKUs",
        dimensions: "Dimensions: 185 x 80 x 75 cm",
        image: "/Images/zura-big.png",
      },
      {
        id: "charge",
        pillLabel: "ZURA",
        title: "THE ZURA",
        description: "Powered up and ready. Built for high-traffic locations and fast sales.",
        capacity: "Capacity: 216 units",
        selection: "Selection: 18 SKUs",
        dimensions: "Dimensions: 180 x 85 x 44 cm",
        image: "/Images/zura.png",
      },
      {
        id: "mini",
        pillLabel: "ZURA MINI",
        title: "THE ZURA MINI",
        description: "Compact but powerful. Perfect for smaller spaces with big potential.",
        capacity: "Capacity: 150 units",
        selection: "Selection: 15 SKUs",
        dimensions: "Dimensions: 103 x 60 x 33 cm",
        image: "/Images/zura-mini.png",
      },
    ] as MachineCopy[],
  },
  pt: {
    sectionLabel: "Máquinas",
    heading: "As Nossas Máquinas",
    subheading: "Escolha a ZURA certa para o seu espaço.",
    machines: [
      {
        id: "big",
        pillLabel: "ZURA BIG",
        title: "A ZURA BIG",
        description: "Máxima capacidade. Máximo volume. A máquina que comanda o espaço.",
        capacity: "Capacidade: 504 unidades",
        selection: "Seleção: 42 SKUs",
        dimensions: "Dimensões: 185 x 80 x 75 cm",
        image: "/Images/zura-big.png",
      },
      {
        id: "charge",
        pillLabel: "ZURA",
        title: "A ZURA",
        description: "Sempre carregada. Perfeita para locais de alto tráfego e vendas rápidas.",
        capacity: "Capacidade: 216 unidades",
        selection: "Seleção: 18 SKUs",
        dimensions: "Dimensões: 180 x 85 x 44 cm",
        image: "/Images/zura.png",
      },
      {
        id: "mini",
        pillLabel: "ZURA MINI",
        title: "A ZURA MINI",
        description: "Compacta mas poderosa. Ideal para espaços pequenos com grande potencial.",
        capacity: "Capacidade: 150 unidades",
        selection: "Seleção: 15 SKUs",
        dimensions: "Dimensões: 103 x 60 x 33 cm",
        image: "/Images/zura-mini.png",
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

  const [activeId, setActiveId] = useState<MachineId>("charge")

  const activeMachine = (localeCopy.machines as MachineCopy[]).find((m) => m.id === activeId) ?? localeCopy.machines[0]

  return (
    <section className="relative pt-10 sm:pt-14 lg:pt-16 pb-16 sm:pb-20 lg:pb-24 px-4 sm:px-6 lg:px-8">
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
          <p className="mt-3 text-sm sm:text-base text-[#c4c1d6] max-w-xl mx-auto sm:mx-0">
            {localeCopy.subheading}
          </p>
        </motion.div>

        <motion.div
          className="grid lg:grid-cols-[minmax(0,1.1fr)_minmax(0,1.2fr)] gap-10 lg:gap-16 items-center"
          initial={{ opacity: 0, y: 40, scale: 0.97 }}
          whileInView={{ opacity: 1, y: 0, scale: 1 }}
          viewport={{ once: true, amount: 0.3 }}
          transition={{ duration: 0.85, ease: [0.16, 1, 0.3, 1] }}
        >
          <div>
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
                  key={`capacity-${activeMachine.id}`}
                  initial={{ opacity: 0, y: 10, scale: 0.96 }}
                  animate={{ opacity: 1, y: 0, scale: 1 }}
                  transition={{ duration: 0.35, ease: "easeOut" }}
                  className="rounded-2xl border border-purple-500/40 bg-[rgba(30,27,53,0.85)] px-4 py-3"
                >
                  <p className="text-[11px] font-semibold tracking-wide text-purple-200 flex items-center gap-2">
                    <span className="inline-flex h-5 w-5 items-center justify-center rounded-full bg-purple-500/20 text-purple-100 text-xs">
                      C
                    </span>
                    {locale === "pt" ? "Capacidade" : "Capacity"}
                  </p>
                  <p className="mt-1 text-sm text-white">{activeMachine.capacity}</p>
                </motion.div>
                <motion.div
                  key={`selection-${activeMachine.id}`}
                  initial={{ opacity: 0, y: 10, scale: 0.96 }}
                  animate={{ opacity: 1, y: 0, scale: 1 }}
                  transition={{ duration: 0.35, ease: "easeOut", delay: 0.03 }}
                  className="rounded-2xl border border-purple-500/40 bg-[rgba(30,27,53,0.85)] px-4 py-3"
                >
                  <p className="text-[11px] font-semibold tracking-wide text-purple-200 flex items-center gap-2">
                    <span className="inline-flex h-5 w-5 items-center justify-center rounded-full bg-purple-500/20 text-purple-100 text-xs">
                      S
                    </span>
                    {locale === "pt" ? "Seleção" : "Selection"}
                  </p>
                  <p className="mt-1 text-sm text-white">{activeMachine.selection}</p>
                </motion.div>
                <motion.div
                  key={`dimensions-${activeMachine.id}`}
                  initial={{ opacity: 0, y: 10, scale: 0.96 }}
                  animate={{ opacity: 1, y: 0, scale: 1 }}
                  transition={{ duration: 0.35, ease: "easeOut", delay: 0.06 }}
                  className="rounded-2xl border border-purple-500/40 bg-[rgba(30,27,53,0.85)] px-4 py-3"
                >
                  <p className="text-[11px] font-semibold tracking-wide text-purple-200 flex items-center gap-2">
                    <span className="inline-flex h-5 w-5 items-center justify-center rounded-full bg-purple-500/20 text-purple-100 text-xs">
                      D
                    </span>
                    {locale === "pt" ? "Dimensões" : "Dimensions"}
                  </p>
                  <p className="mt-1 text-sm text-white">{activeMachine.dimensions}</p>
                </motion.div>
              </div>

              <p className="mt-5 text-[11px] text-[#9b97b3]">
                {locale === "pt"
                  ? "Especificações sujeitas a ajustes de produção. Modelos personalizados disponíveis para grandes contas."
                  : "Specs subject to production adjustments. Custom configurations available for key accounts."}
              </p>
            </motion.div>
          </div>

          <div className="relative">
            <div
              className="absolute inset-0 -m-10 rounded-[3rem] bg-gradient-to-br from-purple-500/25 via-pink-500/10 to-transparent blur-3xl pointer-events-none"
              aria-hidden
            />
            <motion.div
              className="relative rounded-[2.25rem] border border-white/10 bg-black/40 px-6 sm:px-8 py-8 sm:py-10 overflow-hidden shadow-[0_40px_120px_rgba(0,0,0,0.65)]"
              initial={{ opacity: 0, y: 32, scale: 0.96 }}
              whileInView={{ opacity: 1, y: 0, scale: 1 }}
              viewport={{ once: true, amount: 0.3 }}
              transition={{ duration: 0.7, ease: "easeOut" }}
            >
              <div className="absolute inset-0 bg-[radial-gradient(circle_at_0%_0%,rgba(168,85,247,0.18),transparent_55%),radial-gradient(circle_at_100%_100%,rgba(236,72,153,0.25),transparent_55%)] opacity-60 pointer-events-none" />

              <motion.img
                key={activeMachine.image}
                src={activeMachine.image}
                alt={activeMachine.title}
                loading="lazy"
                decoding="async"
                className="relative z-10 mx-auto max-h-[460px] sm:max-h-[500px] lg:max-h-[540px] w-auto object-contain drop-shadow-[0_40px_80px_rgba(0,0,0,0.9)]"
                initial={{ opacity: 0, y: 24, scale: 0.94 }}
                animate={{ opacity: 1, y: 0, scale: 1 }}
                transition={{ duration: 0.4, ease: "easeOut" }}
              />
            </motion.div>
          </div>
        </motion.div>
      </div>
    </section>
  )
}

