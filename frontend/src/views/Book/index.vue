<template>
  <div class="book-page">
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 sm:mb-8">
      <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-4 sm:mb-0 float-animation">阅读</h1>
      <AnimatedButton variant="primary" @click="openCreateDialog">
        <el-icon><Plus /></el-icon>
        添加记录
      </AnimatedButton>
    </div>

    <!-- 筛选栏 -->
    <div class="mb-6">
      <div class="flex flex-col gap-3">
        <div class="flex flex-wrap gap-2 w-full">
          <AnimatedButton
            v-for="status in statusOptions"
            :key="status.value"
            :variant="filterStatus === status.value ? 'primary' : 'secondary'"
            size="small"
            @click="changeStatus(status.value)"
          >
            <span>{{ status.label }}</span>
            <span
              v-if="status.value !== 'all' && statusCounts[status.value]"
              class="ml-1.5 px-1.5 py-0.5 rounded text-xs font-medium"
              :class="filterStatus === status.value 
                ? 'bg-white/20 text-white' 
                : 'bg-white/10 text-gray-300'"
            >
              {{ statusCounts[status.value] }}
            </span>
          </AnimatedButton>
        </div>
        <AnimatedSearch
          v-model="keyword"
          placeholder="搜索书名、作者…"
          @enter="handleSearch"
          @clear="handleSearch"
        />
      </div>
    </div>

    <!-- 书籍卡片网格 -->
    <div class="min-h-[120px] relative">
      <!-- 骨架屏加载 -->
      <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6">
        <SkeletonCard v-for="i in pageSize" :key="`skeleton-${i}`" />
      </div>
      
      <EmptyState
        v-else-if="bookList.length === 0"
        title="暂无记录"
        description="点击右上角「添加记录」开始你的阅读之旅"
      >
        <template #icon>
          <el-icon :size="64" class="text-gray-500">
            <Document />
          </el-icon>
        </template>
        <template #action>
          <AnimatedButton variant="primary" @click="openCreateDialog">
            <el-icon><Plus /></el-icon>
            添加第一条记录
          </AnimatedButton>
        </template>
        <template #guide>
          <div class="guide-inline-panel glass-effect">
            <div class="guide-step-list">
              <div
                v-for="(step, idx) in guideSteps"
                :key="step.title"
                class="guide-step-card"
              >
                <div class="guide-step-index">{{ idx + 1 }}</div>
                <div class="guide-step-content">
                  <p class="guide-step-title">{{ step.title }}</p>
                  <p class="guide-step-desc">{{ step.description }}</p>
                </div>
              </div>
            </div>
            <div class="guide-inline-actions">
              <AnimatedButton size="small" variant="primary" @click="openGuide">
                开始快速指引
              </AnimatedButton>
              <AnimatedButton size="small" variant="secondary" @click="openCreateDialog">
                直接添加
              </AnimatedButton>
            </div>
          </div>
        </template>
      </EmptyState>

      <div
        v-else
        class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6"
      >
        <template
          v-for="(book, index) in bookList"
          :key="book.recordId"
        >
          <div class="flex flex-col gap-2 book-card-wrapper" :style="{ animationDelay: `${index * 50}ms` }">
            <!-- 书籍卡片 -->
            <AnimatedCard
              variant="3d"
              class="group cursor-pointer"
              @click="goDetail(book.bookId)"
            >
              <div class="rounded-2xl overflow-hidden">
                <!-- 封面 -->
                <div class="relative aspect-[3/4] overflow-hidden book-poster-container">
                  <div class="poster-overlay"></div>
                  <img
                    v-if="book.coverUrl && !imageErrorMap[book.bookId]"
                    :src="book.coverUrl"
                    :alt="book.title"
                    class="book-poster w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
                    loading="lazy"
                    @error="handleImageError(book.bookId)"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center bg-gradient-to-br from-[#00d4ff]/20 to-[#00ffcc]/10 poster-placeholder">
                    <el-icon :size="48" class="text-[#c3cfe2]/40">
                      <Document />
                    </el-icon>
                  </div>

                  <!-- 状态标签 -->
                  <div class="absolute top-3 right-3 status-badge-wrapper">
                    <div class="status-badge" :class="`status-badge-${book.readStatus}`">
                      <div class="status-badge-glow"></div>
                      <div class="status-badge-ripple"></div>
                      <el-tag
                        :type="getStatusType(book.readStatus) as any"
                        size="small"
                        effect="dark"
                        class="backdrop-blur-md status-tag"
                        @click.stop="handleStatusClick(book)"
                        @mouseenter="handleStatusHover"
                      >
                        <span class="status-text">{{ getStatusLabel(book.readStatus) }}</span>
                      </el-tag>
                    </div>
                  </div>

                  <!-- 评分 -->
                  <div
                    v-if="book.personalRating != null"
                    class="rating-badge absolute bottom-3 left-3 flex items-center space-x-1 bg-black/60 backdrop-blur-sm px-2 py-1 rounded-lg"
                  >
                    <el-icon class="text-yellow-400 rating-star"><StarFilled /></el-icon>
                    <span class="text-white text-sm font-semibold rating-value">{{ book.personalRating }}</span>
                  </div>
                </div>

                <!-- 信息 -->
                <div class="p-4 flex flex-col gap-2 glass-effect">
                  <h3 class="text-lg font-semibold text-white line-clamp-1">{{ book.title }}</h3>
                  <p class="text-sm text-[#00d4ff]/80 line-clamp-1">{{ book.author }}</p>
                  <p class="text-xs text-gray-400">
                    <span v-if="book.readDate">已读于 {{ book.readDate }}</span>
                  </p>

                  <!-- 图书类型 -->
                  <div
                    v-if="book.genre"
                    class="flex flex-wrap gap-2 mt-1 book-tags"
                  >
                    <AnimatedTag
                      v-for="(genre, tagIndex) in book.genre.split(',')"
                      :key="genre"
                      variant="glow"
                      :animated="tagIndex % 2 === 0"
                      class="book-tag-item"
                    >
                      {{ genre }}
                    </AnimatedTag>
                  </div>

                  <!-- 标签 -->
                  <div
                    v-if="book.tags"
                    class="flex flex-wrap gap-2 mt-1 book-tags"
                  >
                    <AnimatedTag
                      v-for="(tag, tagIndex) in book.tags.split(',')"
                      :key="tag"
                      variant="glow"
                      :animated="tagIndex % 2 === 0"
                      class="book-tag-item"
                    >
                      {{ tag }}
                    </AnimatedTag>
                  </div>

                  <div class="mt-2 flex justify-between items-center book-actions">
                    <AnimatedButton
                      variant="outline"
                      size="small"
                      @click.stop="openEditDialog(book)"
                      class="action-btn edit-btn"
                    >
                      编辑
                    </AnimatedButton>
                    <el-popconfirm
                      title="确定删除该记录？"
                      confirm-button-text="删除"
                      cancel-button-text="取消"
                      confirm-button-type="danger"
                      @confirm="handleDelete(book.recordId)"
                    >
                      <template #reference>
                        <button
                          @click.stop
                          class="action-btn delete-btn"
                        >
                          删除
                        </button>
                      </template>
                    </el-popconfirm>
                  </div>
                </div>
              </div>
            </AnimatedCard>

            <!-- 评价卡片 -->
            <AnimatedCard
              v-if="book.comment"
              variant="glass"
              class="comment-card"
            >
              <div class="p-4">
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
            </AnimatedCard>
          </div>
        </template>
      </div>

      <!-- 分页 -->
      <div
        v-if="total > 0"
        class="mt-6 flex justify-end pagination-wrapper"
      >
        <el-pagination
          v-model:current-page="pageNo"
          v-model:page-size="pageSize"
          layout="prev, pager, next"
          :total="total"
          :page-sizes="[8, 16, 32]"
          @current-change="loadBooks"
          @size-change="loadBooks"
          class="animated-pagination"
        />
      </div>
    </div>

    <!-- 快速引导浮层 -->
    <transition name="guide-fade">
      <div v-if="guideVisible" class="guide-overlay">
        <div class="guide-modal glass-effect">
          <div class="guide-modal-header">
            <div>
              <p class="guide-modal-label">快速开始</p>
              <h3 class="guide-modal-title">3 步搞定你的阅读清单</h3>
            </div>
            <button class="guide-close" aria-label="close" @click="closeGuide(true)">×</button>
          </div>
          <div class="guide-modal-body">
            <div class="guide-modal-steps">
              <div
                v-for="(step, idx) in guideSteps"
                :key="step.title"
                class="guide-modal-card"
              >
                <div class="guide-modal-index">{{ idx + 1 }}</div>
                <div class="guide-modal-content">
                  <p class="guide-modal-card-title">{{ step.title }}</p>
                  <p class="guide-modal-card-desc">{{ step.description }}</p>
                </div>
                <span class="guide-modal-pill">{{ step.action }}</span>
              </div>
            </div>
          </div>
          <div class="guide-modal-footer">
            <AnimatedButton variant="secondary" size="small" @click="closeGuide(false)">
              稍后再看
            </AnimatedButton>
            <AnimatedButton variant="primary" size="small" @click="closeGuide(true)">
              已了解，开始使用
            </AnimatedButton>
          </div>
        </div>
      </div>
    </transition>

    <!-- 新建 / 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '添加图书记录' : '编辑图书记录'"
      width="520px"
      destroy-on-close
      class="book-dialog"
    >
      <div class="book-form-wrapper glass-effect">
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="80px"
          class="book-form"
        >
          <el-form-item
            label="书名"
            prop="title"
          >
            <el-input v-model="form.title" placeholder="请输入书名" />
          </el-form-item>

          <el-form-item label="作者">
            <el-input v-model="form.author" placeholder="请输入作者" />
          </el-form-item>

          <el-form-item label="类型">
            <el-input
              v-model="form.genre"
              placeholder="例如：小说,魔幻现实主义"
            />
          </el-form-item>

          <el-form-item label="简介">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="3"
              placeholder="请输入简介"
            />
          </el-form-item>

          <el-form-item label="封面">
            <div class="w-full">
              <el-upload
                :auto-upload="true"
                :on-success="handleCoverUploadSuccess"
                :on-error="handleCoverUploadError"
                :before-upload="beforeCoverUpload"
                :show-file-list="false"
                accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
                :http-request="handleCoverUpload"
                class="cover-upload"
              >
                <template #trigger>
                  <el-button type="primary" :loading="coverUploading">
                    <el-icon><UploadFilled /></el-icon>
                    选择封面
                  </el-button>
                </template>
              </el-upload>
              <div
                v-if="form.coverUrl"
                class="cover-preview mt-3"
              >
                <img
                  :src="form.coverUrl"
                  alt="cover preview"
                />
                <el-button
                  type="danger"
                  size="small"
                  circle
                  class="remove-cover-btn"
                  @click="form.coverUrl = ''"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
          </el-form-item>

          <el-form-item label="阅读状态">
            <el-select v-model="form.readStatus">
              <el-option :value="1" label="想读" />
              <el-option :value="2" label="在读" />
              <el-option :value="3" label="已读" />
              <el-option :value="4" label="弃读" />
            </el-select>
          </el-form-item>

          <el-form-item label="个人评分">
            <el-input-number
              v-model="form.personalRating"
              :min="0"
              :max="10"
              :step="0.5"
              controls-position="right"
              class="w-full"
            />
          </el-form-item>

          <el-form-item label="评价">
            <el-input
              v-model="form.comment"
              type="textarea"
              :rows="3"
              placeholder="说点什么吧"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <AnimatedButton variant="secondary" @click="dialogVisible = false">
            取消
          </AnimatedButton>
          <AnimatedButton
            variant="primary"
            :loading="submitLoading"
            @click="handleSubmit"
          >
            确认
          </AnimatedButton>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElDialog, ElForm, ElFormItem, ElInput, ElInputNumber, ElButton, FormInstance, FormRules } from 'element-plus'
