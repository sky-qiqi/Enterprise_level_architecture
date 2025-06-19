import { createStore } from 'vuex'
import user from './modules/user'
import menu from './modules/menu'
import configure from './modules/configure'

export default createStore({
  modules: {
    user,
    menu,
    configure
  }
})