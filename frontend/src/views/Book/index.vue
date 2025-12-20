<template>
  <div class="book-page">
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 sm:mb-8">
      <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-4 sm:mb-0 float-animation">阅读</h1>
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
            v-for="status in statusOptions"
            :key="status.value"
            :type="filterStatus === status.value ? 'primary' : 'default'"
            @click="changeStatus(status.value)"
            :class="filterStatus === status.value ? '!bg-[#00d4ff] !border-[#00d4ff]' : ''"
          >
            <span>{{ status.label }}</span>
            <span
              v-if="status.value !== 'all' && statusCounts[status.value]"
              class="ml-1.5 px-1.5 py-0.5 rounded text-xs font-medium"
              :class="filterStatus === status.value 
                ? 'bg-white/20 text-white' 
                : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300'"
            >
              {{ statusCounts[status.value] }}
            </span>
          </el-button>
        </div>
        <el-input
          v-model="keyword"
          placeholder="搜索书名、作者…"
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

    <!-- 书籍卡片网格 -->
    <div
      v-loading="loading"
      class="min-h-[120px]"
    >
      <div
        v-if="bookList.length === 0 && !loading"
        class="py-10 text-center text-gray-400"
      >
        暂无记录，点击右上角「添加记录」开始你的阅读之旅。
      </div>

      <div
        v-else
        class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6"
      >
        <template
          v-for="book in bookList"
          :key="book.id"
        >
          <div class="flex flex-col gap-2">
            <!-- 书籍卡片 -->
            <div
              class="card-3d group cursor-pointer"
              @click="goDetail()"
            >
              <div class="card-3d-inner rounded-2xl overflow-hidden border border-white/10 hover:border-[#00d4ff]/30 transition-all duration-500 glow-effect bg-transparent">
                <!-- 封面 -->
                <div class="relative aspect-[3/4] overflow-hidden">
                  <img
                    v-if="book.coverUrl && !imageErrorMap[book.id]"
                    :src="book.coverUrl"
                    :alt="book.title"
                    class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
                    loading="lazy"
                    @error="handleImageError(book.id)"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center bg-gradient-to-br from-[#00d4ff]/20 to-[#00ffcc]/10">
                    <el-icon :size="48" class="text-[#c3cfe2]/40">
                      <Document />
                    </el-icon>
                  </div>

                  <!-- 状态标签 -->
                  <div class="absolute top-3 right-3">
                    <el-tag
                      :type="getStatusType(book.readStatus) as any"
                      size="small"
                      effect="dark"
                      class="backdrop-blur-md"
                    >
                      {{ getStatusLabel(book.readStatus) }}
                    </el-tag>
                  </div>

                  <!-- 评分 -->
                  <div
                    v-if="book.personalRating != null"
                    class="absolute bottom-3 left-3 flex items-center space-x-1 bg-black/60 backdrop-blur-sm px-2 py-1 rounded-lg"
                  >
                    <el-icon class="text-yellow-400"><StarFilled /></el-icon>
                    <span class="text-white text-sm font-semibold">{{ book.personalRating }}</span>
                  </div>
                </div>

                <!-- 信息 -->
                <div class="p-4 flex flex-col gap-2 glass-effect">
                  <h3 class="text-lg font-semibold text-white line-clamp-1">{{ book.title }}</h3>
                  <p class="text-sm text-[#00d4ff]/80 line-clamp-1">{{ book.author }}</p>
                  <p class="text-xs text-gray-400">
                    <span v-if="book.publishYear">{{ book.publishYear }}</span>
                    <span v-if="book.readDate"> · 已读于 {{ book.readDate }}</span>
                  </p>

                  <!-- 标签 -->
                  <div
                    v-if="book.tags"
                    class="flex flex-wrap gap-2 mt-1"
                  >
                    <el-tag
                      v-for="tag in book.tags.split(',')"
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
                      @confirm="handleDelete(book.id)"
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
              v-if="book.comment"
              class="card-3d"
            >
              <div class="card-3d-inner rounded-2xl glass-effect p-4 transition-all">
                <div class="flex items-start gap-3">
                  <el-icon class="text-[#00d4ff]/60 mt-0.5 flex-shrink-0">
                    <ChatLineRound />
                  </el-icon>
                  <div class="flex-1 min-w-0">
                    <p class="text-sm text-gray-300 leading-relaxed">
                      <span class="text-[#00d4ff]/60">"</span>{{ book.comment }}<span class="text-[#00d4ff]/60">"</span>
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
          @current-change="loadBooks"
          @size-change="loadBooks"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Document, StarFilled, Search, ChatLineRound } from '@element-plus/icons-vue'

const loading = ref(false)
const bookList = ref<any[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(8)
const filterStatus = ref<number | 'all'>('all')
const keyword = ref('')
const statusCounts = ref<Record<number, number>>({})

const statusOptions = [
  { label: '全部', value: 'all' as const },
  { label: '想读', value: 1 },
  { label: '在读', value: 2 },
  { label: '已读', value: 3 },
  { label: '弃读', value: 4 },
]

const imageErrorMap = reactive<Record<number, boolean>>({})

// 假数据
const mockBooks = [
  {
    id: 1,
    title: '百年孤独',
    author: '加西亚·马尔克斯',
    coverUrl: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400&h=600&fit=crop',
    publishYear: 1967,
    readStatus: 3,
    personalRating: 9.5,
    readDate: '2025-01-15',
    tags: '魔幻现实主义,文学经典',
    comment: '一部震撼人心的魔幻现实主义巨作，马尔克斯的想象力令人叹为观止。',
  },
  {
    id: 2,
    title: '1984',
    author: '乔治·奥威尔',
    coverUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=600&fit=crop',
    publishYear: 1949,
    readStatus: 3,
    personalRating: 9.8,
    readDate: '2025-01-10',
    tags: '反乌托邦,政治',
    comment: '对极权主义的深刻警示，至今仍具有强烈的现实意义。',
  },
  {
    id: 3,
    title: '三体',
    author: '刘慈欣',
    coverUrl: 'https://images.unsplash.com/photo-1532012192227-25953d6b0743?w=400&h=600&fit=crop',
    publishYear: 2006,
    readStatus: 2,
    personalRating: 9.2,
    readDate: null,
    tags: '科幻,硬科幻',
    comment: null,
  },
  {
    id: 4,
    title: '活着',
    author: '余华',
    coverUrl: 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=600&fit=crop',
    publishYear: 1993,
    readStatus: 3,
    personalRating: 9.0,
    readDate: '2024-12-20',
    tags: '当代文学,人生',
    comment: '用最朴实的语言讲述最深刻的人生，让人深思。',
  },
  {
    id: 5,
    title: '人类简史',
    author: '尤瓦尔·赫拉利',
    coverUrl: 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&h=600&fit=crop',
    publishYear: 2011,
    readStatus: 1,
    personalRating: null,
    readDate: null,
    tags: '历史,人类学',
    comment: null,
  },
  {
    id: 6,
    title: '解忧杂货店',
    author: '东野圭吾',
    coverUrl: 'https://images.unsplash.com/photo-1506880018603-83d5b814b5a6?w=400&h=600&fit=crop',
    publishYear: 2012,
    readStatus: 3,
    personalRating: 8.5,
    readDate: '2024-11-15',
    tags: '推理,治愈',
    comment: '温暖治愈的故事，让人感受到人性的美好。',
  },
]

function loadBooks() {
  loading.value = true
  setTimeout(() => {
    let filtered = [...mockBooks]
    
    // 状态筛选
    if (filterStatus.value !== 'all') {
      filtered = filtered.filter(book => book.readStatus === filterStatus.value)
    }
    
    // 关键词搜索
    if (keyword.value) {
      const kw = keyword.value.toLowerCase()
      filtered = filtered.filter(book => 
        book.title.toLowerCase().includes(kw) || 
        book.author.toLowerCase().includes(kw)
      )
    }
    
    // 计算总数
    total.value = filtered.length
    
    // 分页
    const start = (pageNo.value - 1) * pageSize.value
    const end = start + pageSize.value
    bookList.value = filtered.slice(start, end)
    
    // 计算状态数量
    statusCounts.value = {
      1: mockBooks.filter(b => b.readStatus === 1).length,
      2: mockBooks.filter(b => b.readStatus === 2).length,
      3: mockBooks.filter(b => b.readStatus === 3).length,
      4: mockBooks.filter(b => b.readStatus === 4).length,
    }
    
    loading.value = false
  }, 300)
}

function changeStatus(value: number | 'all') {
  filterStatus.value = value
  pageNo.value = 1
  loadBooks()
}

function handleSearch() {
  pageNo.value = 1
  loadBooks()
}

function getStatusType(status: number) {
  const map: Record<number, string> = {
    1: 'info',
    2: 'warning',
    3: 'success',
    4: 'danger',
  }
  return map[status] || 'info'
}

function getStatusLabel(status: number) {
  const map: Record<number, string> = {
    1: '想读',
    2: '在读',
    3: '已读',
    4: '弃读',
  }
  return map[status] || '-'
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
  const index = mockBooks.findIndex(b => b.id === id)
  if (index > -1) {
    mockBooks.splice(index, 1)
    loadBooks()
    ElMessage.success('删除成功')
  }
}

onMounted(() => {
  loadBooks()
})
</script>

<style scoped>
.book-page {
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

