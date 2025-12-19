<template>
  <div class="music-page">
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 sm:mb-8">
      <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-4 sm:mb-0 float-animation">音乐</h1>
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
          :class="filterStatus === status.value ? '!bg-[#00d4ff] !border-[#00d4ff]' : ''"
        >
          {{ status.label }}
        </el-button>
      </div>
      <el-input
        v-model="keyword"
        placeholder="搜索专辑名、艺术家…"
        clearable
        size="small"
        class="w-full sm:w-64 mt-3 sm:mt-0"
        @keyup.enter="loadAlbums"
        @clear="loadAlbums"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 专辑卡片网格 -->
    <div
      v-loading="loading"
      class="min-h-[120px]"
    >
      <div
        v-if="albumList.length === 0 && !loading"
        class="py-10 text-center text-gray-400"
      >
        暂无记录，点击右上角「添加记录」开始你的音乐之旅。
      </div>

      <div
        v-else
        class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6"
      >
        <div
          v-for="album in albumList"
          :key="album.recordId"
          class="card-3d group cursor-pointer"
          @click="goDetail(album.albumId)"
        >
          <div class="card-3d-inner rounded-2xl overflow-hidden glass-effect border border-white/10 hover:border-[#00d4ff]/30 transition-all duration-500 glow-effect">
            <!-- 封面 -->
            <div class="relative aspect-square overflow-hidden bg-gradient-to-br from-[#00d4ff]/20 to-[#00ffcc]/10">
              <img
                v-if="album.coverUrl && !imageErrorMap[album.albumId]"
                :src="album.coverUrl"
                :alt="album.title"
                class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
                loading="lazy"
                @error="handleImageError(album.albumId)"
              />
              <div v-else class="w-full h-full flex items-center justify-center">
                <el-icon :size="48" class="text-[#c3cfe2]/40">
                  <Headset />
                </el-icon>
              </div>

              <!-- 状态标签 -->
              <div class="absolute top-3 right-3">
                <el-tag
                  :type="getStatusType(album.listenStatus)"
                  size="small"
                  effect="dark"
                  class="backdrop-blur-md"
                >
                  {{ getStatusLabel(album.listenStatus) }}
                </el-tag>
              </div>

              <!-- 评分 -->
              <div
                v-if="album.personalRating != null"
                class="absolute bottom-3 left-3 flex items-center space-x-1 bg-black/60 backdrop-blur-sm px-2 py-1 rounded-lg"
              >
                <el-icon class="text-yellow-400"><StarFilled /></el-icon>
                <span class="text-white text-sm font-semibold">{{ album.personalRating }}</span>
              </div>
            </div>

            <!-- 信息 -->
            <div class="p-4 flex flex-col gap-2">
              <h3 class="text-lg font-semibold text-white line-clamp-1">{{ album.title }}</h3>
              <p class="text-sm text-[#00d4ff]/80 line-clamp-1">{{ album.artist }}</p>
              <p class="text-xs text-gray-400">
                <span v-if="album.releaseYear">{{ album.releaseYear }}</span>
                <span v-if="album.listenDate"> · 已听于 {{ album.listenDate }}</span>
                <span v-if="album.listenCount"> · {{ album.listenCount }}次</span>
              </p>

              <!-- 标签 -->
              <div
                v-if="album.tags"
                class="flex flex-wrap gap-2 mt-1"
              >
                <el-tag
                  v-for="tag in album.tags.split(',')"
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
                  @click.stop="openEditDialog(album)"
                >
                  编辑
                </el-button>
                <el-popconfirm
                  title="确定删除该记录？"
                  confirm-button-text="删除"
                  cancel-button-text="取消"
                  confirm-button-type="danger"
                  @confirm="handleDelete(album.recordId)"
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
          @current-change="loadAlbums"
          @size-change="loadAlbums"
        />
      </div>
    </div>

    <!-- 新建 / 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '添加专辑记录' : '编辑专辑记录'"
      width="520px"
      destroy-on-close
      class="music-dialog"
    >
      <div class="music-form-wrapper glass-effect">
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="80px"
          class="music-form"
        >
          <el-form-item
            label="专辑名称"
            prop="title"
          >
            <el-input
              v-model="form.title"
              placeholder="请输入专辑名称"
              :disabled="dialogMode === 'edit'"
            />
          </el-form-item>

          <el-form-item
            label="艺术家"
            prop="artist"
          >
            <el-input
              v-model="form.artist"
              placeholder="请输入艺术家/乐队"
              :disabled="dialogMode === 'edit'"
            />
          </el-form-item>

          <el-form-item label="发行年份">
            <el-input-number
              v-model="form.releaseYear"
              :min="1900"
              :max="2100"
              placeholder="发行年份"
              class="w-full"
              :disabled="dialogMode === 'edit'"
            />
          </el-form-item>

          <el-form-item label="音乐类型">
            <el-input
              v-model="form.genre"
              placeholder="多个类型使用逗号分隔，例如：摇滚,流行"
              :disabled="dialogMode === 'edit'"
            />
          </el-form-item>

          <el-form-item label="封面">
            <div class="w-full flex items-center gap-3">
              <el-input
                v-model="form.coverUrl"
                placeholder="支持粘贴图片地址"
                :disabled="dialogMode === 'edit'"
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

          <el-form-item label="总曲目数">
            <el-input-number
              v-model="form.totalTracks"
              :min="1"
              placeholder="总曲目数"
              class="w-full"
              :disabled="dialogMode === 'edit'"
            />
          </el-form-item>

          <el-form-item label="总时长">
            <el-input-number
              v-model="form.duration"
              :min="1"
              placeholder="总时长（秒）"
              class="w-full"
              :disabled="dialogMode === 'edit'"
            />
          </el-form-item>

          <el-form-item label="简介">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="2"
              placeholder="专辑简介"
              :disabled="dialogMode === 'edit'"
            />
          </el-form-item>

          <el-form-item label="状态">
            <el-select v-model="form.listenStatus">
              <el-option :value="1" label="想听" />
              <el-option :value="2" label="在听" />
              <el-option :value="3" label="已听" />
              <el-option :value="4" label="弃听" />
            </el-select>
          </el-form-item>

          <el-form-item label="听歌日期">
            <el-date-picker
              v-model="form.listenDate"
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

          <el-form-item label="听歌次数">
            <el-input-number
              v-model="form.listenCount"
              :min="0"
              placeholder="听歌次数"
              class="w-full"
            />
          </el-form-item>

          <el-form-item label="标签">
            <el-input
              v-model="form.tags"
              placeholder="多个标签使用逗号分隔，例如：摇滚,经典"
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
import { Plus, Headset, StarFilled, Search } from '@element-plus/icons-vue'
import { getAlbumPage, createAlbum, updateAlbumRecord, deleteAlbumRecord } from '@/api/music'
import type { AlbumPageItem } from '@/types/api'

