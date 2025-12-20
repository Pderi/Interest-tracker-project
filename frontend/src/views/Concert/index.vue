<template>
  <div class="concert-page">
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 sm:mb-8">
      <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-4 sm:mb-0 float-animation">演唱会</h1>
      <el-button
        type="primary"
        :icon="Plus"
        @click="openCreateDialog"
        class="!bg-[#00d4ff] !border-[#00d4ff] hover:!bg-[#00ffcc] hover:!border-[#00ffcc] !text-[#1a1a2e] shadow-lg shadow-[#00d4ff]/30 hover:shadow-[#00d4ff]/50 transition-all glow-effect font-semibold"
      >
        添加记录
      </el-button>
    </div>

    <!-- 筛选栏 -->
    <div class="mb-6">
      <div class="flex flex-col gap-3">
        <div class="flex flex-wrap gap-2 w-full">
          <el-button
            v-for="tag in tagOptions"
            :key="tag.value"
            :type="filterTag === tag.value ? 'primary' : 'default'"
            @click="changeTag(tag.value)"
            :class="filterTag === tag.value ? '!bg-[#00d4ff] !border-[#00d4ff]' : ''"
          >
            {{ tag.label }}
          </el-button>
        </div>
        <el-input
          v-model="keyword"
          placeholder="搜索艺人、地点…"
          clearable
          class="w-full"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 演唱会卡片网格 -->
    <div
      v-loading="loading"
      class="min-h-[120px]"
    >
      <div
        v-if="concertList.length === 0 && !loading"
        class="py-10 text-center text-gray-400"
      >
        暂无记录，点击右上角「添加记录」开始你的演唱会之旅。
      </div>

      <div
        v-else
        class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6"
      >
        <template
          v-for="concert in concertList"
          :key="concert.id"
        >
          <div class="flex flex-col gap-2">
            <!-- 演唱会卡片 -->
            <div
              class="card-3d group cursor-pointer"
              @click="goDetail()"
            >
              <div class="card-3d-inner rounded-2xl overflow-hidden border border-white/10 hover:border-[#00d4ff]/30 transition-all duration-500 glow-effect bg-transparent">
                <!-- 图片 -->
                <div class="relative aspect-[4/3] overflow-hidden">
                  <img
                    v-if="concert.imageUrl && !imageErrorMap[concert.id]"
                    :src="concert.imageUrl"
                    :alt="concert.artist"
                    class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
                    loading="lazy"
                    @error="handleImageError(concert.id)"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center bg-gradient-to-br from-[#00d4ff]/20 to-[#00ffcc]/10">
                    <el-icon :size="48" class="text-[#c3cfe2]/40">
                      <Headset />
                    </el-icon>
                  </div>

                  <!-- 评分 -->
                  <div
                    v-if="concert.rating != null"
                    class="absolute bottom-3 left-3 flex items-center space-x-1 bg-black/60 backdrop-blur-sm px-2 py-1 rounded-lg"
                  >
                    <el-icon class="text-yellow-400"><StarFilled /></el-icon>
                    <span class="text-white text-sm font-semibold">{{ concert.rating }}</span>
                  </div>
                </div>

                <!-- 信息 -->
                <div class="p-4 flex flex-col gap-2 glass-effect">
                  <h3 class="text-lg font-semibold text-white line-clamp-1">{{ concert.artist }}</h3>
                  <p class="text-sm text-[#00d4ff]/80 line-clamp-1">{{ concert.venue }}</p>
                  <p class="text-xs text-gray-400">
                    <span v-if="concert.city">{{ concert.city }}</span>
                    <span v-if="concert.concertDate"> · {{ concert.concertDate }}</span>
                  </p>

                  <!-- 标签 -->
                  <div
                    v-if="concert.tags"
                    class="flex flex-wrap gap-2 mt-1"
                  >
                    <el-tag
                      v-for="tag in concert.tags.split(',')"
                      :key="tag"
                      size="small"
                      effect="plain"
                      class="!border-[#00d4ff]/30 !text-[#00d4ff] flicker"
                    >
                      {{ tag }}
                    </el-tag>
                  </div>

                  <div class="mt-2 flex justify-between items-center">
                    <el-button
                      text
                      size="small"
                      class="!text-[#00d4ff] hover:!text-[#00ffcc]"
                      @click.stop="openEditDialog()"
                    >
                      编辑
                    </el-button>
                    <el-popconfirm
                      title="确定删除该记录？"
                      confirm-button-text="删除"
                      cancel-button-text="取消"
                      confirm-button-type="danger"
                      @confirm="handleDelete(concert.id)"
                    >
                      <template #reference>
                        <el-button
                          text
                          size="small"
                          class="!text-red-400 hover:!text-red-300"
                        >
                          删除
                        </el-button>
                      </template>
                    </el-popconfirm>
                  </div>
                </div>
              </div>
            </div>

            <!-- 评价卡片 -->
            <div
              v-if="concert.comment"
              class="card-3d"
            >
              <div class="card-3d-inner rounded-2xl glass-effect p-4 transition-all">
                <div class="flex items-start gap-3">
                  <el-icon class="text-[#00d4ff]/60 mt-0.5 flex-shrink-0">
                    <ChatLineRound />
                  </el-icon>
                  <div class="flex-1 min-w-0">
                    <p class="text-sm text-gray-300 leading-relaxed">
                      <span class="text-[#00d4ff]/60">"</span>{{ concert.comment }}<span class="text-[#00d4ff]/60">"</span>
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>

      <!-- 分页 -->
      <div
        v-if="total > 0"
        class="mt-6 flex justify-end"
      >
        <el-pagination
          v-model:current-page="pageNo"
          v-model:page-size="pageSize"
          layout="prev, pager, next"
          :total="total"
          :page-sizes="[8, 16, 32]"
          @current-change="loadConcerts"
          @size-change="loadConcerts"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Headset, StarFilled, Search, ChatLineRound } from '@element-plus/icons-vue'

const loading = ref(false)
const concertList = ref<any[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(8)
const filterTag = ref<string | 'all'>('all')
const keyword = ref('')

const tagOptions = [
  { label: '全部', value: 'all' as const },
  { label: '流行', value: '流行' },
  { label: '摇滚', value: '摇滚' },
  { label: '民谣', value: '民谣' },
  { label: '电子', value: '电子' },
  { label: '说唱', value: '说唱' },
]

const imageErrorMap = reactive<Record<number, boolean>>({})

// 假数据
const mockConcerts = [
  {
    id: 1,
    artist: '周杰伦',
    venue: '北京鸟巢',
    city: '北京',
    concertDate: '2024-12-25',
    rating: 9.5,
    tags: '流行,华语',
    comment: '现场氛围太棒了，经典歌曲一首接一首，全场大合唱！',
    imageUrl: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop',
  },
  {
    id: 2,
    artist: '五月天',
    venue: '上海梅赛德斯奔驰文化中心',
    city: '上海',
    concertDate: '2024-11-20',
    rating: 9.8,
    tags: '摇滚,华语',
    comment: '五月天的现场感染力太强了，三个小时完全不够！',
    imageUrl: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop',
  },
  {
    id: 3,
    artist: 'Taylor Swift',
    venue: 'Tokyo Dome',
    city: '东京',
    concertDate: '2024-10-15',
    rating: 9.7,
    tags: '流行,欧美',
    comment: 'Taylor的舞台设计太震撼了，每一首歌都是视觉盛宴！',
    imageUrl: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop',
  },
  {
    id: 4,
    artist: '李健',
    venue: '深圳湾体育中心',
    city: '深圳',
    concertDate: '2024-09-10',
    rating: 9.3,
    tags: '民谣,华语',
    comment: '李健的声音太治愈了，现场听《贝加尔湖畔》简直是一种享受。',
    imageUrl: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop',
  },
  {
    id: 5,
    artist: 'Coldplay',
    venue: 'Singapore National Stadium',
    city: '新加坡',
    concertDate: '2024-08-05',
    rating: 9.6,
    tags: '摇滚,欧美',
    comment: 'Coldplay的现场太震撼了，全场手环同步闪烁，美轮美奂！',
    imageUrl: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop',
  },
  {
    id: 6,
    artist: '陈奕迅',
    venue: '香港红磡体育馆',
    city: '香港',
    concertDate: '2024-07-20',
    rating: 9.4,
    tags: '流行,华语',
    comment: 'Eason的演唱会总是那么精彩，每一首歌都唱到心里。',
    imageUrl: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop',
  },
  {
    id: 7,
    artist: 'Billie Eilish',
    venue: '台北小巨蛋',
    city: '台北',
    concertDate: '2024-06-15',
    rating: 9.2,
    tags: '流行,电子,欧美',
    comment: 'Billie的现场太有感染力了，年轻一代的偶像！',
    imageUrl: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop',
  },
  {
    id: 8,
    artist: '朴树',
    venue: '成都东郊记忆',
    city: '成都',
    concertDate: '2024-05-10',
    rating: 9.0,
    tags: '民谣,摇滚,华语',
    comment: '朴树的现场很真实，没有太多花哨，就是纯粹的音乐。',
    imageUrl: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop',
  },
]

function loadConcerts() {
  loading.value = true
  setTimeout(() => {
    let filtered = [...mockConcerts]
    
    // 标签筛选
    if (filterTag.value !== 'all') {
      filtered = filtered.filter(concert => 
        concert.tags && concert.tags.includes(filterTag.value)
      )
    }
    
    // 关键词搜索
    if (keyword.value) {
      const kw = keyword.value.toLowerCase()
      filtered = filtered.filter(concert => 
        concert.artist.toLowerCase().includes(kw) || 
        (concert.venue && concert.venue.toLowerCase().includes(kw)) ||
        (concert.city && concert.city.toLowerCase().includes(kw))
      )
    }
    
    // 计算总数
    total.value = filtered.length
    
    // 分页
    const start = (pageNo.value - 1) * pageSize.value
    const end = start + pageSize.value
    concertList.value = filtered.slice(start, end)
    
    loading.value = false
  }, 300)
}

function changeTag(value: string | 'all') {
  filterTag.value = value
  pageNo.value = 1
  loadConcerts()
}

function handleSearch() {
  pageNo.value = 1
  loadConcerts()
}

function handleImageError(id: number) {
  imageErrorMap[id] = true
}

function goDetail() {
  // 暂时不实现详情页
  ElMessage.info('详情页开发中...')
}

function openCreateDialog() {
  ElMessage.info('添加功能开发中...')
}

function openEditDialog() {
  ElMessage.info('编辑功能开发中...')
}

function handleDelete(id: number) {
  const index = mockConcerts.findIndex(c => c.id === id)
  if (index > -1) {
    mockConcerts.splice(index, 1)
    loadConcerts()
    ElMessage.success('删除成功')
  }
}

onMounted(() => {
  loadConcerts()
})
</script>

<style scoped>
.concert-page {
  padding: 20px;
  min-height: 100%;
}

.neon-text {
  background: linear-gradient(135deg, #00d4ff 0%, #00ffcc 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.float-animation {
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.glow-effect {
  position: relative;
}

.glow-effect::before {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: inherit;
  padding: 2px;
  background: linear-gradient(135deg, #00d4ff, #00ffcc);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.3s;
}

.glow-effect:hover::before {
  opacity: 0.5;
}

.card-3d {
  perspective: 1000px;
}

.card-3d-inner {
  transition: transform 0.3s;
  background: transparent !important;
}

.card-3d:hover .card-3d-inner {
  transform: translateY(-4px);
}

/* 使用全局 glass-effect 样式，与音乐模块一致 */

.flicker {
  animation: flicker 2s infinite;
}

@keyframes flicker {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>

