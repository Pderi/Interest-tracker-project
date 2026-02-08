<template>
  <div class="dashboard min-h-full">
    <!-- Hero Section -->
    <div class="mb-8 sm:mb-12 float-animation">
      <h1 class="text-4xl sm:text-5xl lg:text-6xl font-bold mb-4 neon-text leading-tight">
        仪表板
      </h1>
      <p class="text-gray-400 text-sm sm:text-base">欢迎回来，查看你的兴趣记录概览</p>
    </div>

    <!-- Time Range Selector -->
    <div class="mb-6">
      <TimeRangeSelector v-model="timeRange" @change="handleTimeRangeChange" />
    </div>
    
    <!-- Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6 mb-6 sm:mb-8">
      <AnimatedCard
        v-for="(stat, index) in stats"
        :key="index"
        variant="glass"
        class="stat-card cursor-pointer group"
        :style="{ animationDelay: `${index * 100}ms` }"
        @click="handleStatClick(stat.route)"
      >
        <div class="p-6">
          <div class="flex items-center justify-between">
            <div class="flex-1">
              <div class="flex items-center justify-between mb-2">
                <p class="text-gray-400 text-xs sm:text-sm font-medium">{{ stat.label }}</p>
                <div v-if="stat.trend !== undefined" class="flex items-center space-x-1.5">
                  <el-icon 
                    :size="14" 
                    :class="stat.trend > 0 ? 'text-green-400' : stat.trend < 0 ? 'text-red-400' : 'text-gray-400'"
                  >
                    <component :is="stat.trend > 0 ? ArrowUp : stat.trend < 0 ? ArrowDown : Minus" />
                  </el-icon>
                  <span 
                    :class="stat.trend > 0 ? 'text-green-400' : stat.trend < 0 ? 'text-red-400' : 'text-gray-400'"
                    class="text-xs font-medium"
                  >
                    <span class="text-gray-400">较上周</span>
                    <span class="ml-0.5">{{ stat.trend > 0 ? '+' : '' }}{{ stat.trend }}%</span>
                  </span>
                </div>
              </div>
              <p class="text-3xl sm:text-4xl font-bold gradient-text mb-1">
                <span class="number-counter">{{ stat.value }}</span>
              </p>
              <div class="flex items-center justify-between">
                <div class="flex flex-col">
                  <p class="text-gray-500 text-xs">{{ stat.desc }}</p>
                  <p v-if="stat.lastWeekCount !== undefined && stat.lastWeekCount > 0" class="text-gray-500/70 text-xs mt-0.5">
                    上周新增 {{ stat.lastWeekCount }}{{ getUnit(stat.label) }}
                  </p>
                </div>
                <p v-if="stat.todayCount !== undefined" class="text-[#00d4ff] text-xs font-medium">
                  今日 +{{ stat.todayCount }}
                </p>
              </div>
              <!-- 迷你趋势图 -->
              <div v-if="stat.trendData && stat.trendData.length > 0 && trendDates.length > 0" class="mt-3 -mx-2 h-10 overflow-hidden">
                <MiniTrendChart
                  :key="`${stat.label}-${stat.trendData.length}`"
                  :data="stat.trendData"
                  :dates="trendDates"
                  :color="getTrendColor(stat.label)"
                  height="40px"
                />
              </div>
            </div>
            <div class="ml-4 w-12 h-12 sm:w-14 sm:h-14 rounded-xl bg-gradient-to-br from-[#00d4ff]/20 to-[#00ffcc]/20 border border-[#00d4ff]/30 flex items-center justify-center transition-all duration-300 group-hover:scale-110 group-hover:rotate-6 group-hover:border-[#00d4ff]/50 group-hover:bg-gradient-to-br group-hover:from-[#00d4ff]/30 group-hover:to-[#00ffcc]/30 icon-container">
              <el-icon :size="24" class="text-[#00d4ff]">
                <component :is="stat.icon" />
              </el-icon>
            </div>
          </div>
        </div>
      </AnimatedCard>
    </div>

    <!-- Insights Cards -->
    <div v-if="insights" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6 mb-6 sm:mb-8">
      <!-- 最活跃类型 -->
      <InsightsCard
        v-if="insights.mostActiveType && insights.mostActiveType.count !== undefined && insights.mostActiveType.count !== null"
        title="最活跃类型"
        :icon="getTypeIcon(insights.mostActiveType.type)"
        :value="Number(insights.mostActiveType.count)"
        :unit="getTypeUnit(insights.mostActiveType.type)"
        :description="`${insights.mostActiveType.typeName}占比 ${Number(insights.mostActiveType.percentage || 0).toFixed(1)}%`"
        :loading="insightsLoading"
      />

      <!-- 评分趋势 -->
      <InsightsCard
        v-if="insights.ratingTrend && insights.ratingTrend.currentAvgRating !== undefined && insights.ratingTrend.currentAvgRating !== null"
        title="平均评分"
        :icon="Star"
        :value="Number(insights.ratingTrend.currentAvgRating).toFixed(1)"
        :unit="'分'"
        :description="`较上期 ${insights.ratingTrend.changePercentage > 0 ? '上升' : insights.ratingTrend.changePercentage < 0 ? '下降' : '持平'}`"
        :trend="Number(insights.ratingTrend.changePercentage || 0)"
        :loading="insightsLoading"
      />

      <!-- 活跃度峰值 -->
      <InsightsCard
        v-if="insights.peakActivity && insights.peakActivity.count !== undefined && insights.peakActivity.count !== null && insights.peakActivity.count > 0"
        title="活跃度峰值"
        :icon="Calendar"
        :value="Number(insights.peakActivity.count)"
        :unit="'条'"
        :description="`${formatDate(insights.peakActivity.date)} · ${insights.peakActivity.typeName}`"
        :loading="insightsLoading"
      />

      <!-- 完成率 -->
      <InsightsCard
        v-if="insights.completionRate && insights.completionRate.percentage !== undefined && insights.completionRate.percentage !== null"
        title="完成率"
        :icon="Trophy"
        :value="Number(insights.completionRate.percentage).toFixed(1)"
        :unit="'%'"
        :description="`${insights.completionRate.completed}/${insights.completionRate.total} ${insights.completionRate.typeName}已完成`"
        :loading="insightsLoading"
      />
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
      <div v-if="formattedActivities.length === 0 && !loading" class="p-8 text-center text-gray-400">
        <el-icon :size="48" class="mb-2 opacity-50"><Clock /></el-icon>
        <p>暂无最近活动</p>
      </div>
      <div v-else-if="loading" class="p-8 text-center text-gray-400">
        <el-icon class="is-loading" :size="24"><Loading /></el-icon>
        <p class="mt-2">加载中...</p>
      </div>
      <div v-else class="divide-y divide-white/10">
        <div
          v-for="(activity, index) in formattedActivities"
          :key="`${activity.type}-${activity.id}`"
          class="activity-item p-4 sm:p-6 hover:bg-white/5 transition-all duration-300 cursor-pointer group"
          :style="{ animationDelay: `${index * 50}ms` }"
          @click="handleActivityClick(activity)"
        >
          <div class="flex items-start gap-4 sm:gap-6">
            <!-- 左侧：封面图片或图标 -->
            <div class="flex-shrink-0">
              <div 
                class="w-24 h-24 sm:w-32 sm:h-32 rounded-xl overflow-hidden border border-white/10 shadow-lg group-hover:shadow-[#00d4ff]/20 transition-all duration-300 group-hover:scale-105 relative bg-gradient-to-br from-[#00d4ff]/20 to-[#00ffcc]/20"
              >
                <img 
                  v-if="activity.cover && !imageErrorMap[activity.id]"
                  :src="activity.cover" 
                  :alt="activity.title"
                  :data-activity-id="activity.id"
                  class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110"
                  @error="handleImageError"
                />
                <div 
                  v-else
                  class="w-full h-full flex items-center justify-center"
                >
                  <el-icon :size="40" :class="activity.color">
                    <component :is="activity.icon" />
                  </el-icon>
                </div>
                
                <!-- 评分徽章（如果有） -->
                <div
                  v-if="activity.rating"
                  class="absolute top-2 right-2 flex items-center space-x-1 bg-black/70 backdrop-blur-sm px-2 py-1 rounded-lg border border-white/20"
                >
                  <el-icon :size="12" class="text-yellow-400"><Star /></el-icon>
                  <span class="text-white text-xs font-semibold">{{ activity.rating }}</span>
                </div>
              </div>
            </div>
            
            <!-- 中间：主要内容信息 -->
            <div class="flex-1 min-w-0 flex flex-col">
              <!-- 上半部分：主要信息 -->
              <div class="flex-1 flex flex-col">
                <!-- 类型标签和状态 -->
                <div class="flex items-center space-x-2 flex-wrap mb-2">
                  <span class="text-xs px-2 py-0.5 rounded-full font-medium" :class="getTypeBadgeClass(activity.type)">
                    {{ getTypeLabel(activity.type) }}
                  </span>
                  <span v-if="activity.status" class="text-xs px-2 py-0.5 rounded-full font-medium" :class="getStatusBadgeClass(activity.type, activity.status)">
                    {{ getStatusLabel(activity.type, activity.status) }}
                  </span>
                  <!-- 评分显示（如果没有显示在图片上） -->
                  <div v-if="activity.rating && (!activity.cover || imageErrorMap[activity.id])" class="flex items-center space-x-1 text-[#00d4ff] bg-[#00d4ff]/10 px-2 py-0.5 rounded-full border border-[#00d4ff]/30">
                    <el-icon :size="12"><Star /></el-icon>
                    <span class="text-xs font-semibold">{{ activity.rating }}</span>
                  </div>
                </div>
                
                <!-- 标题和副标题 -->
                <div class="mb-2">
                  <h3 class="text-white font-semibold text-base sm:text-lg mb-1 transition-colors group-hover:text-[#00d4ff] line-clamp-2">
                    {{ activity.title }}
                  </h3>
                  <!-- 对于演唱会，如果副标题是艺术家且标题不包含艺术家，则显示；否则不显示副标题 -->
                  <p v-if="activity.subtitle && !(activity.type === 'concert' && activity.title && activity.title.includes(activity.subtitle))" class="text-gray-400 text-sm line-clamp-1">
                    {{ activity.subtitle }}
                  </p>
                </div>
                
                <!-- 标签 -->
                <div v-if="activity.tags && parseTags(activity.tags).length > 0" class="flex flex-wrap gap-1.5 mb-2">
                  <span
                    v-for="tag in parseTags(activity.tags).slice(0, 6)"
                    :key="tag"
                    class="text-xs px-2 py-0.5 rounded bg-white/5 text-gray-300 border border-white/10"
                  >
                    {{ tag }}
                  </span>
                  <span v-if="parseTags(activity.tags).length > 6" class="text-xs px-2 py-0.5 rounded bg-white/5 text-gray-400">
                    +{{ parseTags(activity.tags).length - 6 }}
                  </span>
                </div>
                
                <!-- 左下角：评论描述 + 时间信息 -->
                <div class="mt-auto pt-2 border-t border-white/5">
                  <!-- 评论描述（"感觉一般"等评论内容） -->
                  <p v-if="activity.desc && activity.desc.trim()" class="text-gray-300 text-sm line-clamp-2 mb-1.5 italic">
                    {{ activity.desc }}
                  </p>
                  <!-- 时间信息 -->
                  <div class="flex items-center">
                    <p class="text-gray-500 text-xs flex items-center">
                      <el-icon :size="12" class="mr-1"><Clock /></el-icon>
                      {{ activity.time }}
                    </p>
                    <!-- 小屏幕显示操作按钮 -->
                    <button
                      @click.stop="handleActivityClick(activity)"
                      class="lg:hidden ml-auto w-8 h-8 rounded-lg bg-[#00d4ff]/10 hover:bg-[#00d4ff]/20 border border-[#00d4ff]/30 flex items-center justify-center transition-all duration-300 hover:scale-110 group-hover:border-[#00d4ff]/50"
                    >
                      <el-icon :size="14" class="text-[#00d4ff]">
                        <ArrowRight />
                      </el-icon>
                    </button>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 右侧：额外信息卡片（仅在大屏幕显示，且避免与中间信息重复） -->
            <div class="hidden lg:flex flex-col items-end justify-between w-28 flex-shrink-0">
              <!-- 评分卡片（如果有且显示在图片上，则在右侧显示详细信息） -->
              <div v-if="activity.rating && activity.cover && !imageErrorMap[activity.id]" class="w-full px-3 py-2 rounded-lg bg-[#00d4ff]/10 border border-[#00d4ff]/30 mb-2">
                <p class="text-xs text-gray-400 mb-1 text-center">评分</p>
                <div class="flex items-center justify-center space-x-1">
                  <el-icon :size="14" class="text-yellow-400"><Star /></el-icon>
                  <span class="text-sm font-bold text-[#00d4ff]">{{ activity.rating }}</span>
                </div>
              </div>
              
              <!-- 状态信息卡片（仅当中间没有显示状态时显示，或者状态信息很重要时显示） -->
              <div v-if="activity.status && activity.type !== 'concert'" class="w-full px-3 py-2 rounded-lg bg-white/5 border border-white/10 mb-2">
                <p class="text-xs text-gray-400 mb-1 text-center">状态</p>
                <p class="text-sm font-semibold text-center" :class="getStatusTextClass(activity.type, activity.status)">
                  {{ getStatusLabel(activity.type, activity.status) }}
                </p>
              </div>
              
              <!-- 操作按钮（移到右侧顶部） -->
              <button
                @click.stop="handleActivityClick(activity)"
                class="w-10 h-10 rounded-lg bg-[#00d4ff]/10 hover:bg-[#00d4ff]/20 border border-[#00d4ff]/30 flex items-center justify-center transition-all duration-300 hover:scale-110 group-hover:border-[#00d4ff]/50 mb-2"
              >
                <el-icon :size="16" class="text-[#00d4ff]">
                  <ArrowRight />
                </el-icon>
              </button>
              
              <!-- 类型图标装饰 -->
              <div class="w-full aspect-square rounded-xl bg-gradient-to-br from-[#00d4ff]/10 to-[#00ffcc]/10 border border-[#00d4ff]/20 flex items-center justify-center opacity-50">
                <el-icon :size="28" :class="activity.color">
                  <component :is="activity.icon" />
                </el-icon>
              </div>
            </div>
          </div>
        </div>
      </div>
    </AnimatedCard>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { markRaw } from 'vue'
