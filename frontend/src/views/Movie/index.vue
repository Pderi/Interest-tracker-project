<template>
  <div class="movie-page">
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 sm:mb-8">
      <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-4 sm:mb-0 float-animation">影视</h1>
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
    <div class="mb-6 flex flex-wrap gap-3 items-center">
      <div class="flex flex-wrap gap-2">
        <el-button
          v-for="status in statusOptions"
          :key="status.value"
          :type="filterStatus === status.value ? 'primary' : 'default'"
          size="small"
          @click="changeStatus(status.value)"
          :class="filterStatus === status.value ? '!bg-[#ff6b6b] !border-[#ff6b6b]' : ''"
        >
          {{ status.label }}
        </el-button>
      </div>
      <el-input
        v-model="keyword"
        placeholder="搜索标题…"
        clearable
        size="small"
        class="w-full sm:w-64 mt-3 sm:mt-0"
        @keyup.enter="loadMovies"
        @clear="loadMovies"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 影视卡片网格 -->
    <div
      v-loading="loading"
      class="min-h-[120px]"
    >
      <div
        v-if="movieList.length === 0 && !loading"
        class="py-10 text-center text-gray-400"
      >
        暂无记录，点击右上角「添加记录」开始你的观影之旅。
      </div>

      <div
        v-else
        class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6"
      >
        <div
          v-for="movie in movieList"
          :key="movie.recordId"
          class="card-3d group cursor-pointer"
          @click="goDetail(movie.movieId)"
        >
          <div class="card-3d-inner rounded-2xl overflow-hidden glass-effect border border-white/10 hover:border-[#00d4ff]/30 transition-all duration-500 glow-effect">
            <!-- 封面 -->
            <div class="relative aspect-[2/3] overflow-hidden bg-gradient-to-br from-[#c3cfe2]/20 to-[#f5f7fa]/10">
              <img
                v-if="movie.posterUrl && !imageErrorMap[movie.movieId]"
                :src="movie.posterUrl"
                :alt="movie.title"
                class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
                loading="lazy"
                @error="handleImageError(movie.movieId)"
              />
              <div v-else class="w-full h-full flex items-center justify-center">
                <el-icon :size="48" class="text-[#c3cfe2]/40">
                  <VideoPlay />
                </el-icon>
              </div>

              <!-- 状态标签 -->
              <div class="absolute top-3 right-3">
                <el-tag
                  :type="getStatusType(movie.watchStatus)"
                  size="small"
                  effect="dark"
                  class="backdrop-blur-md"
                >
                  {{ getStatusLabel(movie.watchStatus) }}
                </el-tag>
              </div>

              <!-- 评分 -->
              <div
                v-if="movie.personalRating != null"
                class="absolute bottom-3 left-3 flex items-center space-x-1 bg-black/60 backdrop-blur-sm px-2 py-1 rounded-lg"
              >
                <el-icon class="text-yellow-400"><StarFilled /></el-icon>
                <span class="text-white text-sm font-semibold">{{ movie.personalRating }}</span>
              </div>
            </div>

            <!-- 信息 -->
            <div class="p-4 flex flex-col gap-2">
              <h3 class="text-lg font-semibold text-white line-clamp-1">{{ movie.title }}</h3>
              <p class="text-xs text-gray-400">
                {{ formatType(movie.type) }}
                <span v-if="movie.watchDate"> · 已看于 {{ movie.watchDate }}</span>
              </p>

              <!-- 标签 -->
              <div
                v-if="movie.tags"
                class="flex flex-wrap gap-2 mt-1"
              >
                <el-tag
                  v-for="tag in movie.tags.split(',')"
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
                  @click.stop="openEditDialog(movie)"
                >
                  编辑
                </el-button>
                <el-popconfirm
                  title="确定删除该记录？"
                  confirm-button-text="删除"
                  cancel-button-text="取消"
                  confirm-button-type="danger"
                  @confirm="handleDelete(movie.recordId)"
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
          @current-change="loadMovies"
          @size-change="loadMovies"
        />
      </div>
    </div>

    <!-- 新建 / 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '添加影视记录' : '编辑影视记录'"
      width="520px"
      destroy-on-close
      class="movie-dialog"
    >
      <div class="movie-form-wrapper glass-effect">
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="80px"
          class="movie-form"
        >
          <el-form-item
            label="标题"
            prop="title"
          >
          <el-input v-model="form.title" placeholder="请输入影视名称" />
          </el-form-item>

          <el-form-item
            label="类型"
            prop="type"
          >
          <el-select
            v-model="form.type"
            placeholder="请选择类型"
          >
            <el-option :value="1" label="电影" />
            <el-option :value="2" label="电视剧" />
          </el-select>
          </el-form-item>

          <el-form-item label="状态">
          <el-select v-model="form.watchStatus">
            <el-option :value="1" label="想看" />
            <el-option :value="2" label="在看" />
            <el-option :value="3" label="已看" />
            <el-option :value="4" label="弃剧" />
          </el-select>
          </el-form-item>

          <el-form-item label="封面">
            <div class="w-full flex items-center gap-3">
              <el-input
                v-model="form.posterUrl"
                placeholder="支持粘贴图片地址，后续可接入上传"
              />
              <div
                v-if="form.posterUrl"
                class="poster-preview"
              >
                <img
                  :src="form.posterUrl"
                  alt="poster preview"
                />
              </div>
            </div>
          </el-form-item>

          <el-form-item label="观看日期">
            <el-date-picker
              v-model="form.watchDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              class="w-full"
            />
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

          <el-form-item label="标签">
            <el-input
              v-model="form.tags"
              placeholder="多个标签使用逗号分隔，例如：科幻,经典"
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
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Plus, VideoPlay, StarFilled, Search } from '@element-plus/icons-vue'
import { getMoviePage, createMovie, updateMovieRecord, deleteMovieRecord } from '@/api/movie'
import type { MoviePageItem } from '@/types/api'

