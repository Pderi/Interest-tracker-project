<template>
  <aside class="sidebar w-80 lg:w-96 backdrop-blur-xl bg-gradient-to-b from-[#1a1a2e]/70 via-[#16213e]/70 to-[#0f3460]/70 border-r border-[#00d4ff]/20 shadow-2xl shadow-[#00d4ff]/10">
    <div class="p-6">
      <div class="mb-8">
        <h2 class="text-xs font-semibold text-[#00d4ff]/60 uppercase tracking-wider mb-4 px-2 neon-text flicker">导航</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="border-0 bg-transparent"
        text-color="rgba(226, 232, 240, 0.6)"
        active-text-color="#00d4ff"
      >
        <el-menu-item index="/dashboard" class="!rounded-xl !mb-3 !h-14 !text-base">
          <el-icon class="!text-xl"><DataBoard /></el-icon>
          <span class="ml-3">仪表板</span>
        </el-menu-item>

        <el-menu-item index="/photo" class="!rounded-xl !mb-3 !h-14 !text-base">
          <el-icon class="!text-xl"><Camera /></el-icon>
          <span class="ml-3">摄影</span>
        </el-menu-item>

        <el-menu-item index="/movie" class="!rounded-xl !mb-3 !h-14 !text-base">
          <el-icon class="!text-xl"><VideoPlay /></el-icon>
          <span class="ml-3">影视</span>
        </el-menu-item>

        <el-menu-item index="/music" class="!rounded-xl !mb-3 !h-14 !text-base">
          <el-icon class="!text-xl"><Headset /></el-icon>
          <span class="ml-3">音乐</span>
        </el-menu-item>

        <el-menu-item index="/match" class="!rounded-xl !mb-3 !h-14 !text-base">
          <el-icon class="!text-xl"><Trophy /></el-icon>
          <span class="ml-3">球赛</span>
        </el-menu-item>

        <el-menu-item index="/timeline" class="!rounded-xl !mb-3 !h-14 !text-base">
          <el-icon class="!text-xl"><Clock /></el-icon>
          <span class="ml-3">时间线</span>
        </el-menu-item>
      </el-menu>
    </div>
  </aside>
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

const activeMenu = computed(() => route.path)
</script>

<style scoped>
.sidebar {
  position: sticky;
  top: 0;
  height: calc(100vh - 3.5rem);
  overflow-y: auto;
}

:deep(.el-menu-item) {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

:deep(.el-menu-item:hover) {
  background: rgba(0, 212, 255, 0.1) !important;
  transform: translateX(8px);
  color: rgba(0, 212, 255, 0.9) !important;
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.15) 0%, rgba(0, 255, 204, 0.05) 100%) !important;
  color: #00d4ff !important;
  font-weight: 600;
  box-shadow: 
    0 4px 20px rgba(0, 212, 255, 0.3),
    inset 0 0 20px rgba(0, 212, 255, 0.1),
    0 0 10px rgba(0, 212, 255, 0.2);
  border-left: 3px solid #00d4ff;
}

:deep(.el-menu-item.is-active::before) {
  display: none;
}

:deep(.el-menu-item.is-active .el-icon) {
  color: #00d4ff;
  filter: drop-shadow(0 0 8px rgba(0, 212, 255, 0.8));
  animation: icon-glow 2s ease-in-out infinite;
}

@keyframes icon-glow {
  0%, 100% {
    filter: drop-shadow(0 0 8px rgba(0, 212, 255, 0.8));
  }
  50% {
    filter: drop-shadow(0 0 12px rgba(0, 212, 255, 1));
  }
}
</style>

