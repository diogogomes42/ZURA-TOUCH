import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import "./index.css"
import { LanguageProvider } from "./i18n/LanguageContext"
import { MachinesPage } from "./pages/MachinesPage"

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <LanguageProvider>
      <MachinesPage />
    </LanguageProvider>
  </StrictMode>
)
