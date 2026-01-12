<template>
  <div class="photo-detail-page">
    <div v-if="loading" class="flex justify-center items-center min-h-[400px]">
      <LoadingSpinner />
    </div>

    <div v-else-if="photo" class="photo-detail-content">
      <!-- 返回按钮 -->
      <AnimatedButton
        variant="secondary"
        size="small"
        class="mb-4"
        @click="goBack"
      >
        <el-icon><ArrowLeft /></el-icon>
        返回
      </AnimatedButton>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- 左侧：照片展示 -->
        <div class="photo-display">
          <AnimatedCard variant="3d" class="overflow-hidden">
            <div class="relative aspect-square bg-gradient-to-br from-[#c3cfe2]/20 to-[#f5f7fa]/10">
              <img
                v-if="photo.filePath && !imageError"
                :src="photo.filePath"
                :alt="photo.title || '照片'"
                class="w-full h-full object-contain"
                @error="imageError = true"
              />
              <div v-else class="w-full h-full flex items-center justify-center">
                <el-icon :size="64" class="text-[#c3cfe2]/40">
                  <Picture />
                </el-icon>
              </div>
            </div>
          </AnimatedCard>
        </div>

        <!-- 右侧：照片信息 -->
        <div class="photo-info">
          <AnimatedCard variant="glass">
            <div class="p-6 space-y-4">
              <!-- 标题 -->
              <div>
                <h1 class="text-2xl font-bold text-white mb-2">
                  {{ photo.title || '未命名照片' }}
                </h1>
              </div>

              <!-- 描述 -->
              <div v-if="photo.description" class="bg-white/5 rounded-lg p-4 border border-white/10">
                <div class="flex items-start gap-2">
                  <el-icon class="text-[#00d4ff] mt-1"><Document /></el-icon>
                  <div class="flex-1">
                    <div class="text-gray-400 text-sm mb-1">照片描述</div>
                    <p class="text-gray-200 whitespace-pre-wrap">{{ photo.description }}</p>
                  </div>
                </div>
              </div>

              <!-- 基本信息 -->
              <div class="space-y-3">
                <div v-if="photo.category" class="flex items-center gap-2">
                  <el-icon class="text-[#00d4ff]"><Folder /></el-icon>
                  <span class="text-gray-300">分类：</span>
                  <span class="text-white">{{ photo.category }}</span>
                </div>

                <div v-if="photo.location" class="flex items-center gap-2">
                  <el-icon class="text-[#00d4ff]"><Location /></el-icon>
                  <span class="text-gray-300">地点：</span>
                  <span class="text-white">{{ photo.location }}</span>
                </div>

                <div v-if="photo.shootTime" class="flex items-center gap-2">
                  <el-icon class="text-[#00d4ff]"><Clock /></el-icon>
                  <span class="text-gray-300">拍摄时间：</span>
                  <span class="text-white">{{ formatDateTime(photo.shootTime) }}</span>
                </div>

                <div v-if="photo.width && photo.height" class="flex items-center gap-2">
                  <el-icon class="text-[#00d4ff]"><FullScreen /></el-icon>
                  <span class="text-gray-300">尺寸：</span>
                  <span class="text-white">{{ photo.width }} × {{ photo.height }} 像素</span>
                </div>

                <div v-if="photo.fileSize" class="flex items-center gap-2">
                  <el-icon class="text-[#00d4ff]"><Document /></el-icon>
                  <span class="text-gray-300">文件大小：</span>
                  <span class="text-white">{{ formatFileSize(photo.fileSize) }}</span>
                </div>

                <div v-if="photo.mimeType" class="flex items-center gap-2">
                  <el-icon class="text-[#00d4ff]"><Document /></el-icon>
                  <span class="text-gray-300">文件类型：</span>
                  <span class="text-white">{{ photo.mimeType }}</span>
                </div>

                <div class="flex items-center gap-2">
                  <el-icon class="text-[#00d4ff]"><View /></el-icon>
                  <span class="text-gray-300">查看次数：</span>
                  <span class="text-white">{{ photo.viewCount || 0 }}</span>
                </div>

                <div class="flex items-center gap-2">
                  <el-icon class="text-[#00d4ff]"><Star /></el-icon>
                  <span class="text-gray-300">点赞次数：</span>
                  <span class="text-white">{{ photo.likeCount || 0 }}</span>
                </div>

                <div v-if="photo.createTime" class="flex items-center gap-2">
                  <el-icon class="text-[#00d4ff]"><Calendar /></el-icon>
                  <span class="text-gray-300">创建时间：</span>
                  <span class="text-white">{{ formatDateTime(photo.createTime) }}</span>
                </div>

                <div v-if="photo.updateTime" class="flex items-center gap-2">
                  <el-icon class="text-[#00d4ff]"><Edit /></el-icon>
                  <span class="text-gray-300">更新时间：</span>
                  <span class="text-white">{{ formatDateTime(photo.updateTime) }}</span>
                </div>

                <div v-if="photo.travelRecordId" class="flex items-center gap-2">
                  <el-icon class="text-[#00d4ff]"><LocationFilled /></el-icon>
                  <span class="text-gray-300">关联旅游记录：</span>
                  <span class="text-white">ID {{ photo.travelRecordId }}</span>
                </div>

                <div v-if="photo.concertRecordId" class="flex items-center gap-2">
                  <el-icon class="text-[#00d4ff]"><VideoCamera /></el-icon>
                  <span class="text-gray-300">关联观演记录：</span>
                  <span class="text-white">ID {{ photo.concertRecordId }}</span>
                </div>
              </div>

              <!-- 标签 -->
              <div v-if="photo.tags" class="flex flex-wrap gap-2">
                <el-tag
                  v-for="tag in getTags(photo.tags)"
                  :key="tag"
                  type="info"
                  size="default"
                >
                  {{ tag }}
                </el-tag>
              </div>

              <!-- 操作按钮 -->
              <div class="flex gap-3 pt-4 border-t border-white/10">
                <AnimatedButton
                  variant="primary"
                  @click="openEditDialog"
                >
                  <el-icon><Edit /></el-icon>
                  编辑
                </AnimatedButton>
                <el-popconfirm
                  title="确定删除这张照片？"
                  confirm-button-text="删除"
                  cancel-button-text="取消"
                  confirm-button-type="danger"
                  @confirm="handleDelete"
                >
                  <template #reference>
                    <AnimatedButton variant="danger">
                      <el-icon><Delete /></el-icon>
                      删除
                    </AnimatedButton>
                  </template>
                </el-popconfirm>
              </div>
            </div>
          </AnimatedCard>
        </div>
      </div>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑照片信息"
      width="520px"
      :close-on-click-modal="false"
    >
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="editForm.title" placeholder="照片标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="3"
            placeholder="照片描述"
          />
        </el-form-item>
        <el-form-item label="标签">
          <el-input
            v-model="editForm.tags"
            placeholder="用逗号分隔，如：风景,自然"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="editForm.categoryId" placeholder="选择分类" clearable>
            <el-option
              v-for="category in categoryOptions"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="editForm.location" placeholder="拍摄地点" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="updating" @click="handleUpdate">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Picture,
  Location,
  Clock,
  FullScreen,
  Document,
  View,
  Edit,
  Delete,
  Folder,
  Star,
  Calendar,
  LocationFilled,
  VideoCamera,
} from '@element-plus/icons-vue'
import {
  AnimatedButton,
  AnimatedCard,
  LoadingSpinner,
} from '@/components/uiverse'
import {
  getPhotoDetail,
  updatePhoto,
  deletePhoto,
  getPhotoCategoryList,
  type PhotoDetail,
  type PhotoCategory,
  type PhotoUpdateReq,
} from '@/api/photo'

