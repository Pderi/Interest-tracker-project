<template>
  <div class="travel-page">
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 sm:mb-8">
      <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-4 sm:mb-0 float-animation">旅游</h1>
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
          placeholder="搜索地点名称…"
          @enter="handleSearch"
          @clear="handleSearch"
        />
      </div>
    </div>

    <!-- 旅游卡片网格 -->
    <div class="min-h-[120px] relative">
      <!-- 骨架屏加载 -->
      <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6">
        <SkeletonCard v-for="i in pageSize" :key="`skeleton-${i}`" />
      </div>
      
      <EmptyState
        v-else-if="travelList.length === 0"
        title="暂无记录"
        description="点击右上角「添加记录」开始你的旅行之旅"
      >
        <template #icon>
          <el-icon :size="64" class="text-gray-500">
            <Location />
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
          v-for="(travel, index) in travelList"
          :key="travel.recordId"
        >
          <div class="flex flex-col gap-2 travel-card-wrapper" :style="{ animationDelay: `${index * 50}ms` }">
            <!-- 旅游卡片 -->
            <AnimatedCard
              variant="3d"
              class="group cursor-pointer"
              @click="(e) => handleCardClick(e, travel)"
            >
              <div class="rounded-2xl overflow-hidden">
                <!-- 图片 -->
                <div class="relative aspect-[4/3] overflow-hidden travel-poster-container">
                  <div class="poster-overlay"></div>
                  <img
                    v-if="travel.coverUrl && !imageErrorMap[travel.placeId]"
                    :src="travel.coverUrl"
                    :alt="travel.name"
                    class="travel-poster w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
                    loading="lazy"
                    @error="handleImageError(travel.placeId)"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center bg-gradient-to-br from-[#00d4ff]/20 to-[#00ffcc]/10 poster-placeholder">
                    <el-icon :size="48" class="text-[#c3cfe2]/40">
                      <Location />
                    </el-icon>
                  </div>

                  <!-- 评分 -->
                  <div
                    v-if="travel.personalRating != null"
                    class="rating-badge absolute bottom-3 left-3 flex items-center space-x-1 bg-black/60 backdrop-blur-sm px-2 py-1 rounded-lg"
                  >
                    <el-icon class="text-yellow-400 rating-star"><StarFilled /></el-icon>
                    <span class="text-white text-sm font-semibold rating-value">{{ travel.personalRating }}</span>
                  </div>
                </div>

                <!-- 信息 -->
                <div class="p-4 flex flex-col gap-2 glass-effect">
                  <h3 class="text-lg font-semibold text-white line-clamp-1">{{ travel.name }}</h3>
                  <p class="text-xs text-gray-400">
                    <span v-if="travel.country">{{ travel.country }}</span>
                    <span v-if="travel.city"> · {{ travel.city }}</span>
                    <span v-if="travel.travelDate"> · {{ formatDate(travel.travelDate) }}</span>
                  </p>

                  <!-- 状态标签 -->
                  <div class="flex flex-wrap gap-2 mt-1">
                    <AnimatedTag variant="glow" class="travel-tag-item">
                      {{ getStatusLabel(travel.travelStatus) }}
                    </AnimatedTag>
                  </div>

                  <div class="mt-2 flex justify-between items-center travel-actions">
                    <AnimatedButton
                      variant="outline"
                      size="small"
                      @click.stop="openEditDialog(travel)"
                      class="action-btn edit-btn"
                    >
                      编辑
                    </AnimatedButton>
                    <div class="flex gap-2">
                      <AnimatedButton
                        variant="outline"
                        size="small"
                        @click.stop="openPhotoUploadDialog(travel.recordId)"
                        class="action-btn photo-btn"
                      >
                        <el-icon><Picture /></el-icon>
                        照片
                      </AnimatedButton>
                      <el-popconfirm
                        title="确定删除该记录？"
                        confirm-button-text="删除"
                        cancel-button-text="取消"
                        confirm-button-type="danger"
                        @confirm="handleDelete(travel.recordId)"
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
              </div>
            </AnimatedCard>

            <!-- 评价卡片 -->
            <AnimatedCard
              v-if="travel.comment"
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
                      <span class="text-[#00d4ff]/60">"</span>{{ travel.comment }}<span class="text-[#00d4ff]/60">"</span>
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
          @current-change="loadTravels"
          @size-change="loadTravels"
          class="animated-pagination"
        />
      </div>
    </div>

    <!-- 新建 / 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '添加旅游记录' : '编辑旅游记录'"
      width="600px"
      destroy-on-close
      class="travel-dialog"
    >
      <div class="travel-form-wrapper glass-effect">
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
          class="travel-form"
        >
          <el-form-item
            label="地点名称"
            prop="name"
          >
            <el-input
              v-model="form.name"
              placeholder="请输入地点名称"
            />
          </el-form-item>

          <el-form-item label="国家">
            <el-input
              v-model="form.country"
              placeholder="请输入国家"
            />
          </el-form-item>

          <el-form-item label="城市">
            <el-input
              v-model="form.city"
              placeholder="请输入城市"
            />
          </el-form-item>

          <el-form-item label="详细地址">
            <el-input
              v-model="form.address"
              placeholder="请输入详细地址"
            />
          </el-form-item>

          <el-form-item label="地点类型">
            <el-select v-model="form.placeType" placeholder="请选择地点类型" class="w-full">
              <el-option :value="1" label="城市" />
              <el-option :value="2" label="景点" />
              <el-option :value="3" label="国家" />
              <el-option :value="4" label="其他" />
            </el-select>
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
                    <el-icon><Upload /></el-icon>
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

          <el-form-item label="简介">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="2"
              placeholder="地点简介"
            />
          </el-form-item>

          <el-form-item label="状态">
            <el-select v-model="form.travelStatus" placeholder="请选择状态" class="w-full">
              <el-option :value="1" label="想去" />
              <el-option :value="2" label="计划中" />
              <el-option :value="3" label="已去" />
            </el-select>
          </el-form-item>

          <el-form-item label="旅游日期">
            <el-date-picker
              v-model="form.travelDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              class="w-full"
            />
          </el-form-item>

          <el-form-item label="旅游时长(天)">
            <el-input-number
              v-model="form.travelDuration"
              :min="0"
              placeholder="旅游时长"
              class="w-full"
            />
          </el-form-item>

          <el-form-item label="花费(元)">
            <el-input-number
              v-model="form.expense"
              :min="0"
              :precision="2"
              placeholder="花费"
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
              placeholder="多个标签使用逗号分隔"
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

    <!-- 照片上传对话框 -->
    <el-dialog
      v-model="photoUploadDialogVisible"
      title="上传照片"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="space-y-4">
        <el-upload
          v-model:file-list="uploadFileList"
          :auto-upload="false"
          :multiple="true"
          :limit="10"
          :on-exceed="() => ElMessage.warning('最多只能上传10张照片')"
          drag
          accept="image/*"
          class="w-full"
        >
          <el-icon class="el-icon--upload"><Upload /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持 JPG、PNG 格式，单张不超过 10MB
            </div>
          </template>
        </el-upload>

        <div
          v-if="uploadFileList.length > 0"
          class="grid grid-cols-4 gap-2 mt-4"
        >
          <div
            v-for="(uploadFile, index) in uploadFileList"
            :key="index"
            class="relative aspect-square rounded-lg overflow-hidden group"
          >
            <img
              :src="uploadFile.url || URL.createObjectURL(uploadFile.raw || uploadFile)"
              :alt="uploadFile.name"
              class="w-full h-full object-cover"
            />
            <div class="absolute inset-0 bg-black/60 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
              <el-button
                type="danger"
                size="small"
                :icon="Delete"
                circle
                @click="removeFile(index)"
              />
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="photoUploadDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="uploading"
          @click="handlePhotoUpload"
        >
          上传
        </el-button>
      </template>
    </el-dialog>

    <!-- 全部照片对话框 -->
    <el-dialog
      v-model="allPhotosDialogVisible"
      :title="`${allPhotosTravel?.name || ''} - 全部照片 (${allPhotosList.length})`"
      width="90%"
      :close-on-click-modal="true"
      class="all-photos-dialog"
    >
      <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4 max-h-[70vh] overflow-y-auto">
        <div
          v-for="photo in allPhotosList"
          :key="photo.id"
          class="relative aspect-square rounded-lg overflow-hidden cursor-pointer group"
          @click="previewPhotoFromAll(photo)"
        >
          <img
            :src="photo.thumbnailPath || photo.filePath"
            :alt="photo.title"
            class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110"
          />
          <div class="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity">
            <div class="absolute bottom-2 left-2 right-2">
              <p class="text-white text-xs truncate">{{ photo.title }}</p>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end">
          <el-button @click="allPhotosDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 照片预览对话框 -->
    <el-dialog
      v-model="photoPreviewVisible"
      title="照片预览"
      width="90%"
      :close-on-click-modal="true"
      class="photo-preview-dialog"
    >
      <div class="relative w-full" style="min-height: 60vh;">
        <el-image
          :src="previewPhotoList[previewIndex]"
          fit="contain"
          class="w-full"
          style="max-height: 70vh;"
          :preview-src-list="previewPhotoList"
          :initial-index="previewIndex"
          :preview-teleported="true"
        />
      </div>
      <template #footer>
        <div class="flex items-center justify-between">
          <div class="text-sm text-gray-400">
            {{ previewIndex + 1 }} / {{ previewPhotoList.length }}
          </div>
          <div class="flex gap-2">
            <el-button
              :disabled="previewIndex === 0"
              @click="previewIndex = Math.max(0, previewIndex - 1)"
            >
              上一张
            </el-button>
            <el-button
              :disabled="previewIndex === previewPhotoList.length - 1"
              @click="previewIndex = Math.min(previewPhotoList.length - 1, previewIndex + 1)"
            >
              下一张
            </el-button>
            <el-button @click="photoPreviewVisible = false">关闭</el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Location, StarFilled, Search, ChatLineRound, Picture, Upload, Delete, UploadFilled } from '@element-plus/icons-vue'
import { getTravelPage, createTravel, getTravelDetail, deleteTravelRecord, updateTravelRecord, updateTravelPlace } from '@/api/travel'
import { batchUploadPhotos, getPhotoPage, deletePhoto, uploadCoverImage } from '@/api/photo'
import type { Photo } from '@/api/photo'
import type { TravelPageItem } from '@/types/api'
import AnimatedButton from '@/components/uiverse/AnimatedButton.vue'
import AnimatedCard from '@/components/uiverse/AnimatedCard.vue'
import EmptyState from '@/components/uiverse/EmptyState.vue'
import SkeletonCard from '@/components/uiverse/SkeletonCard.vue'
import AnimatedSearch from '@/components/uiverse/AnimatedSearch.vue'
import AnimatedTag from '@/components/uiverse/AnimatedTag.vue'

const router = useRouter()

const loading = ref(false)
const travelList = ref<TravelPageItem[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(8)
const filterStatus = ref<number | 'all'>('all')
const keyword = ref('')
const statusCounts = ref<Record<number, number>>({})

const statusOptions = [
  { label: '全部', value: 'all' as const },
  { label: '想去', value: 1 },
  { label: '计划中', value: 2 },
  { label: '已去', value: 3 },
]

const imageErrorMap = reactive<Record<number, boolean>>({})

async function loadTravels() {
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
    const res = await getTravelPage(params)
    travelList.value = res.data.page?.list || []
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
  loadTravels()
}

function getStatusLabel(status: number) {
  const map: Record<number, string> = {
    1: '想去',
    2: '计划中',
    3: '已去',
  }
  return map[status] || '-'
}

function formatDate(dateStr?: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

function handleSearch() {
  pageNo.value = 1
  loadTravels()
}

function handleImageError(travelId: number) {
  imageErrorMap[travelId] = true
}

function goDetail(placeId?: number) {
  if (!placeId) {
    ElMessage.info('请选择有效的地点')
    return
  }
  router.push(`/travel/${placeId}`)
}

function handleCardClick(event: MouseEvent, travel: TravelPageItem) {
  // 如果点击的是按钮或链接，不触发卡片点击
  const target = event.target as HTMLElement
  if (target.closest('button') || target.closest('a') || target.closest('.action-btn') || target.closest('.el-popconfirm')) {
    return
  }
  goDetail(travel.placeId)
}

// ========== 新建 / 编辑表单 ==========
type DialogMode = 'create' | 'edit'

const dialogVisible = ref(false)
const dialogMode = ref<DialogMode>('create')
const formRef = ref<FormInstance>()

const form = reactive<{
  recordId?: number
  placeId?: number
  name: string
  country?: string
  city?: string
  address?: string
  latitude?: number
  longitude?: number
  placeType?: number
  description?: string
  coverUrl?: string
  travelStatus?: number
  personalRating?: number
  travelDate?: string
  travelDuration?: number
  expense?: number
  tags?: string
  comment?: string
}>({
  name: '',
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入地点名称', trigger: 'blur' }],
}

function resetForm() {
  form.recordId = undefined
  form.placeId = undefined
  form.name = ''
  form.country = ''
  form.city = ''
  form.address = ''
  form.latitude = undefined
  form.longitude = undefined
  form.placeType = undefined
  form.description = ''
  form.coverUrl = ''
  form.travelStatus = 1
  form.personalRating = undefined
  form.travelDate = ''
  form.travelDuration = undefined
  form.expense = undefined
  form.tags = ''
  form.comment = ''
}

function openCreateDialog() {
  dialogMode.value = 'create'
  resetForm()
  dialogVisible.value = true
}

async function openEditDialog(item: TravelPageItem) {
  dialogMode.value = 'edit'
  form.recordId = item.recordId
  form.placeId = item.placeId
  
  // 先设置列表返回的基本信息
  form.name = item.name
  form.country = item.country
  form.city = item.city
  form.coverUrl = item.coverUrl
  form.travelStatus = item.travelStatus
  form.personalRating = item.personalRating
  form.travelDate = item.travelDate
  form.travelDuration = item.travelDuration
  form.expense = item.expense
  form.comment = item.comment || ''
  
  // 获取详情以填充 address、description、placeType 等字段
  try {
    const res = await getTravelDetail(item.placeId)
    if (res.data?.place) {
      form.address = res.data.place.address || ''
      form.description = res.data.place.description || ''
      form.placeType = res.data.place.placeType
      form.latitude = res.data.place.latitude
      form.longitude = res.data.place.longitude
    }
    if (res.data?.record) {
      form.tags = res.data.record.tags || ''
      form.comment = res.data.record.comment || ''
    }
  } catch (e) {
    // 如果获取详情失败，至少保证基本字段已设置
    form.address = ''
    form.description = ''
  }
  
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
      await createTravel({
        name: form.name,
        country: form.country,
        city: form.city,
        address: form.address,
        latitude: form.latitude,
        longitude: form.longitude,
        placeType: form.placeType,
        description: form.description,
        coverUrl: form.coverUrl,
        travelStatus: form.travelStatus,
        personalRating: form.personalRating,
        travelDate: form.travelDate,
        travelDuration: form.travelDuration,
        expense: form.expense,
        tags: form.tags,
        comment: form.comment,
      })
      ElMessage.success('创建成功')
    } else if (dialogMode.value === 'edit' && form.recordId && form.placeId) {
      // 编辑时同时更新地点信息和记录信息
      await Promise.all([
        // 更新地点信息
        updateTravelPlace(form.placeId, {
          name: form.name,
          country: form.country,
          city: form.city,
          address: form.address,
          latitude: form.latitude,
          longitude: form.longitude,
          placeType: form.placeType,
          description: form.description,
          coverUrl: form.coverUrl,
        }),
        // 更新旅游记录
        updateTravelRecord(form.recordId, {
          id: form.recordId,
          travelStatus: form.travelStatus,
          personalRating: form.personalRating,
          travelDate: form.travelDate,
          travelDuration: form.travelDuration,
          expense: form.expense,
          comment: form.comment,
        }),
      ])
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    loadTravels()
  } catch (e) {
    // 已统一处理
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(recordId: number) {
  try {
    await deleteTravelRecord(recordId)
    ElMessage.success('删除成功')
    loadTravels()
  } catch (e) {
    // 错误在 request 拦截器中已统一提示
  }
}

// 照片相关功能
const photoUploadDialogVisible = ref(false)
const currentTravelId = ref<number | null>(null)
const uploadFileList = ref<any[]>([])
const uploading = ref(false)

function openPhotoUploadDialog(travelId: number) {
  currentTravelId.value = travelId
  photoUploadDialogVisible.value = true
  uploadFileList.value = []
}

function removeFile(index: number) {
  uploadFileList.value.splice(index, 1)
}

async function handlePhotoUpload() {
  if (!currentTravelId.value || uploadFileList.value.length === 0) {
    ElMessage.warning('请选择要上传的照片')
    return
  }

  try {
    uploading.value = true
    const files = uploadFileList.value.map(uploadFile => uploadFile.raw || uploadFile).filter(Boolean) as File[]
    await batchUploadPhotos(files, {
      travelRecordId: currentTravelId.value,
    })

    ElMessage.success('照片上传成功')
    photoUploadDialogVisible.value = false
    uploadFileList.value = []
    loadTravels()
  } catch (error) {
    ElMessage.error('照片上传失败')
  } finally {
    uploading.value = false
  }
}

// 照片预览功能
const photoPreviewVisible = ref(false)
const previewPhotoList = ref<string[]>([])
const previewIndex = ref(0)

// 全部照片对话框
const allPhotosDialogVisible = ref(false)
const allPhotosList = ref<Photo[]>([])
const allPhotosTravel = ref<any>(null)

function previewPhoto(photo: Photo) {
  // TODO: 照片预览功能需要从API获取照片列表
  // 目前TravelPageItem中没有photos字段，需要单独调用API获取
  previewPhotoList.value = [photo.filePath]
  previewIndex.value = 0
  photoPreviewVisible.value = true
}

function openAllPhotosDialog(travel: TravelPageItem) {
  allPhotosTravel.value = travel
  // 注意：TravelPageItem中没有photos字段，需要从API获取
  allPhotosList.value = []
  allPhotosDialogVisible.value = true
  // TODO: 调用API获取该旅游记录关联的照片
  // getPhotoList({ travelRecordId: travel.recordId }).then(res => {
  //   allPhotosList.value = res.data.list || []
  // })
}

function previewPhotoFromAll(photo: Photo) {
  const currentIndex = allPhotosList.value.findIndex(p => p.id === photo.id)
  if (currentIndex === -1) return
  previewPhotoList.value = allPhotosList.value.map(p => p.filePath)
  previewIndex.value = currentIndex
  photoPreviewVisible.value = true
}

async function handleDeletePhoto(travelId: number, photoId: number) {
  try {
    await ElMessageBox.confirm('确定删除这张照片吗？', '提示', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
    })
    await deletePhoto(photoId)
        ElMessage.success('删除成功')
    loadTravels()
  } catch (e) {
    // 用户取消或删除失败
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

/* 旅游卡片进入动画 */
.travel-card-wrapper {
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

/* 标签动画 */
.travel-tags {
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

.travel-tag-item {
  animation: tagPop 0.3s ease-out both;
}

.travel-tag-item:nth-child(1) { animation-delay: 0.1s; }
.travel-tag-item:nth-child(2) { animation-delay: 0.2s; }
.travel-tag-item:nth-child(3) { animation-delay: 0.3s; }

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
.travel-actions {
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

/* 海报容器动画 */
.travel-poster-container {
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

.travel-poster {
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

/* 照片画廊卡片动画 */
.photo-gallery-card {
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

/* 评价卡片动画 */
.comment-card {
  animation: slideInRight 0.4s ease-out;
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

