import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import "./index.css"
import { LanguageProvider } from "./i18n/LanguageContext"
import { TermsPage } from "./pages/TermsPage"

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <LanguageProvider>
      <TermsPage />
    </LanguageProvider>
  </StrictMode>
)