const router = useRouter()

const loading = ref(false)
const albumList = ref<AlbumPageItem[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(8)
const filterStatus = ref<number | 'all'>('all')
const keyword = ref('')

const statusOptions = [
  { label: '全部', value: 'all' as const },
  { label: '想听', value: 1 },
  { label: '在听', value: 2 },
  { label: '已听', value: 3 },
  { label: '弃听', value: 4 },
]

const imageErrorMap = reactive<Record<number, boolean>>({})

async function loadAlbums() {
  try {
    loading.value = true
    const params: any = {
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
    }
    if (filterStatus.value !== 'all') {
      params.status = filterStatus.value
    }
    const res = await getAlbumPage(params)
    albumList.value = res.data.list || []
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
  loadAlbums()
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
    1: '想听',
    2: '在听',
    3: '已听',
    4: '弃听',
  }
  return map[status] || '-'
}

function handleImageError(albumId: number) {
  imageErrorMap[albumId] = true
}

function goDetail(albumId: number) {
  if (!albumId) return
  router.push(`/music/${albumId}`)
}

// ========== 新建 / 编辑表单 ==========
type DialogMode = 'create' | 'edit'

const dialogVisible = ref(false)
const dialogMode = ref<DialogMode>('create')
const formRef = ref<FormInstance>()

const form = reactive<{
  recordId?: number
  title: string
  artist: string
  releaseYear?: number
  genre?: string
  description?: string
  coverUrl?: string
  totalTracks?: number
  duration?: number
  listenStatus?: number
  personalRating?: number
  listenDate?: string
  listenCount?: number
  tags?: string
  comment?: string
}>({
  title: '',
  artist: '',
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入专辑名称', trigger: 'blur' }],
  artist: [{ required: true, message: '请输入艺术家', trigger: 'blur' }],
}

function resetForm() {
  form.recordId = undefined
  form.title = ''
  form.artist = ''
  form.releaseYear = undefined
  form.genre = ''
  form.description = ''
  form.coverUrl = ''
  form.totalTracks = undefined
  form.duration = undefined
  form.listenStatus = 1
  form.personalRating = undefined
  form.listenDate = ''
  form.listenCount = undefined
  form.tags = ''
  form.comment = ''
}

function openCreateDialog() {
  dialogMode.value = 'create'
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(item: AlbumPageItem) {
  dialogMode.value = 'edit'
  form.recordId = item.recordId
  form.title = item.title
  form.artist = item.artist
  form.releaseYear = item.releaseYear
  form.coverUrl = item.coverUrl
  form.listenStatus = item.listenStatus
  form.personalRating = item.personalRating
  form.listenDate = item.listenDate
  form.listenCount = item.listenCount
  form.tags = item.tags
  form.comment = '' // 列表不返回 comment，编辑时可在详情页扩展
  dialogVisible.value = true
}

const submitLoading = ref(false)

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()

  try {
    submitLoading.value = true
    if (dialogMode.value === 'create') {
      await createAlbum({
        title: form.title,
        artist: form.artist,
        releaseYear: form.releaseYear,
        genre: form.genre,
        description: form.description,
        coverUrl: form.coverUrl,
        totalTracks: form.totalTracks,
        duration: form.duration,
        listenStatus: form.listenStatus,
        personalRating: form.personalRating,
        listenDate: form.listenDate,
        listenCount: form.listenCount,
        tags: form.tags,
        comment: form.comment,
      })
      ElMessage.success('创建成功')
    } else if (dialogMode.value === 'edit' && form.recordId) {
      await updateAlbumRecord(form.recordId, {
        id: form.recordId,
        listenStatus: form.listenStatus,
        personalRating: form.personalRating,
        listenDate: form.listenDate,
        listenCount: form.listenCount,
        tags: form.tags,
        comment: form.comment,
      })
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    loadAlbums()
  } catch (e) {
    // 已统一处理
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(recordId: number) {
  try {
    await deleteAlbumRecord(recordId)
    ElMessage.success('删除成功')
    if (albumList.value.length === 1 && pageNo.value > 1) {
      pageNo.value -= 1
    }
    loadAlbums()
  } catch (e) {
    // 已统一处理
  }
}

onMounted(() => {
  loadAlbums()
})
</script>

<style scoped>
.music-page {
  min-height: 100%;
}

.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 弹窗样式 */
:deep(.music-dialog) {
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

:deep(.music-dialog .el-dialog__header) {
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

:deep(.music-dialog .el-dialog__title) {
  color: #e5f5ff;
  font-weight: 600;
  letter-spacing: 0.04em;
}

:deep(.music-dialog .el-dialog__body) {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 16px;
}

:deep(.music-dialog .el-dialog__footer) {
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.music-form-wrapper {
  padding: 16px 18px 4px;
  border-radius: 14px;
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.04),
    rgba(0, 212, 255, 0.05),
    rgba(0, 0, 0, 0.3)
  );
}

.music-form {
  --el-text-color-regular: #e5e7f0;
}

:deep(.music-dialog .el-form-item__label) {
  color: #cbd5ff;
}

.cover-preview {
  width: 56px;
  height: 56px;
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

/* 移动端适配 */
@media (max-width: 600px) {
  :deep(.music-dialog) {
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

  :deep(.music-dialog .el-dialog__body) {
    padding: 12px 12px 80px;
  }

  :deep(.music-dialog .el-dialog__footer) {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 60px;
    z-index: 2000;
    background: linear-gradient(to top, rgba(10, 14, 35, 0.98), transparent);
    padding-top: 8px;
  }
}
</style>