import { useRouter } from 'vue-router'
import {
  Camera,
  VideoPlay,
  Headset,
  Trophy,
  Document,
  Location,
  ArrowRight,
  ArrowUp,
  ArrowDown,
  Minus,
  Clock,
  Loading,
  Star,
  Calendar,
} from '@element-plus/icons-vue'
import { getDashboardSummary, getRecentActivities, getDashboardInsights } from '@/api/dashboard'
import { AnimatedCard, AnimatedButton } from '@/components/uiverse'
import MiniTrendChart from '@/components/charts/MiniTrendChart.vue'
import TimeRangeSelector from '@/components/dashboard/TimeRangeSelector.vue'
import InsightsCard from '@/components/dashboard/InsightsCard.vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const router = useRouter()

const loading = ref(false)
const insightsLoading = ref(false)
const timeRange = ref<'today' | 'week' | 'month' | 'year'>('week')
const summary = ref<any>(null)
const insights = ref<any>(null)
const recentActivities = ref<any[]>([])
const imageErrorMap = ref<Record<number, boolean>>({})

function handleImageError(e: Event) {
  const img = e.target as HTMLImageElement
  const activityId = parseInt(img.getAttribute('data-activity-id') || '0')
  if (activityId) {
    imageErrorMap.value[activityId] = true
  }
}

