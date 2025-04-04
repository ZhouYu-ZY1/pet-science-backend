import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { formatDate } from './utils/utils'
// 引入中文语言包
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

const app = createApp(App)
// 注册全局函数
app.config.globalProperties.$formatDate = formatDate

app.use(router)
// 配置Element Plus使用中文
app.use(ElementPlus, {
  locale: zhCn
})
app.mount('#app')
