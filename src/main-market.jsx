import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import "./index.css"
import { LanguageProvider } from "./i18n/LanguageContext"
import { MarketPage } from "./pages/MarketPage"

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <LanguageProvider>
      <MarketPage />
    </LanguageProvider>
  </StrictMode>
)