// 统计卡片配置
const stats = computed(() => {
  if (!summary.value) {
    return [
      {
        label: '照片总数',
        value: '—',
        desc: '加载中...',
        icon: markRaw(Camera),
        iconBg: 'from-purple-500 to-purple-600',
      },
      {
        label: '影视记录',
        value: '—',
        desc: '加载中...',
        icon: markRaw(VideoPlay),
        iconBg: 'from-pink-500 to-pink-600',
      },
      {
        label: '音乐记录',
        value: '—',
        desc: '加载中...',
        icon: markRaw(Headset),
        iconBg: 'from-blue-500 to-blue-600',
      },
      {
        label: '阅读记录',
        value: '—',
        desc: '加载中...',
        icon: markRaw(Document),
        iconBg: 'from-orange-500 to-orange-600',
      },
      {
        label: '旅游记录',
        value: '—',
        desc: '加载中...',
        icon: markRaw(Location),
        iconBg: 'from-cyan-500 to-cyan-600',
      },
      {
        label: '演唱会记录',
        value: '—',
        desc: '加载中...',
        icon: markRaw(Headset),
        iconBg: 'from-indigo-500 to-indigo-600',
      },
      {
        label: '球赛记录',
        value: '—',
        desc: '加载中...',
        icon: markRaw(Trophy),
        iconBg: 'from-green-500 to-green-600',
      },
    ]
  }

  const s = summary.value
  const movieStatusCounts = s.movieStatusCounts || {}
  const watchedCount = movieStatusCounts[3] || 0 // 3-已看

  // 计算趋势（本周新增相对于上周的增长百分比）
  const calculateTrend = (weekly: number, lastWeek: number = 0) => {
    if (lastWeek === 0) {
      // 如果上周为0，本周有新增，显示100%增长
      if (weekly > 0) return 100
      // 如果上周为0，本周也为0，显示0%
      return 0
    }
    // 计算增长率：(本周 - 上周) / 上周 * 100
    const growth = ((weekly - lastWeek) / lastWeek) * 100
    return Math.round(growth)
  }

  const getTimeRangeLabel = () => {
    const map: Record<string, string> = {
      today: '今日',
      week: '本周',
      month: '本月',
      year: '本年',
    }
    return map[timeRange.value] || '本周'
  }

  const getUnit = (label: string) => {
    const unitMap: Record<string, string> = {
      '照片总数': '张',
      '影视记录': '部',
      '音乐记录': '首',
      '阅读记录': '本',
      '旅游记录': '个',
      '演唱会记录': '场',
      '球赛记录': '场',
    }
    return unitMap[label] || ''
  }

  return [
    {
      label: '照片总数',
      value: String(s.photoCount || 0),
      desc: `${getTimeRangeLabel()}新增 ${s.weeklyStats?.photo || 0} ${getUnit('照片总数')}`,
      todayCount: s.todayStats?.photo || 0,
      lastWeekCount: s.lastWeekStats?.photo || 0,
      trend: calculateTrend(s.weeklyStats?.photo || 0, s.lastWeekStats?.photo || 0),
      trendData: (s.trendData?.photo || []).map(v => Number(v) || 0),
      icon: markRaw(Camera),
      iconBg: 'from-purple-500 to-purple-600',
      route: '/photo',
    },
    {
      label: '影视记录',
      value: String(s.movieCount || 0),
      desc: `已看 ${watchedCount} 部`,
      todayCount: s.todayStats?.movie || 0,
      lastWeekCount: s.lastWeekStats?.movie || 0,
      trend: calculateTrend(s.weeklyStats?.movie || 0, s.lastWeekStats?.movie || 0),
      trendData: (s.trendData?.movie || []).map(v => Number(v) || 0),
      icon: markRaw(VideoPlay),
      iconBg: 'from-pink-500 to-pink-600',
      route: '/movie',
    },
    {
      label: '音乐记录',
      value: String(s.musicCount || 0),
      desc: `${getTimeRangeLabel()}新增 ${s.weeklyStats?.music || 0} ${getUnit('音乐记录')}`,
      todayCount: s.todayStats?.music || 0,
      lastWeekCount: s.lastWeekStats?.music || 0,
      trend: calculateTrend(s.weeklyStats?.music || 0, s.lastWeekStats?.music || 0),
      trendData: (s.trendData?.music || []).map(v => Number(v) || 0),
      icon: markRaw(Headset),
      iconBg: 'from-blue-500 to-blue-600',
      route: '/music',
    },
    {
      label: '阅读记录',
      value: String(s.bookCount || 0),
      desc: `${getTimeRangeLabel()}新增 ${s.weeklyStats?.book || 0} ${getUnit('阅读记录')}`,
      todayCount: s.todayStats?.book || 0,
      lastWeekCount: s.lastWeekStats?.book || 0,
      trend: calculateTrend(s.weeklyStats?.book || 0, s.lastWeekStats?.book || 0),
      trendData: (s.trendData?.book || []).map(v => Number(v) || 0),
      icon: markRaw(Document),
      iconBg: 'from-orange-500 to-orange-600',
      route: '/book',
    },
    {
      label: '旅游记录',
      value: String(s.travelCount || 0),
      desc: `${getTimeRangeLabel()}新增 ${s.weeklyStats?.travel || 0} ${getUnit('旅游记录')}`,
      todayCount: s.todayStats?.travel || 0,
      lastWeekCount: s.lastWeekStats?.travel || 0,
      trend: calculateTrend(s.weeklyStats?.travel || 0, s.lastWeekStats?.travel || 0),
      trendData: (s.trendData?.travel || []).map(v => Number(v) || 0),
      icon: markRaw(Location),
      iconBg: 'from-cyan-500 to-cyan-600',
      route: '/travel',
    },
    {
      label: '演唱会记录',
      value: String(s.concertCount || 0),
      desc: `${getTimeRangeLabel()}新增 ${s.weeklyStats?.concert || 0} ${getUnit('演唱会记录')}`,
      todayCount: s.todayStats?.concert || 0,
      lastWeekCount: s.lastWeekStats?.concert || 0,
      trend: calculateTrend(s.weeklyStats?.concert || 0, s.lastWeekStats?.concert || 0),
      trendData: (s.trendData?.concert || []).map(v => Number(v) || 0),
      icon: markRaw(Headset),
      iconBg: 'from-indigo-500 to-indigo-600',
      route: '/concert',
    },
    {
      label: '球赛记录',
      value: String(s.matchCount || 0),
      desc: `${getTimeRangeLabel()}新增 ${s.weeklyStats?.match || 0} ${getUnit('球赛记录')}`,
      todayCount: s.todayStats?.match || 0,
      lastWeekCount: s.lastWeekStats?.match || 0,
      trend: calculateTrend(s.weeklyStats?.match || 0, s.lastWeekStats?.match || 0),
      trendData: (s.trendData?.match || []).map(v => Number(v) || 0),
      icon: markRaw(Trophy),
      iconBg: 'from-green-500 to-green-600',
      route: '/match',
    },
  ]
})

