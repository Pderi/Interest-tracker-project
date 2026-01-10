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
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6 mb-6 sm:mb-8">
      <AnimatedCard
        v-for="(stat, index) in stats"
        :key="index"
        variant="glass"
        class="stat-card"
        :style="{ animationDelay: `${index * 100}ms` }"
      >
        <div class="p-6">
          <div class="flex items-center justify-between">
            <div class="flex-1">
              <p class="text-gray-400 text-xs sm:text-sm mb-2 font-medium">{{ stat.label }}</p>
              <p class="text-3xl sm:text-4xl font-bold gradient-text">
                <span class="number-counter">{{ stat.value }}</span>
              </p>
              <p class="text-gray-500 text-xs mt-1">{{ stat.desc }}</p>
            </div>
            <div class="ml-4 w-12 h-12 sm:w-14 sm:h-14 rounded-xl bg-gradient-to-br from-[#00d4ff]/20 to-[#00ffcc]/20 border border-[#00d4ff]/30 flex items-center justify-center transition-all duration-300 hover:scale-110 hover:rotate-6 hover:border-[#00d4ff]/50 hover:bg-gradient-to-br hover:from-[#00d4ff]/30 hover:to-[#00ffcc]/30 icon-container">
              <el-icon :size="24" class="text-[#00d4ff]">
                <component :is="stat.icon" />
              </el-icon>
            </div>
          </div>
        </div>
      </AnimatedCard>
    </div>

    <!-- Recent Activity -->
    <AnimatedCard variant="glass" class="overflow-hidden">
      <div class="p-4 sm:p-6 border-b border-white/10">
        <div class="flex items-center justify-between">
          <h2 class="text-lg sm:text-xl font-semibold text-white">最近活动</h2>
          <AnimatedButton variant="outline" size="small" @click="handleViewAll">
            查看全部
            <el-icon class="ml-1"><ArrowRight /></el-icon>
          </AnimatedButton>
        </div>
      </div>
      <div class="divide-y divide-white/10">
        <div
          v-for="(activity, index) in recentActivities"
          :key="activity.id"
          class="activity-item p-4 sm:p-6 hover:bg-white/5 transition-all duration-300 cursor-pointer group"
          :style="{ animationDelay: `${index * 50}ms` }"
        >
          <div class="flex items-start">
            <div class="flex-shrink-0 w-10 h-10 sm:w-12 sm:h-12 rounded-xl bg-gradient-to-br from-[#00d4ff]/20 to-[#00ffcc]/20 border border-[#00d4ff]/30 flex items-center justify-center mr-4 transition-all duration-300 group-hover:scale-110 group-hover:rotate-6 activity-icon">
              <el-icon :size="20" :class="activity.color">
                <component :is="activity.icon" />
              </el-icon>
            </div>
            <div class="flex-1 min-w-0">
              <h3 class="text-white font-semibold text-sm sm:text-base mb-1 transition-colors group-hover:text-[#00d4ff]">{{ activity.title }}</h3>
              <p class="text-gray-400 text-xs sm:text-sm mb-1">{{ activity.desc }}</p>
              <p class="text-gray-500 text-xs">{{ activity.time }}</p>
            </div>
          </div>
        </div>
      </div>
    </AnimatedCard>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { markRaw } from 'vue'
import {
  Camera,
  VideoPlay,
  Headset,
  Trophy,
  Document,
  Location,
  ArrowRight,
} from '@element-plus/icons-vue'
import { getMoviePage } from '@/api/movie'
import { AnimatedCard, AnimatedButton } from '@/components/uiverse'

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
    value: '—',
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
    label: '阅读记录',
    value: '24',
    desc: '本书',
    icon: markRaw(Document),
    iconBg: 'from-orange-500 to-orange-600',
  },
  {
    label: '旅游记录',
    value: '12',
    desc: '个地点',
    icon: markRaw(Location),
    iconBg: 'from-cyan-500 to-cyan-600',
  },
  {
    label: '演唱会记录',
    value: '8',
    desc: '场演出',
    icon: markRaw(Headset),
    iconBg: 'from-indigo-500 to-indigo-600',
  },
  {
    label: '球赛记录',
    value: '18',
    desc: '场比赛',
    icon: markRaw(Trophy),
    iconBg: 'from-green-500 to-green-600',
  },
])

async function loadMovieStats() {
  try {
    // 总记录数
    const all = await getMoviePage({ pageNo: 1, pageSize: 1 })
    const total = (all.data as any)?.page?.total || 0

    // 已看数量
    const watched = await getMoviePage({ pageNo: 1, pageSize: 1, watchStatus: 3 })
    const watchedTotal = (watched.data as any)?.page?.total || 0

    stats.value[1].value = `${total}`
    stats.value[1].desc = `已看 ${watchedTotal} 部`
  } catch {
    // ignore, 保持占位数据
  }
}

function handleViewAll() {
  // 可以导航到时间线页面或其他页面
  console.log('查看全部活动')
}

onMounted(() => {
  loadMovieStats()
})

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
    type: 'book',
    title: '读完了《百年孤独》',
    desc: '评分：9.5',
    time: '1天前',
    icon: markRaw(Document),
    color: 'text-orange-400',
  },
  {
    id: 4,
    type: 'travel',
    title: '去了北京',
    desc: '故宫和天坛',
    time: '2天前',
    icon: markRaw(Location),
    color: 'text-cyan-400',
  },
  {
    id: 5,
    type: 'concert',
    title: '看了周杰伦演唱会',
    desc: '北京鸟巢',
    time: '3天前',
    icon: markRaw(Headset),
    color: 'text-indigo-400',
  },
  {
    id: 6,
    type: 'music',
    title: '添加了《Blinding Lights》',
    desc: 'The Weeknd',
    time: '4天前',
    icon: markRaw(Headset),
    color: 'text-blue-400',
  },
  {
    id: 7,
    type: 'match',
    title: '观看了比赛',
    desc: '巴萨 vs 皇马',
    time: '5天前',
    icon: markRaw(Trophy),
    color: 'text-green-400',
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

/* 数字计数动画 */
.number-counter {
  display: inline-block;
  animation: countUp 0.8s ease-out;
}

@keyframes countUp {
  from {
    opacity: 0;
    transform: translateY(10px) scale(0.8);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 图标容器动画 */
.icon-container {
  position: relative;
}

.icon-container::before {
  content: '';
  position: absolute;
  inset: -4px;
  border-radius: inherit;
  background: radial-gradient(circle, rgba(0, 212, 255, 0.3) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.3s;
}

.icon-container:hover::before {
  opacity: 1;
}

/* 活动项动画 */
.activity-item {
  animation: slideInLeft 0.5s ease-out both;
  position: relative;
  overflow: hidden;
}

.activity-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: linear-gradient(180deg, #00d4ff, #00ffcc);
  transform: scaleY(0);
  transform-origin: bottom;
  transition: transform 0.3s ease;
}

.activity-item:hover::before {
  transform: scaleY(1);
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 活动图标动画 */
.activity-icon {
  position: relative;
}

.activity-icon::after {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: inherit;
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.3), rgba(0, 255, 204, 0.3));
  opacity: 0;
  transition: opacity 0.3s;
  z-index: -1;
}

.activity-item:hover .activity-icon::after {
  opacity: 1;
}
</style>

