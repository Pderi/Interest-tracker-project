<template>
  <router-view />
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useThemeStore } from '@/store/theme'
import { useUserStore } from '@/store/user'

const themeStore = useThemeStore()
const userStore = useUserStore()

onMounted(() => {
  // 初始化主题
  themeStore.initTheme()
  
  // 如果已登录，获取用户信息
  if (userStore.isLoggedIn()) {
    userStore.fetchUserInfo()
  }
})
</script>

<style scoped></style>