// 图标映射
const typeIconMap: Record<string, any> = {
  photo: Camera,
  movie: VideoPlay,
  music: Headset,
  book: Document,
  travel: Location,
  concert: Headset,
  match: Trophy,
}

// 颜色映射
const typeColorMap: Record<string, string> = {
  photo: 'text-purple-400',
  movie: 'text-pink-400',
  music: 'text-blue-400',
  book: 'text-orange-400',
  travel: 'text-cyan-400',
  concert: 'text-indigo-400',
  match: 'text-green-400',
}

// 格式化活动数据
const formattedActivities = computed(() => {
  return recentActivities.value.map((activity) => {
    const icon = typeIconMap[activity.type] || Camera
    const color = typeColorMap[activity.type] || 'text-gray-400'
    const time = activity.activityTime
      ? dayjs(activity.activityTime).fromNow()
      : '未知时间'

    return {
      ...activity,
      icon: markRaw(icon),
      color,
      time,
      desc: activity.description || '',
      cover: activity.cover || undefined,
      rating: activity.rating,
      status: activity.status,
      subtitle: activity.subtitle,
      tags: activity.tags,
    }
  })
})

async function loadSummary() {
  try {
    loading.value = true
    const res = await getDashboardSummary({
      timeRange: timeRange.value,
    })
    if (res.code === 0) {
      summary.value = res.data
    }
  } catch (error: any) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
}

