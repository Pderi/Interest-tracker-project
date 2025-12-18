import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo } from '@/types/api'
import { getUserInfo, logout as logoutApi } from '@/api/user'
import router from '@/router'

// 获取 token（优先 localStorage，其次 sessionStorage）
function getStoredToken(): string {
  return localStorage.getItem('token') || sessionStorage.getItem('token') || ''
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(getStoredToken())
  const userInfo = ref<UserInfo | null>(null)

  // 设置token（默认存 localStorage，Login 页面会根据"记住我"调整）
  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 设置用户信息
  function setUserInfo(info: UserInfo) {
    userInfo.value = info
  }

  // 获取用户信息
  async function fetchUserInfo() {
    try {
      const res = await getUserInfo()
      if (res.data) {
        setUserInfo(res.data)
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  // 登出
  async function logout() {
    try {
      await logoutApi()
    } catch (error) {
      console.error('登出失败:', error)
    } finally {
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      sessionStorage.removeItem('token')
      router.push('/login')
    }
  }

  // 检查是否已登录
  function isLoggedIn(): boolean {
    return !!token.value
  }

  return {
    token,
    userInfo,
    setToken,
    setUserInfo,
    fetchUserInfo,
    logout,
    isLoggedIn,
  }
})

