<template>
  <div class="dashboard min-h-full">
    <!-- Hero Section -->
    <div class="mb-8 sm:mb-12 float-animation">
      <h1 class="text-4xl sm:text-5xl lg:text-6xl font-bold mb-4 neon-text leading-tight">
        仪表板
      </h1>
      <p class="text-gray-400 text-sm sm:text-base">欢迎回来，查看你的兴趣记录概览</p>
    </div>
    
    <!-- Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6 mb-6 sm:mb-8">
      <div 
        v-for="(stat, index) in stats" 
        :key="index"
        class="stat-card group relative overflow-hidden rounded-2xl backdrop-blur-xl bg-gradient-to-br from-white/5 to-white/[0.02] border border-white/10 p-6 transition-all duration-500 hover:scale-[1.02] hover:border-[#00d4ff]/30 hover:shadow-xl hover:shadow-[#00d4ff]/10"
        :style="{ animationDelay: `${index * 100}ms` }"
      >
        <div class="absolute inset-0 bg-gradient-to-br from-[#00d4ff]/0 to-[#00ffcc]/0 group-hover:from-[#00d4ff]/3 group-hover:to-[#00ffcc]/3 transition-all duration-500"></div>
          <div class="relative z-10 flex items-center justify-between">
            <div class="flex-1">
              <p class="text-gray-400 text-xs sm:text-sm mb-2 font-medium">{{ stat.label }}</p>
              <p class="text-3xl sm:text-4xl font-bold gradient-text">
                {{ stat.value }}
              </p>
              <p class="text-gray-500 text-xs mt-1">{{ stat.desc }}</p>
            </div>
            <div class="ml-4 w-12 h-12 sm:w-14 sm:h-14 rounded-xl bg-gradient-to-br from-[#00d4ff]/20 to-[#00ffcc]/20 border border-[#00d4ff]/30 flex items-center justify-center transition-transform group-hover:scale-110 group-hover:rotate-6 group-hover:border-[#00d4ff]/50 group-hover:bg-gradient-to-br group-hover:from-[#00d4ff]/30 group-hover:to-[#00ffcc]/30">
              <el-icon :size="24" class="text-[#00d4ff]">
                <component :is="stat.icon" />
              </el-icon>
            </div>
          </div>
      </div>
    </div>

    <!-- Recent Activity -->
    <div class="rounded-2xl backdrop-blur-xl bg-gradient-to-br from-white/5 to-white/[0.02] border border-white/10 overflow-hidden">
      <div class="p-4 sm:p-6 border-b border-white/10">
        <div class="flex items-center justify-between">
          <h2 class="text-lg sm:text-xl font-semibold text-white">最近活动</h2>
          <el-button text type="primary" size="small" class="!text-[#00d4ff] hover:!text-[#00ffcc] glow-effect">
            查看全部
            <el-icon class="ml-1"><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
      <div class="divide-y divide-white/10">
        <div
          v-for="activity in recentActivities"
          :key="activity.id"
          class="p-4 sm:p-6 hover:bg-white/5 transition-colors cursor-pointer group"
        >
          <div class="flex items-start">
            <div class="flex-shrink-0 w-10 h-10 sm:w-12 sm:h-12 rounded-xl bg-gradient-to-br from-[#00d4ff]/20 to-[#00ffcc]/20 border border-[#00d4ff]/30 flex items-center justify-center mr-4 group-hover:scale-110 transition-transform">
              <el-icon :size="20" :class="activity.color">
                <component :is="activity.icon" />
              </el-icon>
            </div>
            <div class="flex-1 min-w-0">
              <h3 class="text-white font-semibold text-sm sm:text-base mb-1">{{ activity.title }}</h3>
              <p class="text-gray-400 text-xs sm:text-sm mb-1">{{ activity.desc }}</p>
              <p class="text-gray-500 text-xs">{{ activity.time }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { markRaw } from 'vue'
import {
  Camera,
  VideoPlay,
  Headset,
  Trophy,
  Document,
  ArrowRight,
} from '@element-plus/icons-vue'

const stats = ref([
  {
    label: '照片总数',
    value: '128',
    desc: '张照片',
    icon: markRaw(Camera),
    iconBg: 'from-purple-500 to-purple-600',
  },
  {
    label: '影视记录',
    value: '42',
    desc: '部作品',
    icon: markRaw(VideoPlay),
    iconBg: 'from-pink-500 to-pink-600',
  },
  {
    label: '音乐记录',
    value: '356',
    desc: '首歌曲',
    icon: markRaw(Headset),
    iconBg: 'from-blue-500 to-blue-600',
  },
  {
    label: '球赛记录',
    value: '18',
    desc: '场比赛',
    icon: markRaw(Trophy),
    iconBg: 'from-green-500 to-green-600',
  },
])

// 最近活动数据
const recentActivities = ref([
  {
    id: 1,
    type: 'photo',
    title: '上传了新照片',
    desc: '日落时分',
    time: '2小时前',
    icon: markRaw(Camera),
    color: 'text-purple-400',
  },
  {
    id: 2,
    type: 'movie',
    title: '观看了《星际穿越》',
    desc: '评分：9.5',
    time: '5小时前',
    icon: markRaw(VideoPlay),
    color: 'text-pink-400',
  },
  {
    id: 3,
    type: 'music',
    title: '添加了《Blinding Lights》',
    desc: 'The Weeknd',
    time: '1天前',
    icon: markRaw(Headset),
    color: 'text-blue-400',
  },
  {
    id: 4,
    type: 'match',
    title: '观看了比赛',
    desc: '巴萨 vs 皇马',
    time: '2天前',
    icon: markRaw(Trophy),
    color: 'text-green-400',
  },
  {
    id: 5,
    type: 'photo',
    title: '上传了新照片',
    desc: '城市夜景',
    time: '3天前',
    icon: markRaw(Camera),
    color: 'text-purple-400',
  },
])
</script>

<style scoped>
.dashboard {
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-card {
  animation: slideUp 0.6s ease-out both;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

