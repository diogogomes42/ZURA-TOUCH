import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import "./index.css"
import { LanguageProvider } from "./i18n/LanguageContext"
import { ServicesPage } from "./pages/ServicesPage"

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <LanguageProvider>
      <ServicesPage />
    </LanguageProvider>
  </StrictMode>
)