async function loadInsights() {
  try {
    insightsLoading.value = true
    const res = await getDashboardInsights({
      timeRange: timeRange.value,
    })
    if (res.code === 0) {
      insights.value = res.data
    }
  } catch (error: any) {
    console.error('加载数据洞察失败:', error)
    // 静默失败，不显示错误提示
  } finally {
    insightsLoading.value = false
  }
}

function handleTimeRangeChange(value: 'today' | 'week' | 'month' | 'year') {
  loadSummary()
  loadInsights()
}

async function loadRecentActivities() {
  try {
    const res = await getRecentActivities(10)
    if (res.code === 0) {
      recentActivities.value = res.data || []
    }
  } catch (error: any) {
    console.error('加载最近活动失败:', error)
    // 不显示错误提示，静默失败
  }
}

function handleViewAll() {
  router.push('/timeline')
}

function handleStatClick(route?: string) {
  if (route) {
    router.push(route)
  }
}

// 获取趋势日期列表
const trendDates = computed(() => {
  return summary.value?.trendData?.dates || []
})

function getUnit(label: string) {
  const unitMap: Record<string, string> = {
    '照片总数': '张',
    '影视记录': '部',
    '音乐记录': '首',
    '阅读记录': '本',
    '旅游记录': '个',
    '演唱会记录': '场',
    '球赛记录': '场',
  }
  return unitMap[label] || ''
}

