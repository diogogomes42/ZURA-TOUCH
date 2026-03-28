import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import "./index.css"
import { LanguageProvider } from "./i18n/LanguageContext"
import { ProblemPage } from "./pages/ProblemPage"

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <LanguageProvider>
      <ProblemPage />
    </LanguageProvider>
  </StrictMode>
)

