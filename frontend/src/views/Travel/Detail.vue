<template>
  <div class="travel-detail min-h-full">
    <el-page-header
      content="详情"
      @back="goBack"
      class="mb-4 sm:mb-6"
    />

    <div v-if="loading" class="flex items-center justify-center py-16 text-gray-400 text-sm">
      <el-icon class="mr-2">
        <Loading />
      </el-icon>
      正在加载...
    </div>

    <div v-else-if="detail" class="space-y-6">
      <!-- 主要信息卡片 -->
      <div class="glass-effect rounded-2xl border border-white/10 p-6 sm:p-8">
        <div class="grid grid-cols-1 lg:grid-cols-[260px,1fr] gap-8">
          <!-- 左侧封面 -->
          <div class="flex flex-col items-center gap-4">
            <div class="w-52 h-52 rounded-2xl overflow-hidden bg-black/40 border border-white/15 shadow-xl">
              <img
                v-if="detail.place.coverUrl"
                :src="detail.place.coverUrl"
                :alt="detail.place.name"
                class="w-full h-full object-cover"
              />
              <div
                v-else
                class="w-full h-full flex items-center justify-center text-gray-500"
              >
                <el-icon :size="48">
                  <Location />
                </el-icon>
              </div>
            </div>
            <div class="text-sm text-gray-400 space-y-1 text-center">
              <div v-if="detail.record?.travelDate">
                旅游日期：{{ detail.record.travelDate }}
              </div>
              <div v-if="detail.place.country">
                国家：{{ detail.place.country }}
              </div>
              <div v-if="detail.place.city">
                城市：{{ detail.place.city }}
              </div>
              <div v-if="detail.record?.travelDuration">
                旅游时长：{{ detail.record.travelDuration }} 天
              </div>
              <div v-if="detail.record?.expense">
                花费：¥{{ detail.record.expense }}
              </div>
              <div v-if="detail.record?.personalRating">
                评分：{{ detail.record.personalRating }}/10
              </div>
            </div>
          </div>

          <!-- 右侧信息 -->
          <div class="flex flex-col gap-4">
            <div>
              <h1 class="text-2xl sm:text-3xl font-bold text-white mb-2">
                {{ detail.place.name }}
              </h1>
              <p
                v-if="detail.place.address"
                class="text-lg text-[#00d4ff]/80 mb-2"
              >
                {{ detail.place.address }}
              </p>
              <p
                v-if="getPlaceTypeLabel(detail.place.placeType)"
                class="text-sm text-[#00d4ff]/60 mb-1"
              >
                {{ getPlaceTypeLabel(detail.place.placeType) }}
              </p>
              <p
                v-if="detail.place.description"
                class="text-sm text-gray-300 leading-relaxed mt-2"
              >
                {{ detail.place.description }}
              </p>
            </div>

            <el-divider />

            <!-- 旅游记录 -->
            <div v-if="detail.record">
              <h2 class="text-lg font-semibold text-white mb-3">
                我的记录
              </h2>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div class="space-y-2">
                  <div class="text-sm text-gray-400">状态</div>
                  <el-select
                    v-model="editForm.travelStatus"
                    size="small"
                    class="w-40"
                  >
                    <el-option :value="1" label="想去" />
                    <el-option :value="2" label="计划中" />
                    <el-option :value="3" label="已去" />
                  </el-select>
                </div>
                <div class="space-y-2">
                  <div class="text-sm text-gray-400">个人评分</div>
                  <el-input-number
                    v-model="editForm.personalRating"
                    :min="0"
                    :max="10"
                    :step="0.5"
                    size="small"
                    controls-position="right"
                    class="w-full sm:w-40"
                  />
                </div>
                <div class="space-y-2">
                  <div class="text-sm text-gray-400">旅游日期</div>
                  <el-date-picker
                    v-model="editForm.travelDate"
                    type="date"
                    placeholder="选择日期"
                    size="small"
                    value-format="YYYY-MM-DD"
                    class="w-full sm:w-40"
                  />
                </div>
                <div class="space-y-2">
                  <div class="text-sm text-gray-400">旅游时长(天)</div>
                  <el-input-number
                    v-model="editForm.travelDuration"
                    :min="0"
                    size="small"
                    controls-position="right"
                    class="w-full sm:w-40"
                  />
                </div>
                <div class="space-y-2">
                  <div class="text-sm text-gray-400">花费(元)</div>
                  <el-input-number
                    v-model="editForm.expense"
                    :min="0"
                    :precision="2"
                    size="small"
                    controls-position="right"
                    class="w-full sm:w-40"
                  />
                </div>
                <div class="space-y-2 sm:col-span-2">
                  <div class="text-sm text-gray-400">标签</div>
                  <div v-if="editForm.tags" class="flex flex-wrap gap-2">
                    <el-tag
                      v-for="tag in editForm.tags.split(',').filter(t => t.trim())"
                      :key="tag"
                      size="small"
                      type="info"
                    >
                      {{ tag.trim() }}
                    </el-tag>
                  </div>
                  <el-input
                    v-else
                    size="small"
                    placeholder="暂无标签（标签暂不支持编辑）"
                    disabled
                  />
                </div>
              </div>

              <div class="mt-4">
                <div class="text-sm text-gray-400 mb-1">评价</div>
                <el-input
                  v-model="editForm.comment"
                  type="textarea"
                  :rows="3"
                  placeholder="说点什么吧"
                />
              </div>

              <div class="mt-4 text-sm text-gray-400 space-y-1">
                <div v-if="detail.record.createTime">
                  创建时间：{{ formatDateTime(detail.record.createTime) }}
                </div>
                <div v-if="detail.record.updateTime && detail.record.updateTime !== detail.record.createTime">
                  更新时间：{{ formatDateTime(detail.record.updateTime) }}
                </div>
              </div>

              <div class="mt-4 text-right">
                <el-button
                  type="primary"
                  :loading="saving"
                  @click="saveRecord"
                >
                  保存记录
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 照片区域 -->
      <div class="glass-effect rounded-2xl border border-white/10 p-6 sm:p-8">
        <div class="flex items-center justify-between mb-4 gap-3 flex-wrap">
          <div>
            <h2 class="text-lg font-semibold text-white">旅途照片</h2>
            <p class="text-xs text-gray-400">
              仅当前旅行记录可见，摄影模块不会显示
            </p>
          </div>
          <el-upload
            :auto-upload="true"
            :on-success="handlePhotoUploadSuccess"
            :on-error="handlePhotoUploadError"
            :before-upload="beforePhotoUpload"
            :show-file-list="false"
            accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
            :http-request="handlePhotoUpload"
            :disabled="uploading || !detail?.record?.id"
          >
            <template #trigger>
              <el-button
                type="primary"
                size="small"
                :icon="Upload"
                :loading="uploading"
                :disabled="uploading || !detail?.record?.id"
              >
                上传照片
              </el-button>
            </template>
          </el-upload>
        </div>

        <div v-loading="photoLoading">
          <div
            v-if="photoList.length === 0"
            class="text-center text-gray-500 py-8 text-sm"
          >
            暂无照片，点击“上传照片”添加
          </div>

          <div v-else class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
            <div
              v-for="photo in photoList"
              :key="photo.id"
              class="relative group rounded-xl overflow-hidden border border-white/10"
            >
              <img
                :src="photo.thumbnailPath || photo.filePath"
                :alt="photo.title || 'photo'"
                class="w-full h-44 object-cover"
              />
              <div class="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-2">
                <el-button
                  type="danger"
                  size="small"
                  :icon="Delete"
                  circle
                  @click.stop="confirmDeletePhoto(photo.id)"
                />
              </div>
            </div>
          </div>

          <div class="flex justify-end mt-4" v-if="photoPage.total > photoPage.pageSize">
            <el-pagination
              background
              layout="prev, pager, next"
              :page-size="photoPage.pageSize"
              :current-page="photoPage.pageNo"
              :total="photoPage.total"
              @current-change="page => loadPhotos(page)"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Location, Loading, Upload } from '@element-plus/icons-vue'
