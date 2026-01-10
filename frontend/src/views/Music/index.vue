<template>
  <div class="music-page">
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 sm:mb-8">
      <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-4 sm:mb-0 float-animation">音乐</h1>
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
          placeholder="搜索专辑名、艺术家…"
          @enter="handleSearch"
          @clear="handleSearch"
        />
      </div>
    </div>

    <!-- 专辑卡片网格 -->
    <div class="min-h-[120px] relative">
      <!-- 骨架屏加载 -->
      <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6">
        <SkeletonCard v-for="i in pageSize" :key="`skeleton-${i}`" />
      </div>
      
      <EmptyState
        v-else-if="albumList.length === 0"
        title="暂无记录"
        description="点击右上角「添加记录」开始你的音乐之旅"
      >
        <template #icon>
          <el-icon :size="64" class="text-gray-500">
            <Headset />
          </el-icon>
        </template>
        <template #action>
          <AnimatedButton variant="primary" @click="openCreateDialog">
            <el-icon><Plus /></el-icon>
            添加第一条记录
          </AnimatedButton>
        </template>
      </EmptyState>

      <div
        v-else
        class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6"
      >
        <template
          v-for="(album, index) in albumList"
          :key="album.recordId"
        >
          <div class="flex flex-col gap-2 music-card-wrapper" :style="{ animationDelay: `${index * 50}ms` }">
            <!-- 音乐卡片 -->
            <AnimatedCard
              variant="3d"
              class="group cursor-pointer"
              @click="goDetail(album.albumId)"
            >
              <div class="rounded-2xl overflow-hidden">
                <!-- 封面 -->
                <div class="relative aspect-square overflow-hidden music-poster-container">
                  <div class="poster-overlay"></div>
                  <img
                    v-if="album.coverUrl && !imageErrorMap[album.albumId]"
                    :src="album.coverUrl"
                    :alt="album.title"
                    class="music-poster w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
                    loading="lazy"
                    @error="handleImageError(album.albumId)"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center bg-gradient-to-br from-[#00d4ff]/20 to-[#00ffcc]/10 poster-placeholder">
                    <el-icon :size="48" class="text-[#c3cfe2]/40">
                      <Headset />
                    </el-icon>
                  </div>

                  <!-- 状态标签 -->
                  <div class="absolute top-3 right-3 status-badge-wrapper">
                    <div class="status-badge" :class="`status-badge-${album.listenStatus}`">
                      <div class="status-badge-glow"></div>
                      <div class="status-badge-ripple"></div>
                      <el-tag
                        :type="getStatusType(album.listenStatus)"
                        size="small"
                        effect="dark"
                        class="backdrop-blur-md status-tag"
                        @click.stop="handleStatusClick(album)"
                        @mouseenter="handleStatusHover"
                      >
                        <span class="status-text">{{ getStatusLabel(album.listenStatus) }}</span>
                      </el-tag>
                    </div>
                  </div>

                  <!-- 评分 -->
                  <div
                    v-if="album.personalRating != null"
                    class="rating-badge absolute bottom-3 left-3 flex items-center space-x-1 bg-black/60 backdrop-blur-sm px-2 py-1 rounded-lg"
                  >
                    <el-icon class="text-yellow-400 rating-star"><StarFilled /></el-icon>
                    <span class="text-white text-sm font-semibold rating-value">{{ album.personalRating }}</span>
                  </div>
                </div>

                <!-- 信息 -->
                <div class="p-4 flex flex-col gap-2 glass-effect">
                  <h3 class="text-lg font-semibold text-white line-clamp-1">{{ album.title }}</h3>
                  <p class="text-sm text-[#00d4ff]/80 line-clamp-1">{{ album.artist }}</p>
                  <p class="text-xs text-gray-400">
                    <span v-if="album.releaseYear">{{ album.releaseYear }}</span>
                    <span v-if="album.listenDate"> · 已听于 {{ album.listenDate }}</span>
                    <span v-if="album.listenCount"> · {{ album.listenCount }}次</span>
                  </p>

                  <!-- 音乐类型 -->
                  <div
                    v-if="album.genre"
                    class="flex flex-wrap gap-2 mt-1 music-tags"
                  >
                    <AnimatedTag
                      v-for="(genre, tagIndex) in album.genre.split(',')"
                      :key="genre"
                      variant="glow"
                      :animated="tagIndex % 2 === 0"
                      class="music-tag-item"
                    >
                      {{ genre }}
                    </AnimatedTag>
                  </div>

                  <div class="mt-2 flex justify-between items-center music-actions">
                    <AnimatedButton
                      variant="outline"
                      size="small"
                      @click.stop="openEditDialog(album)"
                      class="action-btn edit-btn"
                    >
                      编辑
                    </AnimatedButton>
                    <el-popconfirm
                      title="确定删除该记录？"
                      confirm-button-text="删除"
                      cancel-button-text="取消"
                      confirm-button-type="danger"
                      @confirm="handleDelete(album.recordId)"
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
              v-if="album.comment"
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
                      <span class="text-[#00d4ff]/60">"</span>{{ album.comment }}<span class="text-[#00d4ff]/60">"</span>
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
          @current-change="loadAlbums"
          @size-change="loadAlbums"
          class="animated-pagination"
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
            />
          </el-form-item>

          <el-form-item
            label="艺术家"
            prop="artist"
          >
            <el-input
              v-model="form.artist"
              placeholder="请输入艺术家/乐队"
            />
          </el-form-item>

          <el-form-item label="发行年份">
            <el-input-number
              v-model="form.releaseYear"
              :min="1900"
              :max="2100"
              placeholder="发行年份"
              class="w-full"
            />
          </el-form-item>

          <el-form-item label="音乐类型">
            <el-input
              v-model="form.genre"
              placeholder="多个类型使用逗号分隔，例如：摇滚,流行"
            />
          </el-form-item>

          <el-form-item label="封面">
            <div class="w-full flex items-center gap-3">
              <el-input
                v-model="form.coverUrl"
                placeholder="支持粘贴图片地址"
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
            />
          </el-form-item>

          <el-form-item label="简介">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="2"
              placeholder="专辑简介"
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
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Headset, StarFilled, Search, ChatLineRound } from '@element-plus/icons-vue'
import { getAlbumPage, createAlbum, getAlbumDetail, updateAlbum, updateAlbumRecord, deleteAlbumRecord } from '@/api/music'
import type { AlbumPageItem } from '@/types/api'
import { 
  AnimatedButton, 
  AnimatedCard, 
  EmptyState,
  SkeletonCard,
  AnimatedSearch,
  AnimatedTag
} from '@/components/uiverse'

const router = useRouter()

const loading = ref(false)
const albumList = ref<AlbumPageItem[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(8)
const filterStatus = ref<number | 'all'>('all')
const keyword = ref('')
const statusCounts = ref<Record<number, number>>({})

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
    albumList.value = res.data.page?.list || []
    total.value = res.data.page?.total || 0
    statusCounts.value = res.data.statusCounts || {}
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

function handleSearch() {
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

function handleStatusClick(album: AlbumPageItem) {
  changeStatus(album.listenStatus)
  window.scrollTo({ top: 0, behavior: 'smooth' })
  ElMessage.success(`已筛选：${getStatusLabel(album.listenStatus)}`)
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
  albumId?: number
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
  form.albumId = undefined
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
  form.comment = ''
}

function openCreateDialog() {
  dialogMode.value = 'create'
  resetForm()
  dialogVisible.value = true
}

async function openEditDialog(item: AlbumPageItem) {
  dialogMode.value = 'edit'
  form.recordId = item.recordId
  form.albumId = item.albumId
  
  // 先设置列表返回的基本信息
  form.title = item.title
  form.artist = item.artist
  form.releaseYear = item.releaseYear
  form.coverUrl = item.coverUrl
  form.listenStatus = item.listenStatus
  form.personalRating = item.personalRating
  form.listenDate = item.listenDate
  form.listenCount = item.listenCount
  form.comment = item.comment || ''
  
  // 获取详情以填充 genre、description、totalTracks 等字段
  try {
    const res = await getAlbumDetail(item.albumId)
    if (res.data?.album) {
      form.genre = res.data.album.genre || ''
      form.description = res.data.album.description || ''
      form.totalTracks = res.data.album.totalTracks
      form.duration = res.data.album.duration
    }
    if (res.data?.record) {
      form.comment = res.data.record.comment || ''
    }
  } catch (e) {
    // 如果获取详情失败，至少保证基本字段已设置
    form.genre = ''
    form.description = ''
  }
  
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
        comment: form.comment,
      })
      ElMessage.success('创建成功')
    } else if (dialogMode.value === 'edit' && form.recordId && form.albumId) {
      // 编辑时同时更新专辑信息和记录信息
      await Promise.all([
        // 更新专辑信息
        updateAlbum(form.albumId, {
          title: form.title,
          artist: form.artist,
          releaseYear: form.releaseYear,
          genre: form.genre,
          description: form.description,
          coverUrl: form.coverUrl,
          totalTracks: form.totalTracks,
          duration: form.duration,
        }),
        // 更新听歌记录
        updateAlbumRecord(form.recordId, {
          id: form.recordId,
          listenStatus: form.listenStatus,
          personalRating: form.personalRating,
          listenDate: form.listenDate,
          listenCount: form.listenCount,
          comment: form.comment,
        }),
      ])
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

/* 音乐卡片进入动画 */
.music-card-wrapper {
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
.music-poster-container {
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

.music-poster {
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
.music-tags {
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

.music-tag-item {
  animation: tagPop 0.3s ease-out both;
}

.music-tag-item:nth-child(1) { animation-delay: 0.1s; }
.music-tag-item:nth-child(2) { animation-delay: 0.2s; }
.music-tag-item:nth-child(3) { animation-delay: 0.3s; }
.music-tag-item:nth-child(4) { animation-delay: 0.4s; }

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
.music-actions {
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
  animation: dialogSlideIn 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes dialogSlideIn {
  from {
    opacity: 0;
    transform: translate(-50%, -60%) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
  }
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
