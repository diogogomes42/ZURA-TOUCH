import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import "./index.css"
import { LanguageProvider } from "./i18n/LanguageContext"
import { PrivacyPage } from "./pages/PrivacyPage"

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <LanguageProvider>
      <PrivacyPage />
    </LanguageProvider>
  </StrictMode>
)