import { uploadPhoto, deletePhoto, getPhotoPage } from '@/api/photo'
import { getTravelDetail, updateTravelRecord } from '@/api/travel'
import type { TravelDetail } from '@/types/api'
import type { PhotoPageItem, PhotoUploadResp } from '@/api/photo'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const detail = ref<TravelDetail | null>(null)
const photoLoading = ref(false)
const photoList = ref<PhotoPageItem[]>([])
const photoPage = reactive({
  pageNo: 1,
  pageSize: 12,
  total: 0,
})
const uploading = ref(false)
const MAX_PHOTO_SIZE = 10 * 1024 * 1024
const ALLOWED_PHOTO_TYPES = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']

const editForm = reactive<{
  recordId?: number
  travelStatus?: number
  personalRating?: number
  travelDate?: string
  travelDuration?: number
  expense?: number
  tags?: string
  comment?: string
}>({})

function getPlaceTypeLabel(type?: number) {
  const map: Record<number, string> = {
    1: '城市',
    2: '景点',
    3: '国家',
    4: '其他',
  }
  return type ? map[type] : ''
}

function formatDateTime(dateTime?: string) {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function goBack() {
  router.back()
}

async function fetchDetail() {
  const id = Number(route.params.id)
  if (!id) return
  try {
    loading.value = true
    const res = await getTravelDetail(id)
    detail.value = res.data
    if (res.data.record) {
      editForm.recordId = res.data.record.id
      editForm.travelStatus = res.data.record.travelStatus
      editForm.personalRating = res.data.record.personalRating
      editForm.travelDate = res.data.record.travelDate
      editForm.travelDuration = res.data.record.travelDuration
      editForm.expense = res.data.record.expense
      editForm.tags = res.data.record.tags
      editForm.comment = res.data.record.comment
      await loadPhotos(1)
    }
  } catch {
    // 统一错误处理
  } finally {
    loading.value = false
  }
}

async function saveRecord() {
  if (!editForm.recordId) return
  try {
    saving.value = true
    await updateTravelRecord(editForm.recordId, {
      id: editForm.recordId,
      travelStatus: editForm.travelStatus,
      personalRating: editForm.personalRating,
      travelDate: editForm.travelDate,
      travelDuration: editForm.travelDuration,
      expense: editForm.expense,
      comment: editForm.comment,
      // 注意：tags字段后端暂不支持更新，需要在创建时设置
    })
    ElMessage.success('已保存')
    fetchDetail()
  } catch {
    // 已统一处理
  } finally {
    saving.value = false
  }
}

async function loadPhotos(page = 1) {
  if (!detail.value?.record?.id) return
  try {
    photoLoading.value = true
    photoPage.pageNo = page
    const res = await getPhotoPage({
      pageNo: photoPage.pageNo,
      pageSize: photoPage.pageSize,
      travelRecordId: detail.value.record.id,
    })
    photoList.value = res.data.list || []
    photoPage.total = res.data.total || 0
  } catch {
    // 已统一处理
  } finally {
    photoLoading.value = false
  }
}

function beforePhotoUpload(file: File) {
  if (!ALLOWED_PHOTO_TYPES.includes(file.type)) {
    ElMessage.error('仅支持 JPG/PNG/GIF/WEBP 格式')
    return false
  }
  if (file.size > MAX_PHOTO_SIZE) {
    ElMessage.error('单张图片不能超过 10MB')
    return false
  }
  return true
}

async function handlePhotoUpload(options: any) {
  const { file } = options
  if (!detail.value?.record?.id) {
    ElMessage.warning('缺少旅行记录 ID，无法上传')
    return
  }
  
  uploading.value = true
  try {
    const res = await uploadPhoto(file, {
      travelRecordId: detail.value.record.id,
    })
    if (res.code === 0 && res.data) {
      // 立即添加到列表中显示
      const newPhoto: PhotoPageItem = {
        id: res.data.id,
        filePath: res.data.filePath,
        thumbnailPath: res.data.thumbnailPath,
        createTime: res.data.createTime,
      }
      photoList.value.unshift(newPhoto)
      photoPage.total += 1
      ElMessage.success('上传成功')
    } else {
      ElMessage.error(res.msg || '上传失败')
    }
  } catch (error: any) {
    console.error('照片上传失败:', error)
    ElMessage.error(error.message || '照片上传失败')
  } finally {
    uploading.value = false
  }
}

function handlePhotoUploadSuccess() {
  // 成功处理已在 handlePhotoUpload 中完成
}

function handlePhotoUploadError() {
  ElMessage.error('照片上传失败')
  uploading.value = false
}

function confirmDeletePhoto(photoId: number) {
  ElMessageBox.confirm('删除后不可恢复，确定删除这张照片吗？', '提示', {
    type: 'warning',
  }).then(() => deletePhotoById(photoId))
}

async function deletePhotoById(photoId: number) {
  try {
    await deletePhoto(photoId)
    ElMessage.success('已删除')
    const nextPage = photoList.value.length === 1 && photoPage.pageNo > 1
      ? photoPage.pageNo - 1
      : photoPage.pageNo
    await loadPhotos(nextPage)
  } catch {
    // 已统一处理
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.travel-detail {
  min-height: 100%;
}
</style>