const router = useRouter()

const loading = ref(false)
const movieList = ref<MoviePageItem[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(8)
const filterStatus = ref<number | 'all'>('all')
const keyword = ref('')

const statusOptions = [
  { label: '全部', value: 'all' as const },
  { label: '想看', value: 1 },
  { label: '在看', value: 2 },
  { label: '已看', value: 3 },
  { label: '弃剧', value: 4 },
]

const imageErrorMap = reactive<Record<number, boolean>>({})

async function loadMovies() {
  try {
    loading.value = true
    const params: any = {
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
    }
    if (filterStatus.value !== 'all') {
      params.watchStatus = filterStatus.value
    }
    const res = await getMoviePage(params)
    movieList.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (e) {
    // 错误在 request 拦截器中已统一提示
  } finally {
    loading.value = false
  }
}

function changeStatus(value: number | 'all') {
  filterStatus.value = value
  pageNo.value = 1
  loadMovies()
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
    1: '想看',
    2: '在看',
    3: '已看',
    4: '弃剧',
  }
  return map[status] || '-'
}

function formatType(type: number) {
  return type === 1 ? '电影' : type === 2 ? '电视剧' : '未知'
}

function handleImageError(movieId: number) {
  imageErrorMap[movieId] = true
}

function goDetail(movieId: number) {
  if (!movieId) return
  // 直接使用路径导航，避免 name 不匹配等问题
  router.push(`/movie/${movieId}`)
}

// ========== 新建 / 编辑表单 ==========
type DialogMode = 'create' | 'edit'

const dialogVisible = ref(false)
const dialogMode = ref<DialogMode>('create')
const formRef = ref<FormInstance>()

const form = reactive<{
  recordId?: number
  title: string
  type: number | null
  posterUrl?: string
  watchStatus?: number
  personalRating?: number
  watchDate?: string
  tags?: string
  comment?: string
}>({
  title: '',
  type: null,
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
}

function resetForm() {
  form.recordId = undefined
  form.title = ''
  form.type = null
  form.posterUrl = ''
  form.watchStatus = 1
  form.personalRating = undefined
  form.watchDate = ''
  form.tags = ''
  form.comment = ''
}

function openCreateDialog() {
  dialogMode.value = 'create'
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(item: MoviePageItem) {
  dialogMode.value = 'edit'
  form.recordId = item.recordId
  form.title = item.title
  form.type = item.type
  form.posterUrl = item.posterUrl
  form.watchStatus = item.watchStatus
  form.personalRating = item.personalRating
  form.watchDate = item.watchDate
  form.tags = item.tags
  form.comment = '' // 列表不返回 comment，编辑时可在详情页扩展，这里先留空
  dialogVisible.value = true
}

const submitLoading = ref(false)

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()

  try {
    submitLoading.value = true
    if (dialogMode.value === 'create') {
      await createMovie({
        title: form.title,
        type: form.type as number,
        watchStatus: form.watchStatus,
        personalRating: form.personalRating,
        posterUrl: form.posterUrl,
        watchDate: form.watchDate,
        tags: form.tags,
        comment: form.comment,
      })
      ElMessage.success('创建成功')
    } else if (dialogMode.value === 'edit' && form.recordId) {
      await updateMovieRecord(form.recordId, {
        id: form.recordId,
        watchStatus: form.watchStatus,
        personalRating: form.personalRating,
        posterUrl: form.posterUrl,
        watchDate: form.watchDate,
        tags: form.tags,
        comment: form.comment,
      })
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    loadMovies()
  } catch (e) {
    // 已统一处理
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(recordId: number) {
  try {
    await deleteMovieRecord(recordId)
    ElMessage.success('删除成功')
    if (movieList.value.length === 1 && pageNo.value > 1) {
      pageNo.value -= 1
    }
    loadMovies()
  } catch (e) {
    // 已统一处理
  }
}

onMounted(() => {
  loadMovies()
})
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

/* 弹窗在小屏下的兼容处理与美化 */
:deep(.movie-dialog) {
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

:deep(.movie-dialog .el-dialog__header) {
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

:deep(.movie-dialog .el-dialog__title) {
  color: #e5f5ff;
  font-weight: 600;
  letter-spacing: 0.04em;
}

:deep(.movie-dialog .el-dialog__body) {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 16px;
}

:deep(.movie-dialog .el-dialog__footer) {
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.movie-form-wrapper {
  padding: 16px 18px 4px;
  border-radius: 14px;
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.04),
    rgba(0, 212, 255, 0.05),
    rgba(0, 0, 0, 0.3)
  );
}

.movie-form {
  --el-text-color-regular: #e5e7f0;
}

:deep(.movie-dialog .el-form-item__label) {
  color: #cbd5ff;
}

.poster-preview {
  width: 56px;
  height: 84px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow:
    0 8px 20px rgba(0, 0, 0, 0.8),
    0 0 15px rgba(0, 212, 255, 0.35);
}

.poster-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 更小屏幕（如 iPhone SE）下，弹窗改为“近乎全屏”以避免被底部遮挡 */
@media (max-width: 600px) {
  :deep(.movie-dialog) {
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

  :deep(.movie-dialog .el-dialog__body) {
    padding: 12px 12px 80px;
  }

  :deep(.movie-dialog .el-dialog__footer) {
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