const route = useRoute()
const router = useRouter()

const photo = ref<PhotoDetail | null>(null)
const categoryOptions = ref<PhotoCategory[]>([])
const loading = ref(false)
const updating = ref(false)
const imageError = ref(false)
const editDialogVisible = ref(false)

const editForm = ref<PhotoUpdateReq>({
  title: '',
  description: '',
  tags: '',
  categoryId: undefined,
  location: '',
})

// 加载照片详情
const loadPhotoDetail = async () => {
  const id = Number(route.params.id)
  if (!id) {
    ElMessage.error('照片ID无效')
    router.back()
    return
  }

  loading.value = true
  try {
    const res = await getPhotoDetail(id)
    if (res.code === 0) {
      photo.value = res.data
      // 初始化编辑表单
      editForm.value = {
        title: photo.value.title || '',
        description: photo.value.description || '',
        tags: photo.value.tags || '',
        categoryId: photo.value.categoryId,
        location: photo.value.location || '',
      }
    } else {
      ElMessage.error(res.msg || '加载照片详情失败')
      router.back()
    }
  } catch (error: any) {
    console.error('加载照片详情失败:', error)
    ElMessage.error(error.message || '加载照片详情失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const res = await getPhotoCategoryList()
    if (res.code === 0) {
      categoryOptions.value = res.data || []
    }
  } catch (error) {
    console.error('加载分类列表失败:', error)
  }
}

// 获取标签数组
const getTags = (tags?: string): string[] => {
  if (!tags) return []
  return tags.split(',').filter(t => t.trim())
}

// 格式化日期时间
const formatDateTime = (dateStr?: string): string => {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    })
  } catch {
    return dateStr
  }
}

// 格式化文件大小
const formatFileSize = (bytes?: number): string => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

// 打开编辑对话框
const openEditDialog = () => {
  if (photo.value) {
    editForm.value = {
      title: photo.value.title || '',
      description: photo.value.description || '',
      tags: photo.value.tags || '',
      categoryId: photo.value.categoryId,
      location: photo.value.location || '',
    }
    editDialogVisible.value = true
  }
}

// 更新照片
const handleUpdate = async () => {
  if (!photo.value) return

  updating.value = true
  try {
    // 确保请求体中包含ID（后端验证需要）
    const updateData = {
      ...editForm.value,
      id: photo.value.id,
    }
    const res = await updatePhoto(photo.value.id, updateData)
    if (res.code === 0) {
      ElMessage.success('更新成功')
      editDialogVisible.value = false
      loadPhotoDetail()
    } else {
      ElMessage.error(res.msg || '更新失败')
    }
  } catch (error: any) {
    console.error('更新照片失败:', error)
    ElMessage.error(error.message || '更新照片失败')
  } finally {
    updating.value = false
  }
}

// 删除照片
const handleDelete = async () => {
  if (!photo.value) return

  try {
    const res = await deletePhoto(photo.value.id)
    if (res.code === 0) {
      ElMessage.success('删除成功')
      router.push('/photo')
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error: any) {
    console.error('删除照片失败:', error)
    ElMessage.error(error.message || '删除照片失败')
  }
}

// 返回
const goBack = () => {
  router.back()
}

// 初始化
onMounted(() => {
  loadCategories()
  loadPhotoDetail()
})
</script>

<style scoped>
.photo-detail-page {
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

.photo-detail-content {
  animation: contentFadeIn 0.5s ease-out;
}

@keyframes contentFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

