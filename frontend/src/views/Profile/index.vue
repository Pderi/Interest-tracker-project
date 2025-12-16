<template>
  <div class="profile-page">
    <h1 class="text-3xl font-bold gradient-text mb-6">个人中心</h1>

    <el-card class="glass-effect border-white/10">
      <div class="text-center py-12">
        <el-avatar :size="80" :src="userStore.userInfo?.avatar" class="mb-4">
          <el-icon :size="40"><User /></el-icon>
        </el-avatar>
        <p class="text-xl font-semibold mb-2">{{ userStore.userInfo?.username || '用户' }}</p>
        <p class="text-gray-400 mb-6">{{ userStore.userInfo?.email || '暂无邮箱' }}</p>
        
        <el-button type="primary" @click="handleLogout">
          退出登录
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { User } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

function handleLogout() {
  userStore.logout()
}
</script>

<style scoped>
.profile-page {
  min-height: 100%;
}
</style>

