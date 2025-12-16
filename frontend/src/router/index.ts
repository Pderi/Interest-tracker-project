import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false,
    },
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    meta: {
      requiresAuth: true,
    },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: {
          title: '仪表板',
        },
      },
      {
        path: 'photo',
        name: 'Photo',
        component: () => import('@/views/Photo/index.vue'),
        meta: {
          title: '摄影',
        },
      },
      {
        path: 'movie',
        name: 'Movie',
        component: () => import('@/views/Movie/index.vue'),
        meta: {
          title: '影视',
        },
      },
      {
        path: 'music',
        name: 'Music',
        component: () => import('@/views/Music/index.vue'),
        meta: {
          title: '音乐',
        },
      },
      {
        path: 'match',
        name: 'Match',
        component: () => import('@/views/Match/index.vue'),
        meta: {
          title: '球赛',
        },
      },
      {
        path: 'timeline',
        name: 'Timeline',
        component: () => import('@/views/Timeline/index.vue'),
        meta: {
          title: '时间线',
        },
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile/index.vue'),
        meta: {
          title: '个人中心',
        },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 兴趣追踪平台`
  }

  // 检查是否需要登录
  if (to.meta.requiresAuth && !userStore.isLoggedIn()) {
    next({
      path: '/login',
      query: { redirect: to.fullPath },
    })
  } else if (to.path === '/login' && userStore.isLoggedIn()) {
    // 已登录用户访问登录页，重定向到首页
    next('/')
  } else {
    next()
  }
})

export default router

