import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import "./index.css"
import { LanguageProvider } from "./i18n/LanguageContext"
import { ContactPage } from "./pages/ContactPage"

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <LanguageProvider>
      <ContactPage />
    </LanguageProvider>
  </StrictMode>
)

