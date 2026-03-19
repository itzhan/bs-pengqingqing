import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// 清除系统代理，防止 Vite 代理请求走系统代理（如 Clash）
delete process.env.http_proxy
delete process.env.HTTP_PROXY
delete process.env.https_proxy
delete process.env.HTTPS_PROXY
delete process.env.all_proxy
delete process.env.ALL_PROXY

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5174,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8100',
        changeOrigin: true
      },
      '/uploads': {
        target: 'http://127.0.0.1:8100',
        changeOrigin: true
      }
    }
  }
})
