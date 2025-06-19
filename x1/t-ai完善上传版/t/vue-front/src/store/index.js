
import { createStore } from 'vuex'
import { logout } from '@/api/front.js'
import msg from './msg.js'
import configure from './configure.js'

export default createStore({
  state: () => ({  // 改为函数形式返回状态对象
    HOST: 'http://localhost:8080',
  }),
  actions: {
    logout({ commit }) {
      return new Promise((resolve) => {
        logout().then(res => {
          // 建议添加清理逻辑（根据你的业务需求）
        }).catch(error => {
          console.error('登出失败:', error)
        }).finally(() => {
          resolve()  // 确保始终执行 resolve
        })
      })
    }
  },
  modules: {
    msg,
    configure
  }
})
