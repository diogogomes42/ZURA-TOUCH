import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import "./index.css"
import { LanguageProvider } from "./i18n/LanguageContext"
import { BrandsPage } from "./pages/BrandsPage"

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <LanguageProvider>
      <BrandsPage />
    </LanguageProvider>
  </StrictMode>
)
