<template>
  <nav class="mobile-nav fixed bottom-0 left-0 right-0 z-50 lg:hidden backdrop-blur-xl bg-black/80 border-t border-white/10">
    <div class="flex items-center justify-around h-16 px-2">
      <router-link
        v-for="item in navItems"
        :key="item.path"
        :to="item.path"
        class="flex flex-col items-center justify-center flex-1 h-full rounded-lg transition-all duration-200"
        :class="isActive(item.path) ? 'text-purple-400' : 'text-gray-400'"
        active-class="text-purple-400"
      >
        <el-icon :size="22">
          <component :is="item.icon" />
        </el-icon>
        <span class="text-xs mt-1">{{ item.label }}</span>
        <div
          v-if="isActive(item.path)"
          class="absolute bottom-0 left-1/2 transform -translate-x-1/2 w-8 h-0.5 bg-gradient-to-r from-purple-400 to-pink-400 rounded-full"
        ></div>
      </router-link>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  DataBoard,
  Camera,
  VideoPlay,
  Headset,
  Trophy,
  Clock,
} from '@element-plus/icons-vue'

const route = useRoute()

const navItems = [
  { path: '/dashboard', label: '仪表板', icon: DataBoard },
  { path: '/photo', label: '摄影', icon: Camera },
  { path: '/movie', label: '影视', icon: VideoPlay },
  { path: '/music', label: '音乐', icon: Headset },
  { path: '/match', label: '球赛', icon: Trophy },
  { path: '/timeline', label: '时间线', icon: Clock },
]

function isActive(path: string): boolean {
  return route.path === path
}
</script>

<style scoped>
.mobile-nav {
  padding-bottom: env(safe-area-inset-bottom);
}

.router-link-active {
  @apply text-purple-400;
}
</style>

