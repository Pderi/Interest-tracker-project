<template>
  <div class="concert-detail min-h-full">
    <el-page-header
      content="演唱会详情"
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
        <div class="grid grid-cols-1 lg:grid-cols-[280px,1fr] gap-8">
      <!-- 左侧海报 -->
      <div class="flex flex-col items-center gap-4">
        <div class="w-60 aspect-[2/3] rounded-2xl overflow-hidden bg-black/40 border border-white/15 shadow-xl">
          <img
            v-if="detail.concert.posterUrl"
            :src="detail.concert.posterUrl"
            :alt="detail.concert.artist"
            class="w-full h-full object-contain bg-[#0b162b]"
          />
          <div
            v-else
            class="w-full h-full flex items-center justify-center text-gray-500"
          >
            <el-icon :size="48">
              <Headset />
            </el-icon>
          </div>
        </div>
        <div class="text-sm text-gray-400 space-y-1 text-left w-full">
          <div v-if="detail.concert.concertDate">
            演出日期：{{ detail.concert.concertDate }}
          </div>
          <div v-if="detail.concert.venue">
            场馆：{{ detail.concert.venue }}
          </div>
          <div v-if="detail.concert.city">
            城市：{{ detail.concert.city }}
          </div>
          <div v-if="detail.concert.country">
            国家：{{ detail.concert.country }}
          </div>
          <div v-if="detail.concert.concertType">
            类型：{{ getConcertTypeLabel(detail.concert.concertType) }}
          </div>
          <div v-if="detail.record?.ticketPrice">
            票价：¥{{ detail.record.ticketPrice }}
          </div>
        </div>
      </div>

      <!-- 右侧信息 -->
      <div class="flex flex-col gap-4">
        <div>
          <h1 class="text-2xl sm:text-3xl font-bold text-white mb-2">
            {{ detail.concert.artist }}
          </h1>
          <p
            v-if="detail.concert.title"
            class="text-lg text-[#00d4ff]/80 mb-2"
          >
            {{ detail.concert.title }}
          </p>
          <p
            v-if="detail.concert.description"
            class="text-sm text-gray-300 leading-relaxed mt-2"
          >
            {{ detail.concert.description }}
          </p>
        </div>

        <el-divider />

        <!-- 观演记录 -->
        <div v-if="detail.record">
          <h2 class="text-lg font-semibold text-white mb-3">
            我的记录
          </h2>
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div class="space-y-2">
              <div class="text-sm text-gray-400">状态</div>
              <el-select
                v-model="editForm.watchStatus"
                size="small"
                class="w-40"
              >
                <el-option :value="1" label="想看" />
                <el-option :value="2" label="已看" />
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
              <div class="text-sm text-gray-400">观演日期</div>
              <el-date-picker
                v-model="editForm.watchDate"
                type="date"
                placeholder="选择日期"
                size="small"
                value-format="YYYY-MM-DD"
                class="w-full sm:w-40"
              />
            </div>
            <div class="space-y-2">
              <div class="text-sm text-gray-400">票价(元)</div>
              <el-input-number
                v-model="editForm.ticketPrice"
                :min="0"
                :precision="2"
                size="small"
                controls-position="right"
                class="w-full sm:w-40"
              />
            </div>
            <div class="space-y-2 sm:col-span-2">
              <div class="text-sm text-gray-400">座位信息</div>
              <el-input
                v-model="editForm.seatInfo"
                size="small"
                placeholder="如：A区1排1号"
              />
            </div>
            <div class="space-y-2 sm:col-span-2">
              <div class="text-sm text-gray-400">标签</div>
              <el-input
                v-model="editForm.tags"
                size="small"
                placeholder="创建时填写的标签（暂不支持修改）"
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
            <h2 class="text-lg font-semibold text-white">观演照片</h2>
            <p class="text-xs text-gray-400">
              仅当前演唱会记录可见，摄影模块不会显示
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
            暂无照片，点击"上传照片"添加
          </div>

          <div v-else class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
            <div
              v-for="photo in photoList"
              :key="photo.id"
              class="relative group rounded-xl overflow-hidden border border-white/10"
            >
              <img
                :src="photo.thumbnailPath || photo.filePath"
                :alt="photo.title || detail.concert.artist"
                class="w-full h-44 object-contain bg-black/20"
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
import { Headset, Loading, Picture, Upload, Delete } from '@element-plus/icons-vue'
import { getConcertDetail, updateConcertRecord } from '@/api/concert'
import { uploadPhoto, deletePhoto, getPhotoPage } from '@/api/photo'
import type { ConcertDetail } from '@/types/api'
import type { PhotoPageItem } from '@/api/photo'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const detail = ref<ConcertDetail | null>(null)
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
  watchStatus?: number
  personalRating?: number
  watchDate?: string
  ticketPrice?: number
  seatInfo?: string
  tags?: string
  comment?: string
}>({})

function getConcertTypeLabel(type?: number) {
  const map: Record<number, string> = {
    1: '演唱会',
    2: '音乐节',
    3: '演出',
    4: '其他',
  }
  return type ? map[type] : ''
}

function goBack() {
  router.back()
}

async function fetchDetail() {
  const id = Number(route.params.id)
  if (!id) return
  try {
    loading.value = true
    const res = await getConcertDetail(id)
    detail.value = res.data
    if (res.data.record) {
      editForm.recordId = res.data.record.id
      editForm.watchStatus = res.data.record.watchStatus
      editForm.personalRating = res.data.record.personalRating
      editForm.watchDate = res.data.record.watchDate
      editForm.ticketPrice = res.data.record.ticketPrice
      editForm.seatInfo = res.data.record.seatInfo
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

async function loadPhotos(page = 1) {
  if (!detail.value?.record?.id) return
  try {
    photoLoading.value = true
    photoPage.pageNo = page
    const res = await getPhotoPage({
      pageNo: photoPage.pageNo,
      pageSize: photoPage.pageSize,
      concertRecordId: detail.value.record.id,
    })
    photoList.value = res.data.list || []
    photoPage.total = res.data.total || 0
  } catch {
    // 统一错误处理
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
    ElMessage.warning('缺少演唱会记录 ID，无法上传')
    return
  }
  
  uploading.value = true
  try {
    const res = await uploadPhoto(file, {
      concertRecordId: detail.value.record.id,
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

async function saveRecord() {
  if (!editForm.recordId) return
  try {
    saving.value = true
    await updateConcertRecord(editForm.recordId, {
      id: editForm.recordId,
      watchStatus: editForm.watchStatus,
      personalRating: editForm.personalRating,
      watchDate: editForm.watchDate,
      ticketPrice: editForm.ticketPrice,
      seatInfo: editForm.seatInfo,
      comment: editForm.comment,
      // tags 暂不支持更新
    })
    ElMessage.success('已保存')
    await fetchDetail()
  } catch (error: any) {
    console.error(error)
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.concert-detail {
  padding: 20px;
}
</style>

