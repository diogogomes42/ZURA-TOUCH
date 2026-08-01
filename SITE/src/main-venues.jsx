import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import "./index.css"
import { LanguageProvider } from "./i18n/LanguageContext"
import { VenuesPage } from "./pages/VenuesPage"

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <LanguageProvider>
      <VenuesPage />
    </LanguageProvider>
  </StrictMode>
)