import { Plus, Document, StarFilled, Search, ChatLineRound, UploadFilled, Delete } from '@element-plus/icons-vue'
import { getBookPage, createBook, updateBookRecord, deleteBookRecord } from '@/api/book'
import { uploadCoverImage } from '@/api/photo'
import type { BookPageItem, BookCreateReq, BookRecordUpdateReq } from '@/types/api'
import { 
  AnimatedButton, 
  AnimatedCard, 
  EmptyState,
  SkeletonCard,
  AnimatedSearch,
  AnimatedTag,
} from '@/components/uiverse'

const loading = ref(false)
const bookList = ref<BookPageItem[]>([])
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

const GUIDE_STORAGE_KEY = 'book_onboarding_seen'
const guideVisible = ref(false)
const guideAutoShown = ref(false)
const guideSteps = [
  {
    title: '添加第一条记录',
    description: '点击右上角"添加记录"，录入想读的书籍，支持上传封面与标签。',
    action: '添加记录',
  },
  {
    title: '用筛选快速定位',
    description: '状态筛选 + 关键词搜索，快速找到在读/已读，列表支持分页。',
    action: '筛选/搜索',
  },
  {
    title: '评分与评价',
    description: '在卡片上直接编辑、评分、标记状态，保持你的阅读清单实时更新。',
    action: '编辑/评分',
  },
]

