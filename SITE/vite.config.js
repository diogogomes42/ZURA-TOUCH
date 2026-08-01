import { defineConfig } from 'vite'
import { resolve } from 'node:path'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  build: {
    minify: 'esbuild',
    rollupOptions: {
      input: {
        home: resolve(__dirname, 'index.html'),
        game: resolve(__dirname, 'o-jogo/index.html'),
        venues: resolve(__dirname, 'espacos/index.html'),
        brands: resolve(__dirname, 'marcas/index.html'),
        machines: resolve(__dirname, 'maquinas/index.html'),
        contact: resolve(__dirname, 'contacto/index.html'),
        privacy: resolve(__dirname, 'privacidade/index.html'),
        terms: resolve(__dirname, 'termos/index.html'),
      },
      output: {
        manualChunks: {
          'vendor-react': ['react', 'react-dom'],
          'vendor-motion': ['framer-motion'],
        },
      },
    },
    cssCodeSplit: true,
    sourcemap: false,
  },
})
