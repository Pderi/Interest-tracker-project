<template>
  <div class="timeline-page">
    <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-6 sm:mb-8">时间线</h1>

    <!-- 时间线容器 -->
    <div class="relative">
      <!-- 时间轴线 -->
      <div class="absolute left-8 sm:left-12 top-0 bottom-0 w-0.5 bg-gradient-to-b from-[#00d4ff]/50 via-[#00d4ff]/30 to-transparent shadow-lg shadow-[#00d4ff]/20"></div>

      <!-- 时间线节点 -->
      <div class="space-y-8">
        <div
          v-for="(item, index) in timelineItems"
          :key="item.id"
          class="timeline-item relative pl-16 sm:pl-24"
          :style="{ animationDelay: `${index * 100}ms` }"
        >
          <!-- 节点 -->
          <div class="absolute left-6 sm:left-10 top-2 w-4 h-4 sm:w-5 sm:h-5 rounded-full bg-gradient-to-br from-[#00d4ff] to-[#00ffcc] border-2 border-[#1a1a2e] pulse-animation shadow-lg shadow-[#00d4ff]/50 rotate-glow"></div>

          <!-- 卡片 -->
          <AnimatedCard
            variant="glass"
            class="timeline-card group"
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
                    <p class="text-sm text-[#00d4ff]/60">{{ formatDate(item.date) }}</p>
                  </div>
                </div>

                <!-- 内容 -->
                <div v-if="item.description" class="mb-4">
                  <p class="text-sm text-[#00d4ff]/80 leading-relaxed">{{ item.description }}</p>
                </div>

                <!-- 标签 -->
                <div v-if="item.tags && item.tags.length" class="flex flex-wrap gap-2 timeline-tags">
                  <AnimatedTag
                    v-for="(tag, tagIndex) in item.tags"
                    :key="tag"
                    variant="glow"
                    :animated="tagIndex % 2 === 0"
                    class="timeline-tag-item"
                  >
                    {{ tag }}
                  </AnimatedTag>
                </div>

                <!-- 图片预览 -->
                <div v-if="item.image" class="mt-4 rounded-xl overflow-hidden">
                  <img 
                    :src="item.image" 
                    :alt="item.title"
                    class="w-full h-48 object-cover transition-transform duration-500 group-hover:scale-105"
                  />
                </div>
              </div>
            </div>
          </AnimatedCard>
        </div>
      </div>
    </div>

    <!-- 加载更多 -->
    <div class="text-center mt-8">
      <AnimatedButton
        variant="primary"
        :loading="loading"
        @click="loadMore"
      >
        {{ loading ? '加载中...' : '加载更多' }}
      </AnimatedButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Camera, VideoPlay, Headset, Trophy, Document, Location, Clock } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { 
  AnimatedButton, 
  AnimatedCard,
  AnimatedTag
} from '@/components/uiverse'

// 假数据
const timelineItems = ref([
  {
    id: 1,
    type: 'photo',
    title: '日落时分',
    description: '在海岸边拍摄的美丽日落，天空被染成了橙红色。',
    date: '2025-01-15',
    tags: ['风景', '日落', '海岸'],
    image: 'https://picsum.photos/800/400?random=1',
  },
  {
    id: 2,
    type: 'book',
    title: '读完了《百年孤独》',
    description: '一部震撼人心的魔幻现实主义巨作，马尔克斯的想象力令人叹为观止。',
    date: '2025-01-14',
    tags: ['魔幻现实主义', '文学经典'],
    rating: 9.5,
  },
  {
    id: 3,
    type: 'travel',
    title: '去了北京',
    description: '故宫和天坛都太震撼了，感受到了深厚的历史底蕴。',
    date: '2025-01-10',
    tags: ['国内', '历史文化', '城市'],
    image: 'https://picsum.photos/800/400?random=2',
  },
  {
    id: 4,
    type: 'concert',
    title: '周杰伦演唱会',
    description: '现场氛围太棒了，经典歌曲一首接一首，全场大合唱！',
    date: '2024-12-25',
    tags: ['流行', '华语'],
    rating: 9.5,
  },
  {
    id: 5,
    type: 'movie',
    title: '星际穿越',
    description: '重新观看了这部经典的科幻电影，依然震撼。',
    date: '2024-12-19',
    tags: ['科幻', '剧情'],
    rating: 9.5,
  },
  {
    id: 6,
    type: 'music',
    title: 'Bohemian Rhapsody',
    artist: 'Queen',
    description: '经典中的经典，每次听都有新的感受。',
    date: '2024-12-18',
    tags: ['摇滚', '经典'],
  },
  {
    id: 7,
    type: 'book',
    title: '读完了《1984》',
    description: '对极权主义的深刻警示，至今仍具有强烈的现实意义。',
    date: '2024-12-15',
    tags: ['反乌托邦', '政治'],
    rating: 9.8,
  },
  {
    id: 8,
    type: 'travel',
    title: '去了杭州西湖',
    description: '西湖的美景让人流连忘返，特别是断桥残雪。',
    date: '2024-12-10',
    tags: ['国内', '自然风光', '城市'],
    image: 'https://picsum.photos/800/400?random=3',
  },
  {
    id: 9,
    type: 'match',
    title: '曼城 vs 利物浦',
    description: '精彩的英超对决，最终3-2结束。',
    date: '2024-12-05',
    tags: ['足球', '英超'],
  },
  {
    id: 10,
    type: 'photo',
    title: '城市夜景',
    description: '从山顶俯瞰整个城市的夜景，灯火通明。',
    date: '2024-12-01',
    tags: ['城市', '夜景'],
    image: 'https://picsum.photos/800/400?random=4',
  },
])

const loading = ref(false)

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

function formatDate(date: string) {
  return dayjs(date).format('YYYY年MM月DD日')
}

function loadMore() {
  loading.value = true
  setTimeout(() => {
    // 模拟加载更多数据
    loading.value = false
  }, 1000)
}
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