async function loadBooks() {
  loading.value = true
  try {
    const res = await getBookPage({
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      readStatus: filterStatus.value === 'all' ? undefined : filterStatus.value,
      keyword: keyword.value || undefined,
    })
    bookList.value = res.data.page.list
    total.value = res.data.page.total
    statusCounts.value = res.data.statusCounts
    maybeShowGuide()
  } catch (e) {
    // 已统一处理
  } finally {
    loading.value = false
  }
}

function hasSeenGuide() {
  try {
    return localStorage.getItem(GUIDE_STORAGE_KEY) === '1'
  } catch (err) {
    console.warn('read guide flag failed', err)
    return false
  }
}

function setGuideSeen() {
  try {
    localStorage.setItem(GUIDE_STORAGE_KEY, '1')
  } catch (err) {
    console.warn('persist guide flag failed', err)
  }
}

function maybeShowGuide() {
  if (guideAutoShown.value) return
  if (hasSeenGuide()) return
  if ((total.value || 0) === 0) {
    guideVisible.value = true
    guideAutoShown.value = true
  }
}

function openGuide() {
  guideVisible.value = true
}

function closeGuide(markSeen = false) {
  guideVisible.value = false
  if (markSeen) {
    setGuideSeen()
  }
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

function goDetail(bookId?: number) {
  if (!bookId) return
  // 暂时不实现详情页
  ElMessage.info('详情页开发中...')
}

function handleStatusClick(book: BookPageItem) {
  changeStatus(book.readStatus)
  window.scrollTo({ top: 0, behavior: 'smooth' })
  ElMessage.success(`已筛选：${getStatusLabel(book.readStatus)}`)
}

function handleStatusHover() {
  // 可以在这里添加悬停时的额外逻辑
}

// ========== 新建 / 编辑表单 ==========
type DialogMode = 'create' | 'edit'

const dialogVisible = ref(false)
const dialogMode = ref<DialogMode>('create')
const formRef = ref<FormInstance>()

const form = reactive<{
  recordId?: number
  title: string
  author?: string
  genre?: string
  description?: string
  coverUrl?: string
  personalRating?: number
  comment?: string
  readStatus?: number
}>({
  title: '',
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
}

function resetForm() {
  form.recordId = undefined
  form.title = ''
  form.author = ''
  form.genre = ''
  form.description = ''
  form.coverUrl = ''
  form.personalRating = undefined
  form.comment = ''
  form.readStatus = 1
}

function openCreateDialog() {
  dialogMode.value = 'create'
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(item: BookPageItem) {
  dialogMode.value = 'edit'
  form.recordId = item.recordId
  form.title = item.title
  form.author = item.author
  form.coverUrl = item.coverUrl
  form.readStatus = item.readStatus
  form.personalRating = item.personalRating
  form.comment = item.comment || ''
  dialogVisible.value = true
}

const submitLoading = ref(false)
const coverUploading = ref(false)

// 封面上传处理
const MAX_COVER_SIZE = 50 * 1024 * 1024 // 50MB

const beforeCoverUpload = (file: File) => {
  // 检查文件大小
  if (file.size > MAX_COVER_SIZE) {
    ElMessage.error(`文件大小超过50MB，请选择较小的文件`)
    return false
  }
  
  // 检查文件类型
  const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
  if (!validTypes.includes(file.type)) {
    ElMessage.error(`文件格式不支持，请选择 jpg、png、gif 或 webp 格式的图片`)
    return false
  }
  
  return true
}

const handleCoverUpload = async (options: any) => {
  const { file } = options
  coverUploading.value = true
  try {
    const res = await uploadCoverImage(file)
    if (res.code === 0 && res.data) {
      form.coverUrl = res.data
      ElMessage.success('封面上传成功')
    } else {
      ElMessage.error(res.msg || '封面上传失败')
    }
  } catch (error: any) {
    console.error('封面上传失败:', error)
    ElMessage.error(error.message || '封面上传失败')
  } finally {
    coverUploading.value = false
  }
}

const handleCoverUploadSuccess = () => {
  // 成功处理已在 handleCoverUpload 中完成
}

const handleCoverUploadError = () => {
  ElMessage.error('封面上传失败')
  coverUploading.value = false
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()

  try {
    submitLoading.value = true
    if (dialogMode.value === 'create') {
      await createBook({
        title: form.title,
        author: form.author,
        genre: form.genre,
        description: form.description,
        coverUrl: form.coverUrl,
        readStatus: form.readStatus,
        personalRating: form.personalRating,
        comment: form.comment,
      })
      ElMessage.success('创建成功')
    } else if (dialogMode.value === 'edit' && form.recordId) {
      await updateBookRecord(form.recordId, {
        id: form.recordId,
        readStatus: form.readStatus,
        personalRating: form.personalRating,
        coverUrl: form.coverUrl,
        comment: form.comment,
      })
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    loadBooks()
  } catch (e) {
    // 已统一处理
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(recordId: number) {
  try {
    await deleteBookRecord(recordId)
    ElMessage.success('删除成功')
    if (bookList.value.length === 1 && pageNo.value > 1) {
      pageNo.value -= 1
    }
    loadBooks()
  } catch (e) {
    // 已统一处理
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

/* 书籍卡片进入动画 */
.book-card-wrapper {
  animation: fadeInUp 0.5s ease-out both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 海报容器动画 */
.book-poster-container {
  position: relative;
}

.poster-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom,
    transparent 0%,
    transparent 60%,
    rgba(0, 0, 0, 0.3) 100%
  );
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 1;
  pointer-events: none;
}

.group:hover .poster-overlay {
  opacity: 1;
}

.book-poster {
  position: relative;
  z-index: 0;
}

.poster-placeholder {
  animation: placeholder-pulse 2s ease-in-out infinite;
}

@keyframes placeholder-pulse {
  0%, 100% {
    opacity: 0.6;
  }
  50% {
    opacity: 0.8;
  }
}

/* 状态标签动画 - 复用影视页面的样式 */
.status-badge-wrapper {
  z-index: 2;
  cursor: pointer;
}

.status-badge {
  position: relative;
  animation: badgeBounce 0.6s ease-out;
  transform-origin: top right;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes badgeBounce {
  0% {
    opacity: 0;
    transform: scale(0) rotate(-180deg);
  }
  60% {
    transform: scale(1.1) rotate(10deg);
  }
  100% {
    opacity: 1;
    transform: scale(1) rotate(0deg);
  }
}

.status-badge-glow {
  position: absolute;
  inset: -6px;
  border-radius: 12px;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1;
  filter: blur(8px);
}

.status-badge-1 .status-badge-glow {
  background: radial-gradient(circle, rgba(64, 158, 255, 0.4) 0%, transparent 70%);
}

.status-badge-2 .status-badge-glow {
  background: radial-gradient(circle, rgba(230, 162, 60, 0.4) 0%, transparent 70%);
}

.status-badge-3 .status-badge-glow {
  background: radial-gradient(circle, rgba(103, 194, 58, 0.4) 0%, transparent 70%);
}

.status-badge-4 .status-badge-glow {
  background: radial-gradient(circle, rgba(245, 108, 108, 0.4) 0%, transparent 70%);
}

.status-badge:hover {
  transform: scale(1.15) translateY(-2px);
}

.status-badge:hover .status-badge-glow {
  opacity: 1;
  animation: badge-glow-pulse 1.5s ease-in-out infinite;
}

.status-badge:hover .status-tag {
  transform: scale(1.1);
  box-shadow: 
    0 4px 12px rgba(0, 212, 255, 0.4),
    0 0 20px rgba(0, 212, 255, 0.2);
}

.status-badge:active {
  transform: scale(1.05) translateY(0);
}

.status-badge:active .status-tag {
  animation: badge-click 0.3s ease-out;
}

@keyframes badge-click {
  0%, 100% {
    transform: scale(1.1);
  }
  50% {
    transform: scale(0.95);
  }
}

@keyframes badge-glow-pulse {
  0%, 100% {
    opacity: 0.6;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.2);
  }
}

.status-tag {
  position: relative;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  user-select: none;
  overflow: visible;
}

.status-text {
  position: relative;
  z-index: 1;
  display: inline-block;
  transition: transform 0.3s ease;
}

.status-badge:hover .status-text {
  transform: scale(1.05);
}

.status-badge-ripple {
  position: absolute;
  inset: -8px;
  border-radius: 12px;
  opacity: 0;
  pointer-events: none;
  z-index: 0;
}

.status-badge:active .status-badge-ripple {
  animation: ripple-effect 0.6s ease-out;
}

@keyframes ripple-effect {
  0% {
    opacity: 0.6;
    transform: scale(0.8);
    box-shadow: 0 0 0 0 rgba(0, 212, 255, 0.4);
  }
  50% {
    opacity: 0.3;
    transform: scale(1.2);
    box-shadow: 0 0 0 10px rgba(0, 212, 255, 0);
  }
  100% {
    opacity: 0;
    transform: scale(1.5);
    box-shadow: 0 0 0 20px rgba(0, 212, 255, 0);
  }
}

.group:hover .status-badge {
  transform: scale(1.1) translateY(-2px);
}

.group:hover .status-badge .status-badge-glow {
  opacity: 0.8;
}

.group:hover .status-tag {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 212, 255, 0.3);
}

/* 评分动画 */
.rating-badge {
  z-index: 2;
  animation: ratingSlideUp 0.5s ease-out 0.2s both;
  transition: all 0.3s ease;
}

@keyframes ratingSlideUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.rating-star {
  animation: starTwinkle 2s ease-in-out infinite;
}

@keyframes starTwinkle {
  0%, 100% {
    transform: scale(1) rotate(0deg);
  }
  50% {
    transform: scale(1.2) rotate(180deg);
  }
}

.rating-value {
  font-weight: 700;
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.group:hover .rating-badge {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 4px 15px rgba(255, 215, 0, 0.4);
}

/* 标签容器动画 */
.book-tags {
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

.book-tag-item {
  animation: tagPop 0.3s ease-out both;
}

.book-tag-item:nth-child(1) { animation-delay: 0.1s; }
.book-tag-item:nth-child(2) { animation-delay: 0.2s; }
.book-tag-item:nth-child(3) { animation-delay: 0.3s; }
.book-tag-item:nth-child(4) { animation-delay: 0.4s; }

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

/* 操作按钮动画 */
.book-actions {
  animation: actionsSlideUp 0.4s ease-out 0.4s both;
}

@keyframes actionsSlideUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.action-btn {
  transition: all 0.3s ease;
}

.edit-btn:hover {
  transform: translateX(-2px);
}

.delete-btn {
  padding: 6px 16px;
  font-size: 12px;
  font-weight: 500;
  color: #ef4444;
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.delete-btn:hover {
  color: #fff;
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.5);
  transform: translateX(2px);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

/* 评价卡片动画 */
.comment-card {
  animation: slideInRight 0.4s ease-out;
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 分页动画 */
.pagination-wrapper {
  animation: paginationFadeIn 0.5s ease-out;
}

@keyframes paginationFadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

:deep(.animated-pagination .el-pager li) {
  transition: all 0.3s ease;
}

:deep(.animated-pagination .el-pager li:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 212, 255, 0.3);
}

:deep(.animated-pagination .el-pager li.is-active) {
  background: linear-gradient(135deg, #00d4ff, #00ffcc);
  color: #1a1a2e;
  font-weight: 600;
}

.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 弹窗在小屏下的兼容处理与美化 */
:deep(.book-dialog) {
  /* 桌面端：居中显示 */
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  margin: 0 !important;
  max-height: calc(100vh - 20px);
  display: flex;
  flex-direction: column;
  background: radial-gradient(circle at top left, rgba(0, 212, 255, 0.08), transparent 55%),
    radial-gradient(circle at bottom right, rgba(0, 255, 204, 0.06), transparent 55%),
    rgba(10, 14, 35, 0.96);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow:
    0 18px 45px rgba(0, 0, 0, 0.7),
    0 0 30px rgba(0, 212, 255, 0.25);
}

:deep(.book-dialog .el-dialog__header) {
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

:deep(.book-dialog .el-dialog__title) {
  color: #e5f5ff;
  font-weight: 600;
  letter-spacing: 0.04em;
}

:deep(.book-dialog .el-dialog__body) {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 16px;
}

:deep(.book-dialog .el-dialog__footer) {
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.book-form-wrapper {
  padding: 16px 18px 4px;
  border-radius: 14px;
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.04),
    rgba(0, 212, 255, 0.05),
    rgba(0, 0, 0, 0.3)
  );
}

.book-form {
  --el-text-color-regular: #e5e7f0;
}

:deep(.book-dialog .el-form-item__label) {
  color: #cbd5ff;
}

.cover-upload {
  width: 100%;
}

.cover-preview {
  position: relative;
  width: 120px;
  height: 180px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow:
    0 8px 20px rgba(0, 0, 0, 0.8),
    0 0 15px rgba(0, 212, 255, 0.35);
}

.cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-cover-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  z-index: 10;
}

/* 更小屏幕（如 iPhone SE）下，弹窗改为"近乎全屏"以避免被底部遮挡 */
@media (max-width: 600px) {
  :deep(.book-dialog) {
    transform: none;
    width: 100vw !important;
    max-width: 100vw !important;
    height: 100vh;
    max-height: 100vh;
    top: 0 !important;
    left: 0 !important;
    right: 0 !important;
    margin: 0 !important;
    border-radius: 0;
    border: none;
  }

  :deep(.book-dialog .el-dialog__body) {
    padding: 12px 12px 80px;
  }

  :deep(.book-dialog .el-dialog__footer) {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 60px; /* 预留底部 TabBar 高度，避免被遮挡 */
    z-index: 2000;
    background: linear-gradient(to top, rgba(10, 14, 35, 0.98), transparent);
    padding-top: 8px;
  }
}

/* 引导面板 */
.guide-inline-panel {
  margin-top: 16px;
  padding: 16px;
  border-radius: 14px;
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.04), rgba(0, 212, 255, 0.06));
  box-shadow:
    0 8px 20px rgba(0, 0, 0, 0.3),
    0 0 20px rgba(0, 212, 255, 0.15);
}

.guide-step-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 12px;
}

.guide-step-card {
  display: flex;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.04);
}

.guide-step-index {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #00d4ff, #00ffcc);
  color: #0c1224;
  font-weight: 700;
  font-size: 14px;
}

.guide-step-content {
  text-align: left;
}

.guide-step-title {
  font-size: 14px;
  font-weight: 600;
  color: #e6f6ff;
  margin-bottom: 4px;
}

.guide-step-desc {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.5;
}

.guide-inline-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 14px;
}

/* 引导浮层 */
.guide-overlay {
  position: fixed;
  inset: 0;
  background: radial-gradient(circle at 30% 30%, rgba(0, 212, 255, 0.1), transparent 55%),
    radial-gradient(circle at 70% 70%, rgba(0, 255, 204, 0.08), transparent 55%),
    rgba(6, 10, 26, 0.75);
  backdrop-filter: blur(6px);
  display: grid;
  place-items: center;
  z-index: 2100;
}

.guide-modal {
  width: min(920px, 92vw);
  max-width: 960px;
  border-radius: 16px;
  padding: 20px 22px;
  position: relative;
  overflow: hidden;
  background: linear-gradient(145deg, rgba(10, 14, 35, 0.9), rgba(10, 20, 45, 0.9));
}

.guide-modal::before,
.guide-modal::after {
  content: '';
  position: absolute;
  width: 180px;
  height: 180px;
  border-radius: 50%;
  filter: blur(40px);
  opacity: 0.4;
  z-index: 0;
}

.guide-modal::before {
  top: -60px;
  left: -40px;
  background: rgba(0, 212, 255, 0.25);
}

.guide-modal::after {
  bottom: -60px;
  right: -50px;
  background: rgba(0, 255, 204, 0.2);
}

.guide-modal-header,
.guide-modal-body,
.guide-modal-footer {
  position: relative;
  z-index: 1;
}

.guide-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.guide-modal-label {
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.6);
}

.guide-modal-title {
  font-size: 20px;
  font-weight: 700;
  color: #e6f6ff;
}

.guide-close {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #e6f6ff;
  background: rgba(255, 255, 255, 0.05);
  cursor: pointer;
  transition: all 0.2s ease;
}

.guide-close:hover {
  background: rgba(0, 212, 255, 0.12);
  color: #0c1224;
}

.guide-modal-body {
  margin: 10px 0 18px;
}

.guide-modal-steps {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 12px;
}

.guide-modal-card {
  position: relative;
  padding: 14px 14px 16px 16px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.06);
  box-shadow:
    0 10px 24px rgba(0, 0, 0, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
  overflow: hidden;
}

.guide-modal-card::after {
  content: '';
  position: absolute;
  inset: 1px;
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.12), rgba(0, 255, 204, 0.08));
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 0;
}

.guide-modal-card:hover::after {
  opacity: 1;
}

.guide-modal-index {
  width: 32px;
  height: 32px;
  border-radius: 12px;
  background: linear-gradient(135deg, #00d4ff, #00ffcc);
  color: #0c1224;
  font-weight: 800;
  display: grid;
  place-items: center;
  box-shadow: 0 8px 18px rgba(0, 212, 255, 0.25);
  position: relative;
  z-index: 1;
}

.guide-modal-content {
  margin-top: 10px;
  position: relative;
  z-index: 1;
}

.guide-modal-card-title {
  font-size: 15px;
  font-weight: 700;
  color: #e6f6ff;
  margin-bottom: 6px;
}

.guide-modal-card-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.6;
}

.guide-modal-pill {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(0, 212, 255, 0.16);
  border: 1px solid rgba(0, 212, 255, 0.35);
  color: #a7f5ff;
  font-size: 12px;
  font-weight: 600;
  z-index: 1;
}

.guide-modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.guide-fade-enter-active,
.guide-fade-leave-active {
  transition: opacity 0.25s ease;
}

.guide-fade-enter-from,
.guide-fade-leave-to {
  opacity: 0;
}

@media (max-width: 720px) {
  .guide-modal {
    width: 94vw;
    padding: 16px;
  }

  .guide-modal-title {
    font-size: 18px;
  }

  .guide-modal-steps {
    grid-template-columns: 1fr;
  }

  .guide-inline-actions {
    flex-direction: column;
    align-items: stretch;
  }
}

@media (prefers-reduced-motion: reduce) {
  .guide-inline-panel,
  .guide-overlay,
  .guide-modal-card::after {
    animation: none;
    transition: none;
  }
}
</style>

