import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import '@/assets/css/index.scss'
import '@/assets/css/global.css'
import store from './store'
import 'element-plus/dist/index.css';
import '@/assets/css/global.css';
import 'quill/dist/quill.core.css';
import 'quill/dist/quill.snow.css';
import 'quill/dist/quill.bubble.css';
import './permission'


const app = createApp(App)

app.use(router)
app.use(ElementPlus, { locale: zhCn })
app.use(store)
app.mount('#app')


import * as ElementPlusIconsVue from '@element-plus/icons-vue'

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
