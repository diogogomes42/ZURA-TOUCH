import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import "./index.css"
import { LanguageProvider } from "./i18n/LanguageContext"
import { GamePage } from "./pages/GamePage"

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <LanguageProvider>
      <GamePage />
    </LanguageProvider>
  </StrictMode>
)
