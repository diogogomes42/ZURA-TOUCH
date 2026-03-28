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
        services: resolve(__dirname, 'servicos/index.html'),
        machines: resolve(__dirname, 'maquinas/index.html'),
        problem: resolve(__dirname, 'problema/index.html'),
        solution: resolve(__dirname, 'solucao/index.html'),
        market: resolve(__dirname, 'mercado/index.html'),
        business: resolve(__dirname, 'negocio/index.html'),
        how: resolve(__dirname, 'como-funciona/index.html'),
        who: resolve(__dirname, 'para-quem/index.html'),
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
