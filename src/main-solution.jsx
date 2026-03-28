import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import "./index.css"
import { LanguageProvider } from "./i18n/LanguageContext"
import { SolutionPage } from "./pages/SolutionPage"

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <LanguageProvider>
      <SolutionPage />
    </LanguageProvider>
  </StrictMode>
)

