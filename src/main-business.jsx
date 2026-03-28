import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import "./index.css"
import { LanguageProvider } from "./i18n/LanguageContext"
import { BusinessPage } from "./pages/BusinessPage"

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <LanguageProvider>
      <BusinessPage />
    </LanguageProvider>
  </StrictMode>
)

