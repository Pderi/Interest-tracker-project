<template>
  <header class="header relative z-50 backdrop-blur-xl bg-black/40 border-b border-white/5">
    <div class="flex items-center justify-between h-14 sm:h-16 px-4 sm:px-6 lg:px-8">
      <!-- Logo -->
      <div class="flex items-center space-x-3 cursor-pointer group">
        <div class="w-9 h-9 sm:w-10 sm:h-10 rounded-xl bg-gradient-to-br from-[#00d4ff] to-[#00ffcc] flex items-center justify-center shadow-lg shadow-[#00d4ff]/50 transition-transform group-hover:scale-110 neon-glow rotate-glow">
          <el-icon :size="20" class="text-[#1a1a2e]">
            <Camera />
          </el-icon>
        </div>
        <h1 class="text-lg sm:text-xl font-semibold gradient-text">
          兴趣追踪
        </h1>
      </div>

      <!-- Right Actions -->
      <div class="flex items-center space-x-2 sm:space-x-4">
        <!-- Theme Toggle -->
        <el-button
          :icon="themeStore.theme === 'dark' ? Sunny : Moon"
          circle
          size="small"
          @click="themeStore.toggleTheme()"
          class="!border-[#00d4ff]/30 !bg-[#00d4ff]/10 hover:!bg-[#00d4ff]/20 hover:!border-[#00d4ff]/50 transition-all glow-effect"
        />

        <!-- User Menu -->
        <el-dropdown @command="handleCommand" trigger="click">
          <div class="flex items-center space-x-2 cursor-pointer px-2 py-1 rounded-lg hover:bg-white/5 transition-colors">
            <el-avatar :size="28" :src="userStore.userInfo?.avatar" class="border border-white/10">
              <el-icon><User /></el-icon>
            </el-avatar>
            <span class="text-sm hidden sm:inline">{{ userStore.userInfo?.username || '用户' }}</span>
            <el-icon class="hidden sm:inline"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu class="!bg-black/90 !backdrop-blur-xl !border-white/10">
              <el-dropdown-item command="profile" class="!text-white hover:!bg-white/10">
                <el-icon><User /></el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item divided command="logout" class="!text-white hover:!bg-white/10">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useThemeStore } from '@/store/theme'
import {
  Camera,
  User,
  ArrowDown,
  SwitchButton,
  Sunny,
  Moon,
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const themeStore = useThemeStore()

function handleCommand(command: string) {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    userStore.logout()
  }
}
</script>

<style scoped>
.header {
  position: sticky;
  top: 0;
}
</style>

