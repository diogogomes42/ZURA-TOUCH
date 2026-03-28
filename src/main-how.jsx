import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import "./index.css"
import { LanguageProvider } from "./i18n/LanguageContext"
import { HowItWorksPage } from "./pages/HowItWorksPage"

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <LanguageProvider>
      <HowItWorksPage />
    </LanguageProvider>
  </StrictMode>
)

