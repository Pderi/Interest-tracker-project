<template>
  <div class="movie-page">
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 sm:mb-8">
      <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-4 sm:mb-0 float-animation">影视</h1>
      <el-button 
        type="primary" 
        :icon="Plus" 
        @click="handleAdd"
        class="!bg-[#00d4ff] !border-[#00d4ff] hover:!bg-[#00ffcc] hover:!border-[#00ffcc] !text-[#1a1a2e] shadow-lg shadow-[#00d4ff]/30 hover:shadow-[#00d4ff]/50 transition-all glow-effect font-semibold"
      >
        添加记录
      </el-button>
    </div>

    <!-- 筛选栏 -->
    <div class="mb-6 flex flex-wrap gap-3">
      <el-button 
        v-for="status in statusOptions" 
        :key="status.value"
        :type="filterStatus === status.value ? 'primary' : 'default'"
        size="small"
        @click="filterStatus = status.value"
        :class="filterStatus === status.value ? '!bg-[#ff6b6b] !border-[#ff6b6b]' : ''"
      >
        {{ status.label }}
      </el-button>
    </div>

    <!-- 影视卡片网格 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6">
      <div
        v-for="movie in filteredMovies"
        :key="movie.id"
        class="card-3d group"
      >
        <div class="card-3d-inner rounded-2xl overflow-hidden glass-effect border border-white/10 hover:border-[#00d4ff]/30 transition-all duration-500 glow-effect">
          <!-- 封面 -->
          <div class="relative aspect-[2/3] overflow-hidden bg-gradient-to-br from-[#c3cfe2]/20 to-[#f5f7fa]/10">
            <img 
              v-if="movie.poster && !movie.imageError" 
              :src="movie.poster" 
              :alt="movie.title"
              class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
              loading="lazy"
              @error="handleImageError(movie.id)"
              @load="handleImageLoad(movie.id)"
            />
            <div v-else class="w-full h-full flex items-center justify-center">
              <el-icon :size="48" class="text-[#c3cfe2]/40">
                <VideoPlay />
              </el-icon>
            </div>
            
            <!-- 状态标签 -->
            <div class="absolute top-3 right-3">
              <el-tag 
                :type="getStatusType(movie.status)"
                size="small"
                effect="dark"
                class="backdrop-blur-md"
              >
                {{ getStatusLabel(movie.status) }}
              </el-tag>
            </div>

            <!-- 评分 -->
            <div v-if="movie.rating" class="absolute bottom-3 left-3 flex items-center space-x-1 bg-black/60 backdrop-blur-sm px-2 py-1 rounded-lg">
              <el-icon class="text-yellow-400"><StarFilled /></el-icon>
              <span class="text-white text-sm font-semibold">{{ movie.rating }}</span>
            </div>
          </div>

          <!-- 信息 -->
          <div class="p-4">
            <h3 class="text-lg font-semibold text-white mb-1 line-clamp-1">{{ movie.title }}</h3>
            <p class="text-sm text-[#00d4ff]/70 mb-3">{{ movie.year }} · {{ movie.type }}</p>
            
            <!-- 标签 -->
            <div class="flex flex-wrap gap-2">
              <el-tag
                v-for="tag in movie.tags"
                :key="tag"
                size="small"
                effect="plain"
                class="!border-[#00d4ff]/30 !text-[#00d4ff] flicker"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Plus, VideoPlay, StarFilled } from '@element-plus/icons-vue'

// 假数据
const movies = ref([
  {
    id: 1,
    title: '星际穿越',
    year: '2014',
    type: '电影',
    status: 'watched',
    rating: 9.5,
    poster: 'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg',
    tags: ['科幻', '冒险', '剧情'],
    imageError: false,
  },
  {
    id: 2,
    title: '权力的游戏',
    year: '2011-2019',
    type: '剧集',
    status: 'watching',
    rating: 9.3,
    poster: 'https://image.tmdb.org/t/p/w500/u3bZgnGQ9T01sWNhyveQz0wH0Hl.jpg',
    tags: ['奇幻', '剧情', '史诗'],
    imageError: false,
  },
  {
    id: 3,
    title: '盗梦空间',
    year: '2010',
    type: '电影',
    status: 'watched',
    rating: 9.2,
    // TMDB ID: 27205, 使用备用图片源
    poster: 'https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_SX300.jpg',
    tags: ['科幻', '悬疑', '动作'],
    imageError: false,
  },
  {
    id: 4,
    title: '黑镜',
    year: '2011-2019',
    type: '剧集',
    status: 'want',
    rating: null,
    // 使用可靠的图片源
    poster: 'https://images.unsplash.com/photo-1519681393784-d120267933ba?w=500&h=750&fit=crop&q=80',
    tags: ['科幻', '惊悚', '剧情'],
    imageError: false,
  },
  {
    id: 5,
    title: '肖申克的救赎',
    year: '1994',
    type: '电影',
    status: 'watched',
    rating: 9.7,
    poster: 'https://image.tmdb.org/t/p/w500/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg',
    tags: ['剧情', '犯罪'],
    imageError: false,
  },
  {
    id: 6,
    title: '怪奇物语',
    year: '2016-2022',
    type: '剧集',
    status: 'watching',
    rating: 8.8,
    poster: 'https://image.tmdb.org/t/p/w500/49WJfeN0moxb9IPfGn8AIqMGskD.jpg',
    tags: ['科幻', '恐怖', '悬疑'],
    imageError: false,
  },
])

const statusOptions = [
  { label: '全部', value: 'all' },
  { label: '想看', value: 'want' },
  { label: '在看', value: 'watching' },
  { label: '已看', value: 'watched' },
  { label: '弃剧', value: 'dropped' },
]

const filterStatus = ref('all')

const filteredMovies = computed(() => {
  if (filterStatus.value === 'all') {
    return movies.value
  }
  return movies.value.filter(m => m.status === filterStatus.value)
})

function getStatusType(status: string) {
  const map: Record<string, string> = {
    want: 'info',
    watching: 'warning',
    watched: 'success',
    dropped: 'danger',
  }
  return map[status] || 'info'
}

function getStatusLabel(status: string) {
  const map: Record<string, string> = {
    want: '想看',
    watching: '在看',
    watched: '已看',
    dropped: '弃剧',
  }
  return map[status] || status
}

function handleAdd() {
  console.log('添加影视记录')
}

function handleImageError(movieId: number) {
  const movie = movies.value.find(m => m.id === movieId)
  if (movie) {
    movie.imageError = true
  }
}

function handleImageLoad(movieId: number) {
  const movie = movies.value.find(m => m.id === movieId)
  if (movie) {
    movie.imageError = false
  }
}
</script>

<style scoped>
.movie-page {
  min-height: 100%;
}

.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