function getTrendColor(label: string) {
  const colorMap: Record<string, string> = {
    '照片总数': '#a855f7', // purple
    '影视记录': '#ec4899', // pink
    '音乐记录': '#3b82f6', // blue
    '阅读记录': '#f97316', // orange
    '旅游记录': '#06b6d4', // cyan
    '演唱会记录': '#6366f1', // indigo
    '球赛记录': '#10b981', // green
  }
  return colorMap[label] || '#00d4ff'
}

function getTypeIcon(type: string) {
  const iconMap: Record<string, any> = {
    photo: Camera,
    movie: VideoPlay,
    music: Headset,
    book: Document,
    travel: Location,
    concert: Headset,
    match: Trophy,
  }
  return iconMap[type] || Camera
}

function getTypeUnit(type: string) {
  const unitMap: Record<string, string> = {
    photo: '张',
    movie: '部',
    music: '首',
    book: '本',
    travel: '个',
    concert: '场',
    match: '场',
  }
  return unitMap[type] || ''
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = dayjs(dateStr)
  const today = dayjs()
  if (date.isSame(today, 'day')) {
    return '今天'
  } else if (date.isSame(today.subtract(1, 'day'), 'day')) {
    return '昨天'
  } else {
    return date.format('MM月DD日')
  }
}

