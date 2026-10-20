import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import "./index.css"
import { LanguageProvider } from "./i18n/LanguageContext"
import { LabsPage } from "./pages/LabsPage"

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <LanguageProvider page="labs">
      <LabsPage />
    </LanguageProvider>
  </StrictMode>
)
