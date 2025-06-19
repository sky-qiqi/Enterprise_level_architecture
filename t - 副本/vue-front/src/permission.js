import router from './router'
import store from './store'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken,removeToken } from '@/utils/token'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register','/forget']

router.beforeEach((to, from, next) => {
    NProgress.start()
    if(getToken()) {
        if (whiteList.indexOf(to.path) !== -1) {
            next(`/`)
        } else {
            next()
        }
    } else {
        // 没有token
        if (whiteList.indexOf(to.path) !== -1) {
            // 在免登录白名单，直接进入
            next()
        } else {
            next(`/login`)
        }
    }
})

router.afterEach(() => {
    NProgress.done()
})
