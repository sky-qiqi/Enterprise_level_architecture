import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "", name: 'main', component:()=>import('@/views/layout/index') ,
      redirect: '/index',
      children: [
        { path: 'index', name: '首页', component: ()=>import('@/views/system/index/index.vue'),},
        { path: 'attractions', name: '景点管理',component: ()=>import('@/views/system/attractions/attractions.vue'),},
        { path: 'line', name: '线路管理',component: ()=>import('@/views/system/line/line.vue'),},
        { path: 'hotel', name: '酒店管理', component: ()=>import('@/views/system/hotel/hotel.vue'),},
        { path: 'hotelItem', name: '房型管理', component: ()=>import('@/views/system/hotel/hotelItem.vue'),},
        { path: 'forum', name: '资讯管理',component: ()=>import('@/views/system/forum/forum.vue'),},
        { path: 'comments', name: '景点评论', component: ()=>import('@/views/system/comments/comments.vue'),},
        { path: 'order', name: '景点预约', component: ()=>import('@/views/system/attractions/order.vue'),},
        { path: 'orderHotel', name: '酒店预约',component: ()=>import('@/views/system/hotel/order.vue'),},
        { path: 'rotations', name: '轮播图管理',component: ()=>import('@/views/system/rotations/rotations.vue'),},
        { path: 'admin', name: '管理员管理', component: ()=>import('@/views/system/user/admin.vue'),},
        { path: 'user', name: '普通用户管理', component: ()=>import('@/views/system/user/user.vue'),},
        { path: 'center', name: '个人中心', component: ()=>import('@/views/system/center/center.vue'),}
      ]
    },
    { path: '/login', name: '登录', component: ()=>import('@/views/system/login/login.vue'),},
    { path: '/404', name: '404',component: ()=>import('@/views/error/404.vue'),}
  ]
})

export default router
