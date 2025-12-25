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
          :key="book.recordId"
        >
          <div class="flex flex-col gap-2">
            <!-- 书籍卡片 -->
            <div
              class="card-3d group cursor-pointer"
              @click="goDetail(book.bookId)"
            >
              <div class="card-3d-inner rounded-2xl overflow-hidden border border-white/10 hover:border-[#00d4ff]/30 transition-all duration-500 glow-effect bg-transparent">
                <!-- 封面 -->
                <div class="relative aspect-[3/4] overflow-hidden">
                  <img
                    v-if="book.coverUrl && !imageErrorMap[book.bookId]"
                    :src="book.coverUrl"
                    :alt="book.title"
                    class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
                    loading="lazy"
                    @error="handleImageError(book.bookId)"
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
                    <span v-if="book.readDate">已读于 {{ book.readDate }}</span>
                  </p>

                  <!-- 图书类型 -->
                  <div
                    v-if="book.genre"
                    class="flex flex-wrap gap-2 mt-1"
                  >
                    <el-tag
                      v-for="genre in book.genre.split(',')"
                      :key="genre"
                      size="small"
                      effect="plain"
                      class="!border-[#00d4ff]/30 !text-[#00d4ff] flicker"
                    >
                      {{ genre }}
                    </el-tag>
                  </div>

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
                      @click.stop="openEditDialog(book)"
                    >
                      编辑
                    </el-button>
                    <el-popconfirm
                      title="确定删除该记录？"
                      confirm-button-text="删除"
                      cancel-button-text="取消"
                      confirm-button-type="danger"
                      @confirm="handleDelete(book.recordId)"
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
            <div class="w-full flex items-center gap-3">
              <el-input
                v-model="form.coverUrl"
                placeholder="支持粘贴图片地址，后续可接入上传"
              />
              <div
                v-if="form.coverUrl"
                class="cover-preview"
              >
                <img
                  :src="form.coverUrl"
                  alt="cover preview"
                />
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
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="submitLoading"
            @click="handleSubmit"
          >
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElDialog, ElForm, ElFormItem, ElInput, ElInputNumber, ElButton, FormInstance, FormRules } from 'element-plus'
import { Plus, Document, StarFilled, Search, ChatLineRound } from '@element-plus/icons-vue'
import { getBookPage, createBook, updateBookRecord, deleteBookRecord } from '@/api/book'
import type { BookPageItem, BookCreateReq, BookRecordUpdateReq } from '@/types/api'

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
  } catch (e) {
    // 已统一处理
  } finally {
    loading.value = false
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

.cover-preview {
  width: 56px;
  height: 84px;
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
</style>