function getTypeLabel(type: string) {
  const map: Record<string, string> = {
    photo: '摄影',
    movie: '影视',
    music: '音乐',
    book: '阅读',
    travel: '旅游',
    concert: '演唱会',
    match: '球赛',
  }
  return map[type] || type
}

function getTypeBadgeClass(type: string) {
  const map: Record<string, string> = {
    photo: 'bg-purple-500/20 text-purple-300 border border-purple-500/30',
    movie: 'bg-pink-500/20 text-pink-300 border border-pink-500/30',
    music: 'bg-blue-500/20 text-blue-300 border border-blue-500/30',
    book: 'bg-orange-500/20 text-orange-300 border border-orange-500/30',
    travel: 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/30',
    concert: 'bg-indigo-500/20 text-indigo-300 border border-indigo-500/30',
    match: 'bg-green-500/20 text-green-300 border border-green-500/30',
  }
  return map[type] || 'bg-[#00d4ff]/20 text-[#00d4ff] border border-[#00d4ff]/30'
}

function getStatusLabel(type: string, status: number) {
  const statusMap: Record<string, Record<number, string>> = {
    movie: { 1: '想看', 2: '在看', 3: '已看', 4: '弃剧' },
    music: { 1: '想听', 2: '在听', 3: '已听', 4: '弃听' },
    book: { 1: '想读', 2: '在读', 3: '已读', 4: '弃读' },
    travel: { 1: '想去', 2: '计划中', 3: '已去' },
    concert: { 1: '想看', 2: '已看' },
  }
  return statusMap[type]?.[status] || ''
}

function getStatusBadgeClass(type: string, status: number) {
  // 已看/已听/已读/已去 用绿色，其他用灰色
  const completedStatus = type === 'movie' ? 3 : type === 'music' ? 3 : type === 'book' ? 3 : type === 'travel' ? 3 : type === 'concert' ? 2 : 0
  if (status === completedStatus) {
    return 'bg-green-500/20 text-green-300 border border-green-500/30'
  }
  return 'bg-gray-500/20 text-gray-300 border border-gray-500/30'
}

function getStatusTextClass(type: string, status: number) {
  const completedStatus = type === 'movie' ? 3 : type === 'music' ? 3 : type === 'book' ? 3 : type === 'travel' ? 3 : type === 'concert' ? 2 : 0
  if (status === completedStatus) {
    return 'text-green-400'
  }
  return 'text-gray-300'
}

function parseTags(tags?: string[]): string[] {
  return (tags || []).map(t => t.trim()).filter(t => t.length > 0)
}

function handleActivityClick(activity: any) {
  const routes: Record<string, string> = {
    photo: '/photo',
    movie: '/movie',
    music: '/music',
    book: '/book',
    travel: '/travel',
    concert: '/concert',
    match: '/match',
  }

  const baseRoute = routes[activity.type]
  if (baseRoute) {
    if (activity.detailId) {
      router.push(`${baseRoute}/${activity.detailId}`)
    } else if (activity.recordId) {
      // 对于记录类型的，可以跳转到列表页
      router.push(baseRoute)
    }
  }
}

onMounted(() => {
  loadSummary()
  loadInsights()
  loadRecentActivities()
})
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

