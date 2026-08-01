export function PageHeader({ pill, title, lead }) {
  return (
    <section className="pt-28 sm:pt-32 pb-8 sm:pb-12 px-4 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-4xl">
        {pill && (
          <div className="inline-flex items-center rounded-full border border-purple-500/20 bg-purple-500/10 px-3 py-1 text-xs font-semibold tracking-wide text-[#c4c1d6]">
            {pill}
          </div>
        )}
        <h1 className="mt-4 text-3xl sm:text-4xl lg:text-5xl font-extrabold text-white tracking-tight">
          {title}
        </h1>
        {lead && (
          <p className="mt-4 text-base sm:text-lg text-[#c4c1d6] leading-relaxed max-w-3xl">
            {lead}
          </p>
        )}
      </div>
    </section>
  )
}
