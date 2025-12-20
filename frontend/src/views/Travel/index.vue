<template>
  <div class="travel-page">
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 sm:mb-8">
      <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-4 sm:mb-0 float-animation">旅游</h1>
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
          placeholder="搜索地点名称…"
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

    <!-- 旅游卡片网格 -->
    <div
      v-loading="loading"
      class="min-h-[120px]"
    >
      <div
        v-if="travelList.length === 0 && !loading"
        class="py-10 text-center text-gray-400"
      >
        暂无记录，点击右上角「添加记录」开始你的旅行之旅。
      </div>

      <div
        v-else
        class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6"
      >
        <template
          v-for="travel in travelList"
          :key="travel.id"
        >
          <div class="flex flex-col gap-2">
            <!-- 旅游卡片 -->
            <div
              class="card-3d group cursor-pointer"
              @click="goDetail()"
            >
              <div class="card-3d-inner rounded-2xl overflow-hidden border border-white/10 hover:border-[#00d4ff]/30 transition-all duration-500 glow-effect bg-transparent">
                <!-- 图片 -->
                <div class="relative aspect-[4/3] overflow-hidden">
                  <img
                    v-if="travel.imageUrl && !imageErrorMap[travel.id]"
                    :src="travel.imageUrl"
                    :alt="travel.location"
                    class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
                    loading="lazy"
                    @error="handleImageError(travel.id)"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center bg-gradient-to-br from-[#00d4ff]/20 to-[#00ffcc]/10">
                    <el-icon :size="48" class="text-[#c3cfe2]/40">
                      <Location />
                    </el-icon>
                  </div>

                  <!-- 评分 -->
                  <div
                    v-if="travel.rating != null"
                    class="absolute bottom-3 left-3 flex items-center space-x-1 bg-black/60 backdrop-blur-sm px-2 py-1 rounded-lg"
                  >
                    <el-icon class="text-yellow-400"><StarFilled /></el-icon>
                    <span class="text-white text-sm font-semibold">{{ travel.rating }}</span>
                  </div>
                </div>

                <!-- 信息 -->
                <div class="p-4 flex flex-col gap-2 glass-effect">
                  <h3 class="text-lg font-semibold text-white line-clamp-1">{{ travel.location }}</h3>
                  <p class="text-xs text-gray-400">
                    <span v-if="travel.country">{{ travel.country }}</span>
                    <span v-if="travel.travelDate"> · {{ travel.travelDate }}</span>
                  </p>

                  <!-- 标签 -->
                  <div
                    v-if="travel.tags"
                    class="flex flex-wrap gap-2 mt-1"
                  >
                    <el-tag
                      v-for="tag in travel.tags.split(',')"
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
                      @confirm="handleDelete(travel.id)"
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
              v-if="travel.comment"
              class="card-3d"
            >
              <div class="card-3d-inner rounded-2xl glass-effect p-4 transition-all">
                <div class="flex items-start gap-3">
                  <el-icon class="text-[#00d4ff]/60 mt-0.5 flex-shrink-0">
                    <ChatLineRound />
                  </el-icon>
                  <div class="flex-1 min-w-0">
                    <p class="text-sm text-gray-300 leading-relaxed">
                      <span class="text-[#00d4ff]/60">"</span>{{ travel.comment }}<span class="text-[#00d4ff]/60">"</span>
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
          @current-change="loadTravels"
          @size-change="loadTravels"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Location, StarFilled, Search, ChatLineRound } from '@element-plus/icons-vue'

const loading = ref(false)
const travelList = ref<any[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(8)
const filterTag = ref<string | 'all'>('all')
const keyword = ref('')

const tagOptions = [
  { label: '全部', value: 'all' as const },
  { label: '国内', value: '国内' },
  { label: '国外', value: '国外' },
  { label: '自然风光', value: '自然风光' },
  { label: '历史文化', value: '历史文化' },
  { label: '城市', value: '城市' },
]

const imageErrorMap = reactive<Record<number, boolean>>({})

// 假数据 - 使用更可靠的图片URL
const mockTravels = [
  {
    id: 1,
    location: '北京',
    country: '中国',
    imageUrl: 'https://picsum.photos/800/600?random=1',
    travelDate: '2025-01-10',
    rating: 9.0,
    tags: '国内,历史文化,城市',
    comment: '故宫和天坛都太震撼了，感受到了深厚的历史底蕴。',
  },
  {
    id: 2,
    location: '杭州西湖',
    country: '中国',
    imageUrl: 'https://picsum.photos/800/600?random=2',
    travelDate: '2024-12-25',
    rating: 9.5,
    tags: '国内,自然风光,城市',
    comment: '西湖的美景让人流连忘返，特别是断桥残雪。',
  },
  {
    id: 3,
    location: '日本京都',
    country: '日本',
    imageUrl: 'https://picsum.photos/800/600?random=3',
    travelDate: '2024-11-15',
    rating: 9.8,
    tags: '国外,历史文化,城市',
    comment: '京都的古建筑和樱花季真的太美了，文化氛围浓厚。',
  },
  {
    id: 4,
    location: '西藏拉萨',
    country: '中国',
    imageUrl: 'https://picsum.photos/800/600?random=4',
    travelDate: '2024-10-01',
    rating: 9.2,
    tags: '国内,自然风光,历史文化',
    comment: '布达拉宫的庄严和纳木错的纯净让人心灵震撼。',
  },
  {
    id: 5,
    location: '巴黎',
    country: '法国',
    imageUrl: 'https://picsum.photos/800/600?random=5',
    travelDate: '2024-09-20',
    rating: 9.3,
    tags: '国外,历史文化,城市',
    comment: '埃菲尔铁塔和卢浮宫都是必去的地方，艺术之都名不虚传。',
  },
  {
    id: 6,
    location: '云南大理',
    country: '中国',
    imageUrl: 'https://picsum.photos/800/600?random=6',
    travelDate: '2024-08-15',
    rating: 9.0,
    tags: '国内,自然风光',
    comment: '苍山洱海的美景让人心旷神怡，是一个放松身心的好地方。',
  },
  {
    id: 7,
    location: '冰岛',
    country: '冰岛',
    imageUrl: 'https://picsum.photos/800/600?random=7',
    travelDate: '2024-07-10',
    rating: 9.5,
    tags: '国外,自然风光',
    comment: '极光和蓝湖温泉是此生难忘的体验，大自然的鬼斧神工。',
  },
  {
    id: 8,
    location: '成都',
    country: '中国',
    imageUrl: 'https://picsum.photos/800/600?random=8',
    travelDate: '2024-06-20',
    rating: 8.8,
    tags: '国内,城市',
    comment: '美食之都，火锅和串串都太棒了，还有大熊猫基地。',
  },
]

function loadTravels() {
  loading.value = true
  setTimeout(() => {
    let filtered = [...mockTravels]
    
    // 标签筛选
    if (filterTag.value !== 'all') {
      filtered = filtered.filter(travel => 
        travel.tags && travel.tags.includes(filterTag.value)
      )
    }
    
    // 关键词搜索
    if (keyword.value) {
      const kw = keyword.value.toLowerCase()
      filtered = filtered.filter(travel => 
        travel.location.toLowerCase().includes(kw) || 
        (travel.country && travel.country.toLowerCase().includes(kw))
      )
    }
    
    // 计算总数
    total.value = filtered.length
    
    // 分页
    const start = (pageNo.value - 1) * pageSize.value
    const end = start + pageSize.value
    travelList.value = filtered.slice(start, end)
    
    loading.value = false
  }, 300)
}

function changeTag(value: string | 'all') {
  filterTag.value = value
  pageNo.value = 1
  loadTravels()
}

function handleSearch() {
  pageNo.value = 1
  loadTravels()
}

function handleImageError(travelId: number) {
  imageErrorMap[travelId] = true
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
  const index = mockTravels.findIndex(t => t.id === id)
  if (index > -1) {
    mockTravels.splice(index, 1)
    loadTravels()
    ElMessage.success('删除成功')
  }
}

onMounted(() => {
  loadTravels()
})
</script>

<style scoped>
.travel-page {
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

