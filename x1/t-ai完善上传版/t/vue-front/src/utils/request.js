import axios from 'axios'
import store from '@/store'
import { ElMessageBox } from 'element-plus'
const instance = axios.create({
    baseURL: '/api',
    timeout: 5000
})

instance.interceptors.request.use(
    function (config) {
        if (config.method === 'post') {
            config.data = {
                ...config.data,
                _t: Date.parse(new Date()) / 1000
            }
        } else if (config.method === 'get') {
            config.params = {
                random: Math.random(),
                ...config.params
            }
        }
        config.headers.x_access_token = window.localStorage.getItem("user_token")
        return config
    },
    function (err) {
        return Promise.request(err)
    }
)

instance.interceptors.response.use(
    function (response) {
        const res = response
        if (res.status === 1011) {
            ElMessageBox.alert('系统登录已过期，请重新登录', '错误', {
                confirmButtonText: '确定'
            }).then(() => {
                store.dispatch('logout').then(() => {
                    window.localStorage.removeItem("user_token")
                    window.localStorage.removeItem("user_info")
                    location.reload()
                })
            })
            return Promise.reject('error')
        } else if(res.data.code === 1009) {
            ElMessageBox.alert('该账号已被锁定', '错误', {
                confirmButtonText: '确定'
            }).then(() => {
                store.dispatch('logout').then(() => {
                    window.localStorage.removeItem("user_token")
                    window.localStorage.removeItem("user_info")
                    location.reload()
                })
            })
            return Promise.reject('error')
        } else if (res.status !== 200) {
            ElMessageBox.alert('系统内部错误，请联系管理员维护', '错误', {
                confirmButtonText: '确定'
            }).then(() => {
                store.dispatch('logout').then(() => {
                    window.localStorage.removeItem("user_token")
                    window.localStorage.removeItem("user_info")
                    location.reload()
                })
            })
            return Promise.reject('error')
        } else {
            return res.data
        }
    },
    function (err) {
        ElMessageBox.alert('系统内部错误，请联系管理员维护', '错误', {
            confirmButtonText: '确定'
        })
        window.localStorage.removeItem("user_token")
        window.localStorage.removeItem("user_info")
        location.reload()
        return Promise.reject('error')
    }
)

export function get(url, params) {
    return instance.get(url, {params})
}

export function post(url, data) {
    return instance.post(url, data)
}

export default instance;
