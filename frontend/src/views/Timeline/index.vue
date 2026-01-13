<template>
  <div class="timeline-page">
    <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-6 sm:mb-8">时间线</h1>

    <!-- 时间线容器 -->
    <div class="relative">
      <!-- 时间轴线 -->
      <div class="absolute left-8 sm:left-12 top-0 bottom-0 w-0.5 bg-gradient-to-b from-[#00d4ff]/50 via-[#00d4ff]/30 to-transparent shadow-lg shadow-[#00d4ff]/20"></div>

      <!-- 时间线节点 -->
      <div v-if="timelineItems.length === 0 && !loading" class="text-center py-12 text-gray-400">
        暂无时间线数据
      </div>
      <div v-else class="space-y-8">
        <div
          v-for="(item, index) in timelineItems"
          :key="`${item.type}-${item.id}`"
          class="timeline-item relative pl-16 sm:pl-24"
          :style="{ animationDelay: `${index * 100}ms` }"
        >
          <!-- 节点 -->
          <div class="absolute left-6 sm:left-10 top-2 w-4 h-4 sm:w-5 sm:h-5 rounded-full bg-gradient-to-br from-[#00d4ff] to-[#00ffcc] border-2 border-[#1a1a2e] pulse-animation shadow-lg shadow-[#00d4ff]/50 rotate-glow"></div>

          <!-- 卡片 -->
          <AnimatedCard
            variant="glass"
            class="timeline-card group cursor-pointer"
            @click="handleItemClick(item)"
          >
            <div class="rounded-2xl overflow-hidden">
              <div class="p-4 sm:p-6">
                <!-- 头部 -->
                <div class="flex items-start justify-between mb-4">
                  <div class="flex-1">
                    <div class="flex items-center space-x-3 mb-2">
                      <el-icon 
                        :size="24" 
                        class="text-[#00d4ff]"
                        :class="getTypeIcon(item.type).class"
                      >
                        <component :is="getTypeIcon(item.type).icon" />
                      </el-icon>
                      <span class="text-xs sm:text-sm text-[#00d4ff]/80 font-medium flicker">{{ getTypeLabel(item.type) }}</span>
                    </div>
                    <h3 class="text-lg sm:text-xl font-semibold text-white mb-1">{{ item.title }}</h3>
                    <p class="text-sm text-[#00d4ff]/60">{{ formatDate(item.activityTime) }}</p>
                  </div>
                </div>

                <!-- 内容 -->
                <div v-if="item.description" class="mb-4">
                  <p class="text-sm text-[#00d4ff]/80 leading-relaxed">{{ item.description }}</p>
                </div>

                <!-- 标签 -->
                <div v-if="parseTags(item.tags).length > 0" class="flex flex-wrap gap-2 timeline-tags">
                  <el-tag
                    v-for="tag in parseTags(item.tags)"
                    :key="tag"
                    type="info"
                    size="small"
                    class="timeline-tag-item"
                  >
                    {{ tag }}
                  </el-tag>
                </div>

                <!-- 图片预览 -->
                <div v-if="item.cover" class="mt-4 rounded-xl overflow-hidden">
                  <img 
                    :src="item.cover" 
                    :alt="item.title"
                    class="w-full h-48 object-cover transition-transform duration-500 group-hover:scale-105"
                    @error="(e: any) => { e.target.style.display = 'none' }"
                  />
                </div>
              </div>
            </div>
          </AnimatedCard>
        </div>
      </div>
    </div>

    <!-- 加载更多 -->
    <div v-if="hasMore || loading" class="text-center mt-8">
      <AnimatedButton
        variant="primary"
        :loading="loading"
        @click="loadMore"
      >
        {{ loading ? '加载中...' : '加载更多' }}
      </AnimatedButton>
    </div>
    <div v-else-if="timelineItems.length > 0" class="text-center mt-8 text-gray-400 text-sm">
      没有更多数据了
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Camera, VideoPlay, Headset, Trophy, Document, Location, Clock } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { 
  AnimatedButton, 
  AnimatedCard,
} from '@/components/uiverse'
import { getTimeline } from '@/api/dashboard'
import { ElMessage } from 'element-plus'
import type { TimelineItem } from '@/types/api'

const router = useRouter()

