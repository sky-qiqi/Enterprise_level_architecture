import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import ElementPlus from 'unplugin-element-plus/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      // !!! 关键修改：importStyle 设置为 'css' !!!
      // 这表示 AutoImport 会自动导入 Element Plus 组件的预编译 CSS 文件。
      resolvers: [ElementPlusResolver({ importStyle: 'css' })],
    }),
    Components({
      // !!! 关键修改：importStyle 设置为 'css' !!!
      resolvers: [ElementPlusResolver({ importStyle: 'css' })],
    }),
    // !!! 关键修改：ElementPlus 插件的配置 !!!
    ElementPlus({
      // 将 useSource 设置为 false，我们不再让 Element Plus 插件编译其 SCSS 源码
      useSource: false,
      // 移除 theme 配置，因为我们不再通过插件来编译 SCSS 主题，
      // 而是通过 CSS 变量在 CSS 层面上覆盖。
      // theme: {
      //   customCss: fileURLToPath(new URL('./src/assets/css/index.scss', import.meta.url)),
      // }
    }),
  ],
  server: {
    host: '0.0.0.0',
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  optimizeDeps: {
    // 这里的 include 路径保持不变，它们都是 CSS 路径，用于优化依赖。
    include: [
      "vue", "vue-router", "axios",
      "element-plus/es/components/base/style/css",
      "element-plus/es/components/message/style/css",
      "element-plus/es/components/message-box/style/css",
      "element-plus/es/components/form/style/css",
      "element-plus/es/components/form-item/style/css",
      "element-plus/es/components/button/style/css",
      "element-plus/es/components/input/style/css",
      "element-plus/es/components/input-number/style/css",
      "element-plus/es/components/switch/style/css",
      "element-plus/es/components/upload/style/css",
      "element-plus/es/components/menu/style/css",
      "element-plus/es/components/col/style/css",
      "element-plus/es/components/icon/style/css",
      "element-plus/es/components/row/style/css",
      "element-plus/es/components/tag/style/css",
      "element-plus/es/components/dialog/style/css",
      "element-plus/es/components/loading/style/css",
      "element-plus/es/components/radio/style/css",
      "element-plus/es/components/radio-group/style/css",
      "element-plus/es/components/popover/style/css",
      "element-plus/es/components/scrollbar/style/css",
      "element-plus/es/components/tooltip/style/css",
      "element-plus/es/components/dropdown/style/css",
      "element-plus/es/components/dropdown-menu/style/css",
      "element-plus/es/components/dropdown-item/style/css",
      "element-plus/es/components/sub-menu/style/css",
      "element-plus/es/components/menu-item/style/css",
      "element-plus/es/components/divider/style/css",
      "element-plus/es/components/card/style/css",
      "element-plus/es/components/link/style/css",
      "element-plus/es/components/breadcrumb/style/css",
      "element-plus/es/components/breadcrumb-item/style/css",
      "element-plus/es/components/table/style/css",
      "element-plus/es/components/tree-select/style/css",
      "element-plus/es/components/table-column/style/css",
      "element-plus/es/components/select/style/css",
      "element-plus/es/components/option/style/css",
      "element-plus/es/components/pagination/style/css",
      "element-plus/es/components/tree/style/css",
      "element-plus/es/components/alert/style/css",
      "element-plus/es/components/radio-button/style/css",
      "element-plus/es/components/checkbox-group/style/css",
      "element-plus/es/components/checkbox/style/css",
      "element-plus/es/components/tabs/style/css",
      "element-plus/es/components/tab-pane/style/css",
      "element-plus/es/components/rate/style/css",
      "element-plus/es/components/date-picker/style/css",
      "element-plus/es/components/notification/style/css",
      "element-plus/es/components/image/style/css",
      "element-plus/es/components/statistic/style/css",
      "element-plus/es/components/watermark/style/css",
      "element-plus/es/components/config-provider/style/css",
      "element-plus/es/components/text/style/css",
      "element-plus/es/components/drawer/style/css",
      "element-plus/es/components/color-picker/style/css",
    ],
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        // !!! 关键修改：清空 additionalData !!!
        // 因为我们不再直接编译 Element Plus 的 SCSS 源码，
        // 且你的 index.scss 也不再包含 @use "sass:color"; 或 @forward Element Plus 的 SCSS。
        additionalData: ``,
      }
    }
  },
})