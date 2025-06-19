import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', component: () => import('@/views/login/Login.vue') },
    { path: '/404', component: () => import('@/views/404.vue') },
    { path: '/:pathMatch(.*)', redirect: '/404' },
    { path: '/index',name:'index' ,component: () => import('@/views/index/index.vue'),  },
    { path: '/register', name: 'register', component:() => import('@/views/register/register.vue'), },
    { path: '/forget', name: 'forget', component: ()=> import('@/views/index/index.vue'),},
    { path: '/attractions', name: 'attractions', component:() => import('@/views/attractions/attractions.vue'), },
    { path: '/line', name: 'line', component:() => import('@/views/line/line.vue'), },
    { path: '/hotel', name: 'hotel', component:() => import('@/views/hotel/hotel.vue'), },
    { path: '/forum', name: 'forum', component:() => import('@/views/forum/forum.vue'), },
    { path: '/favor', name: 'favor', component:() => import('@/views/favor/favor.vue'), },
    { path: '/order', name: 'order',component:() => import('@/views/order/order.vue'), },
    { path: '/attractionsInfo', name: 'attractionsInfo', component:() => import('@/views/attractions/attractionsInfo.vue'), },
    { path: '/hotelInfo', name: 'hotelInfo', component:() => import('@/views/hotel/hotelInfo.vue'), },
    { path: '/forumInfo', name: 'forumInfo', component:() => import('@/views/forum/forumInfo.vue'), },
    { path: '/lineInfo', name: 'lineInfo', component:() => import('@/views/line/lineInfo.vue'), },
    { path: '/center', name: 'center', component:() => import('@/views/center/center.vue'), },
    {path: '/ai', name: 'ai', component:() => import('@/views/ai/ai.vue'), },
    { path: '/rank', name: 'rank', component: () => import('@/views/rank/rank.vue') },
  ]
})

export default router