const timelineItems = ref<TimelineItem[]>([])
const loading = ref(false)
const pageNo = ref(1)
const pageSize = ref(20)
const hasMore = ref(true)

// 筛选条件
const selectedTypes = ref<string[]>([])
const startTime = ref<string>('')
const endTime = ref<string>('')
const tagFilter = ref<string>('')
const keyword = ref<string>('')

function getTypeIcon(type: string) {
  const map: Record<string, { icon: any; class: string }> = {
    photo: { icon: Camera, class: 'text-[#00d4ff]' },
    movie: { icon: VideoPlay, class: 'text-[#00d4ff]' },
    music: { icon: Headset, class: 'text-[#00d4ff]' },
    book: { icon: Document, class: 'text-[#00d4ff]' },
    travel: { icon: Location, class: 'text-[#00d4ff]' },
    concert: { icon: Headset, class: 'text-[#00d4ff]' },
    match: { icon: Trophy, class: 'text-[#00d4ff]' },
  }
  return map[type] || { icon: Clock, class: 'text-[#00d4ff]' }
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

function formatDate(dateStr: string) {
  return dayjs(dateStr).format('YYYY年MM月DD日')
}

function parseTags(tags?: string): string[] {
  if (!tags) return []
  return tags.split(',').filter(t => t.trim())
}

async function loadTimeline(reset = false) {
  if (loading.value) return

  try {
    loading.value = true

    if (reset) {
      pageNo.value = 1
      timelineItems.value = []
    }

    const params: any = {
      pageNo: pageNo.value,
      pageSize: pageSize.value,
    }

    if (selectedTypes.value.length > 0) {
      params.types = selectedTypes.value.join(',')
    }
    if (startTime.value) {
      params.startTime = startTime.value
    }
    if (endTime.value) {
      params.endTime = endTime.value
    }
    if (tagFilter.value) {
      params.tag = tagFilter.value
    }
    if (keyword.value) {
      params.keyword = keyword.value
    }

    const res = await getTimeline(params)
    if (res.code === 0) {
      const data = res.data
      if (reset) {
        timelineItems.value = data.list || []
      } else {
        timelineItems.value.push(...(data.list || []))
      }
      hasMore.value = timelineItems.value.length < (data.total || 0)
    }
  } catch (error: any) {
    console.error('加载时间线失败:', error)
    ElMessage.error('加载时间线失败')
  } finally {
    loading.value = false
  }
}

function loadMore() {
  if (hasMore.value && !loading.value) {
    pageNo.value++
    loadTimeline(false)
  }
}

function handleItemClick(item: TimelineItem) {
  const routes: Record<string, string> = {
    photo: '/photo',
    movie: '/movie',
    music: '/music',
    book: '/book',
    travel: '/travel',
    concert: '/concert',
    match: '/match',
  }

  const baseRoute = routes[item.type]
  if (baseRoute) {
    if (item.detailId) {
      router.push(`${baseRoute}/${item.detailId}`)
    } else if (item.recordId) {
      router.push(baseRoute)
    }
  }
}

onMounted(() => {
  loadTimeline(true)
})
</script>

<style scoped>
.timeline-page {
  min-height: 100%;
  animation: pageFadeIn 0.4s ease-out;
}

@keyframes pageFadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.timeline-item {
  animation: slideInLeft 0.6s ease-out both;
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 时间线卡片动画 */
.timeline-card {
  transition: all 0.3s ease;
}

.timeline-card:hover {
  transform: translateX(8px);
  box-shadow: 0 8px 24px rgba(0, 212, 255, 0.2);
}

/* 标签动画 */
.timeline-tags {
  animation: tagsFadeIn 0.4s ease-out 0.3s both;
}

@keyframes tagsFadeIn {
  from {
    opacity: 0;
    transform: translateY(5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.timeline-tag-item {
  animation: tagPop 0.3s ease-out both;
}

.timeline-tag-item:nth-child(1) { animation-delay: 0.1s; }
.timeline-tag-item:nth-child(2) { animation-delay: 0.2s; }
.timeline-tag-item:nth-child(3) { animation-delay: 0.3s; }
.timeline-tag-item:nth-child(4) { animation-delay: 0.4s; }

@keyframes tagPop {
  from {
    opacity: 0;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
