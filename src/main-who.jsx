import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import "./index.css"
import { LanguageProvider } from "./i18n/LanguageContext"
import { WhoItsForPage } from "./pages/WhoItsForPage"

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <LanguageProvider>
      <WhoItsForPage />
    </LanguageProvider>
  </StrictMode>
)

