<template>
  <div class="photo-page">
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 sm:mb-8">
      <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-4 sm:mb-0 float-animation">摄影</h1>
      <div class="flex gap-3">
        <AnimatedButton variant="secondary" @click="openCategoryManageDialog">
          <el-icon><FolderOpened /></el-icon>
          管理分类
        </AnimatedButton>
        <AnimatedButton variant="primary" @click="openUploadDialog">
        <el-icon><Plus /></el-icon>
        上传照片
      </AnimatedButton>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="mb-6">
      <div class="flex flex-col gap-3">
        <div class="flex flex-wrap gap-2 w-full">
          <!-- 全部按钮始终在最前面 -->
          <AnimatedButton
            :variant="filterCategoryId === null ? 'primary' : 'secondary'"
            size="small"
            @click="changeCategory(null)"
          >
            全部
          </AnimatedButton>
          <!-- 其他分类 -->
          <AnimatedButton
            v-for="category in categoryOptions"
            :key="category.id"
            :variant="filterCategoryId === category.id ? 'primary' : 'secondary'"
            size="small"
            @click="changeCategory(category.id)"
          >
            {{ category.name }}
            <span
              v-if="category.photoCount > 0"
              class="ml-1.5 px-1.5 py-0.5 rounded text-xs font-medium"
              :class="filterCategoryId === category.id 
                ? 'bg-white/20 text-white' 
                : 'bg-white/10 text-gray-300'"
            >
              {{ category.photoCount }}
            </span>
          </AnimatedButton>
        </div>
        <AnimatedSearch
          v-model="keyword"
          placeholder="搜索照片标题、描述、标签…"
          @enter="loadPhotos"
          @clear="loadPhotos"
        />
      </div>
    </div>

    <!-- 照片网格 -->
    <div class="min-h-[120px] relative">
      <!-- 骨架屏加载 -->
      <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6">
        <SkeletonCard v-for="i in pageSize" :key="`skeleton-${i}`" />
      </div>

      <EmptyState
        v-else-if="photoList.length === 0"
        title="暂无照片"
        description="点击右上角「上传照片」开始记录你的美好瞬间"
      >
        <template #icon>
          <el-icon :size="64" class="text-gray-500">
            <Picture />
          </el-icon>
        </template>
        <template #action>
          <AnimatedButton variant="primary" @click="openUploadDialog">
            <el-icon><Plus /></el-icon>
            上传第一张照片
          </AnimatedButton>
        </template>
      </EmptyState>

      <div
        v-else
        class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6"
      >
      <AnimatedCard
          v-for="(photo, index) in photoList"
        :key="photo.id"
        variant="3d"
          class="photo-card-wrapper group cursor-pointer"
        :style="{ animationDelay: `${index * 50}ms` }"
          @click="goDetail(photo.id)"
      >
        <div class="rounded-2xl overflow-hidden">
          <!-- 照片 -->
          <div class="relative aspect-square overflow-hidden bg-gradient-to-br from-[#c3cfe2]/20 to-[#f5f7fa]/10">
            <img 
                v-if="getPhotoUrl(photo) && !imageErrorMap[photo.id]" 
                :src="getPhotoUrl(photo)" 
                :alt="photo.title || '照片'"
              class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
              loading="lazy"
              @error="handleImageError(photo.id)"
              @load="handleImageLoad(photo.id)"
            />
            <div v-else class="w-full h-full flex items-center justify-center">
              <el-icon :size="48" class="text-[#c3cfe2]/40">
                <Picture />
              </el-icon>
            </div>
            
            <!-- 标签 -->
              <div v-if="photo.tags" class="absolute top-3 left-3 right-3 flex flex-wrap gap-2 photo-tags">
              <el-tag
                  v-for="tag in getTags(photo.tags).slice(0, 2)"
                :key="tag"
                type="info"
                size="small"
                class="photo-tag-item"
              >
                {{ tag }}
              </el-tag>
            </div>

            <!-- 拍摄时间 -->
              <div v-if="photo.shootTime" class="absolute bottom-3 left-3 right-3">
              <div class="bg-black/60 backdrop-blur-sm px-2 py-1 rounded-lg">
                  <p class="text-white text-xs">{{ formatDate(photo.shootTime) }}</p>
                </div>
            </div>
          </div>

          <!-- 信息 -->
          <div class="p-4">
              <h3 class="text-lg font-semibold text-white mb-1 line-clamp-1">
                {{ photo.title || '未命名照片' }}
              </h3>
              <p v-if="photo.category" class="text-sm text-[#00d4ff]/70 mb-2">
                {{ photo.category }}
              </p>

            <!-- 位置信息 -->
            <div v-if="photo.location" class="flex items-center text-xs text-gray-400">
              <el-icon class="mr-1"><Location /></el-icon>
              <span class="line-clamp-1">{{ photo.location }}</span>
            </div>
          </div>
        </div>
      </AnimatedCard>
    </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="mt-6 flex justify-center">
      <el-pagination
        v-model:current-page="pageNo"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[12, 24, 48, 96]"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 上传照片对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传照片"
      width="520px"
      destroy-on-close
      class="photo-upload-dialog"
    >
      <div class="photo-upload-form-wrapper glass-effect">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :before-upload="beforeUpload"
          :limit="10"
          multiple
          drag
          accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
          class="upload-area"
        >
          <el-icon class="el-icon--upload upload-icon"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持 jpg、png、gif、webp 格式，单个文件不超过50MB
            </div>
          </template>
        </el-upload>

        <el-form :model="uploadForm" label-width="80px" class="photo-upload-form">
          <el-form-item label="标题">
            <el-input v-model="uploadForm.title" placeholder="照片标题（可选）" />
          </el-form-item>
          <el-form-item label="描述">
            <el-input
              v-model="uploadForm.description"
              type="textarea"
              :rows="3"
              placeholder="照片描述（可选）"
            />
          </el-form-item>
          <el-form-item label="标签">
            <el-input
              v-model="uploadForm.tags"
              placeholder="用逗号分隔，如：风景,自然（可选）"
            />
          </el-form-item>
          <el-form-item label="分类">
            <el-select v-model="uploadForm.categoryId" placeholder="选择分类（可选）" clearable>
              <el-option
                v-for="category in categoryOptions"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="地点">
            <el-input v-model="uploadForm.location" placeholder="拍摄地点（可选）" />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <AnimatedButton variant="secondary" @click="uploadDialogVisible = false">
            取消
          </AnimatedButton>
          <AnimatedButton
            variant="primary"
            :loading="uploading"
            @click="handleUpload"
          >
            上传
          </AnimatedButton>
        </span>
      </template>
    </el-dialog>

    <!-- 分类管理对话框 -->
    <el-dialog
      v-model="categoryManageDialogVisible"
      title="管理分类"
      width="90%"
      :max-width="800"
      :close-on-click-modal="false"
      class="category-manage-dialog"
    >
      <div class="category-manage-content">
        <!-- 操作栏 -->
        <div class="category-manage-header">
          <div class="category-count">
            <el-icon><FolderOpened /></el-icon>
            <span>共 {{ categoryOptions.length }} 个分类</span>
          </div>
          <AnimatedButton variant="primary" size="small" @click="openCategoryForm('create')">
            <el-icon><Plus /></el-icon>
            新建分类
          </AnimatedButton>
        </div>

        <!-- 分类列表 -->
        <div v-if="categoryOptions.length === 0" class="category-empty">
          <el-icon :size="64" class="empty-icon"><FolderOpened /></el-icon>
          <p class="empty-text">暂无分类</p>
          <p class="empty-hint">点击「新建分类」创建第一个分类</p>
        </div>

        <div v-else class="category-list">
          <div
            v-for="category in categoryOptions"
            :key="category.id"
            class="category-item"
          >
            <!-- 分类信息 -->
            <div class="category-info">
              <div class="category-header">
                <h3 class="category-name">{{ category.name }}</h3>
                <span class="category-count-badge">{{ category.photoCount }} 张</span>
              </div>
              <p v-if="category.description" class="category-description">
                {{ category.description }}
              </p>
            </div>

            <!-- 操作按钮 -->
            <div class="category-actions">
              <AnimatedButton
                variant="secondary"
                size="small"
                @click="openCategoryForm('edit', category)"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </AnimatedButton>
              <el-popconfirm
                title="确定删除该分类？"
                :disabled="category.photoCount > 0"
                :confirm-button-text="category.photoCount > 0 ? '分类中有照片，无法删除' : '删除'"
                cancel-button-text="取消"
                confirm-button-type="danger"
                @confirm="handleDeleteCategory(category.id)"
              >
                <template #reference>
                  <AnimatedButton
                    variant="danger"
                    size="small"
                    :disabled="category.photoCount > 0"
                  >
                    <el-icon><Delete /></el-icon>
                    删除
                  </AnimatedButton>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="categoryManageDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分类表单对话框 -->
    <el-dialog
      v-model="categoryFormVisible"
      :title="categoryFormMode === 'create' ? '新建分类' : '编辑分类'"
      width="520px"
      destroy-on-close
      class="category-form-dialog"
    >
      <div class="category-form-wrapper glass-effect">
        <el-form :model="categoryForm" label-width="80px" class="category-form">
          <el-form-item label="分类名称" required>
            <el-input
              v-model="categoryForm.name"
              placeholder="请输入分类名称"
              maxlength="64"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="分类图标">
            <el-input
              v-model="categoryForm.icon"
              placeholder="图标名称（可选）"
              maxlength="64"
            />
          </el-form-item>
          <el-form-item label="分类描述">
            <el-input
              v-model="categoryForm.description"
              type="textarea"
              :rows="3"
              placeholder="分类描述（可选）"
              maxlength="255"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="排序">
            <el-input-number
              v-model="categoryForm.sortOrder"
              :min="0"
              :max="9999"
              controls-position="right"
              class="w-full"
            />
            <div class="form-hint">数字越小越靠前</div>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <AnimatedButton variant="secondary" @click="categoryFormVisible = false">
            取消
          </AnimatedButton>
          <AnimatedButton
            variant="primary"
            :loading="categorySubmitting"
            @click="handleCategorySubmit"
          >
            {{ categoryFormMode === 'create' ? '创建' : '保存' }}
          </AnimatedButton>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Picture, Location, UploadFilled, FolderOpened, Edit, Delete } from '@element-plus/icons-vue'
import { 
  AnimatedButton, 
  AnimatedCard,
  AnimatedSearch,
  SkeletonCard,
  EmptyState
} from '@/components/uiverse'
import {
  getPhotoPage,
  uploadPhoto,
  batchUploadPhotos,
  deletePhoto,
  getPhotoCategoryList,
  createPhotoCategory,
  updatePhotoCategory,
  deletePhotoCategory,
  type PhotoPageItem,
  type PhotoCategory,
  type PhotoUploadReq,
  type PhotoCategoryCreateReq,
  type PhotoCategoryUpdateReq,
} from '@/api/photo'

const router = useRouter()

// 数据
const photoList = ref<PhotoPageItem[]>([])
const categoryOptions = ref<PhotoCategory[]>([])
const loading = ref(false)
const uploading = ref(false)
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(24)
const keyword = ref('')
const filterCategoryId = ref<number | null>(null)
const imageErrorMap = ref<Record<number, boolean>>({})
const uploadDialogVisible = ref(false)
const uploadRef = ref()
const selectedFiles = ref<File[]>([])
const categoryManageDialogVisible = ref(false)
const categoryFormVisible = ref(false)
const categoryFormMode = ref<'create' | 'edit'>('create')
const editingCategoryId = ref<number | null>(null)

// 上传表单
const uploadForm = ref<PhotoUploadReq>({
  title: '',
  description: '',
  tags: '',
  categoryId: undefined,
  location: '',
})

// 分类表单
const categoryForm = ref<PhotoCategoryCreateReq>({
  name: '',
  icon: '',
  description: '',
  sortOrder: 0,
})

// 加载照片列表
const loadPhotos = async () => {
  loading.value = true
  try {
    const params: any = {
      pageNo: pageNo.value,
      pageSize: pageSize.value,
    }
    
    if (filterCategoryId.value !== null) {
      params.categoryId = filterCategoryId.value
    }
    
    if (keyword.value) {
      params.keyword = keyword.value
    }

    const res = await getPhotoPage(params)
    if (res.code === 0) {
      photoList.value = res.data.list || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '加载照片列表失败')
    }
  } catch (error: any) {
    console.error('加载照片列表失败:', error)
    ElMessage.error(error.message || '加载照片列表失败')
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

// 切换分类
const changeCategory = (categoryId: number | null) => {
  filterCategoryId.value = categoryId
  pageNo.value = 1
  loadPhotos()
}

// 获取照片URL（优先使用缩略图）
const getPhotoUrl = (photo: PhotoPageItem) => {
  return photo.thumbnailPath || photo.filePath
}

// 获取标签数组
const getTags = (tags?: string): string[] => {
  if (!tags) return []
  return tags.split(',').filter(t => t.trim())
}

// 格式化日期
const formatDate = (dateStr?: string): string => {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    })
  } catch {
    return dateStr
  }
}

// 图片加载错误处理
const handleImageError = (photoId: number) => {
  imageErrorMap.value[photoId] = true
}

const handleImageLoad = (photoId: number) => {
  imageErrorMap.value[photoId] = false
}

// 打开上传对话框
const openUploadDialog = () => {
  uploadDialogVisible.value = true
  uploadForm.value = {
    title: '',
    description: '',
    tags: '',
    categoryId: undefined,
    location: '',
  }
  selectedFiles.value = []
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 文件大小限制
const MAX_FILE_SIZE = 50 * 1024 * 1024 // 50MB

// 文件上传前校验
const beforeUpload = (file: File) => {
  // 检查文件大小
  if (file.size > MAX_FILE_SIZE) {
    ElMessage.error(`文件 "${file.name}" 大小超过50MB，请选择较小的文件`)
    return false
  }
  
  // 检查文件类型
  const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
  if (!validTypes.includes(file.type)) {
    ElMessage.error(`文件 "${file.name}" 格式不支持，请选择 jpg、png、gif 或 webp 格式的图片`)
    return false
  }
  
  return true
}

// 文件选择变化
const handleFileChange = (file: any, fileList: any[]) => {
  // 校验文件大小
  const invalidFiles = fileList.filter((f: any) => {
    if (f.raw && f.raw.size > MAX_FILE_SIZE) {
      ElMessage.error(`文件 "${f.name}" 大小超过50MB，已自动移除`)
      return true
    }
    return false
  })
  
  // 移除无效文件
  invalidFiles.forEach((f: any) => {
    if (uploadRef.value) {
      uploadRef.value.remove(f)
    }
  })
  
  selectedFiles.value = fileList
    .map((f: any) => f.raw)
    .filter((f: File) => f && f.size <= MAX_FILE_SIZE)
}

// 文件移除
const handleFileRemove = (file: any, fileList: any[]) => {
  selectedFiles.value = fileList.map((f: any) => f.raw).filter(Boolean)
}

// 上传照片
const handleUpload = async () => {
  if (selectedFiles.value.length === 0) {
    ElMessage.warning('请选择要上传的照片')
    return
  }

  // 再次校验文件大小（防止绕过校验）
  const invalidFiles = selectedFiles.value.filter(file => file.size > MAX_FILE_SIZE)
  if (invalidFiles.length > 0) {
    ElMessage.error(`有 ${invalidFiles.length} 个文件大小超过50MB，请重新选择`)
    return
  }

  uploading.value = true
  try {
    if (selectedFiles.value.length === 1) {
      // 单文件上传
      const res = await uploadPhoto(selectedFiles.value[0], uploadForm.value)
      if (res.code === 0) {
        ElMessage.success('照片上传成功')
        uploadDialogVisible.value = false
        loadPhotos()
        loadCategories() // 刷新分类统计
      } else {
        ElMessage.error(res.msg || '照片上传失败')
      }
    } else {
      // 批量上传
      const res = await batchUploadPhotos(selectedFiles.value, uploadForm.value)
      if (res.code === 0) {
        ElMessage.success(`成功上传 ${res.data.length} 张照片`)
        uploadDialogVisible.value = false
        loadPhotos()
        loadCategories() // 刷新分类统计
      } else {
        ElMessage.error(res.msg || '照片上传失败')
      }
    }
  } catch (error: any) {
    console.error('上传照片失败:', error)
    ElMessage.error(error.message || '上传照片失败')
  } finally {
    uploading.value = false
  }
}

// 查看详情
const goDetail = (id: number) => {
  router.push(`/photo/${id}`)
}

// 分页处理
const handlePageChange = (page: number) => {
  pageNo.value = page
  loadPhotos()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  pageNo.value = 1
  loadPhotos()
}

// 打开分类管理对话框
const openCategoryManageDialog = () => {
  categoryManageDialogVisible.value = true
  loadCategories()
}

// 打开分类表单
const openCategoryForm = (mode: 'create' | 'edit', category?: PhotoCategory) => {
  categoryFormMode.value = mode
  if (mode === 'create') {
    categoryForm.value = {
      name: '',
      icon: '',
      description: '',
      sortOrder: categoryOptions.value.length,
    }
    editingCategoryId.value = null
  } else if (category) {
    categoryForm.value = {
      name: category.name,
      icon: category.icon || '',
      description: category.description || '',
      sortOrder: category.sortOrder,
    }
    editingCategoryId.value = category.id
  }
  categoryFormVisible.value = true
}

// 分类提交
const categorySubmitting = ref(false)
const handleCategorySubmit = async () => {
  if (!categoryForm.value.name || categoryForm.value.name.trim() === '') {
    ElMessage.warning('请输入分类名称')
    return
  }

  categorySubmitting.value = true
  try {
    if (categoryFormMode.value === 'create') {
      const res = await createPhotoCategory(categoryForm.value)
      if (res.code === 0) {
        ElMessage.success('分类创建成功')
        categoryFormVisible.value = false
        loadCategories()
        loadPhotos() // 刷新照片列表以更新分类统计
      } else {
        ElMessage.error(res.msg || '分类创建失败')
      }
    } else if (editingCategoryId.value) {
      const res = await updatePhotoCategory(editingCategoryId.value, categoryForm.value as PhotoCategoryUpdateReq)
      if (res.code === 0) {
        ElMessage.success('分类更新成功')
        categoryFormVisible.value = false
        loadCategories()
        loadPhotos() // 刷新照片列表以更新分类统计
      } else {
        ElMessage.error(res.msg || '分类更新失败')
      }
    }
  } catch (error: any) {
    console.error('分类操作失败:', error)
    ElMessage.error(error.message || '分类操作失败')
  } finally {
    categorySubmitting.value = false
  }
}

// 删除分类
const handleDeleteCategory = async (id: number) => {
  try {
    const res = await deletePhotoCategory(id)
    if (res.code === 0) {
      ElMessage.success('分类删除成功')
      loadCategories()
      loadPhotos() // 刷新照片列表
      // 如果删除的是当前筛选的分类，重置筛选
      if (filterCategoryId.value === id) {
        filterCategoryId.value = null
        loadPhotos()
      }
    } else {
      ElMessage.error(res.msg || '分类删除失败')
    }
  } catch (error: any) {
    console.error('删除分类失败:', error)
    ElMessage.error(error.message || '删除分类失败')
  }
}

// 初始化
onMounted(() => {
  loadCategories()
  loadPhotos()
})
</script>

<style scoped>
.photo-page {
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

/* 照片卡片进入动画 */
.photo-card-wrapper {
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
.photo-tags {
  animation: tagsFadeIn 0.4s ease-out 0.2s both;
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

.photo-tag-item {
  animation: tagPop 0.3s ease-out both;
}

.photo-tag-item:nth-child(1) { animation-delay: 0.1s; }
.photo-tag-item:nth-child(2) { animation-delay: 0.2s; }

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

.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 上传照片对话框样式 - 参考影视模块 */
:deep(.photo-upload-dialog) {
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

:deep(.photo-upload-dialog .el-dialog__header) {
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

:deep(.photo-upload-dialog .el-dialog__title) {
  color: #e5f5ff;
  font-weight: 600;
  letter-spacing: 0.04em;
}

:deep(.photo-upload-dialog .el-dialog__body) {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 16px;
}

:deep(.photo-upload-dialog .el-dialog__footer) {
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.photo-upload-form-wrapper {
  padding: 16px 18px 4px;
  border-radius: 14px;
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.04),
    rgba(0, 212, 255, 0.05),
    rgba(0, 0, 0, 0.3)
  );
}

.photo-upload-form {
  --el-text-color-regular: #e5e7f0;
}

:deep(.photo-upload-dialog .el-form-item__label) {
  color: #cbd5ff;
}

.upload-area {
  margin-bottom: 16px;
}

:deep(.upload-area .el-upload) {
  width: 100%;
}

:deep(.upload-area .el-upload-dragger) {
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.05), rgba(255, 255, 255, 0.02));
  border: 2px dashed rgba(0, 212, 255, 0.3);
  border-radius: 12px;
  padding: 40px 20px;
  transition: all 0.3s ease;
}

:deep(.upload-area .el-upload-dragger:hover) {
  border-color: rgba(0, 212, 255, 0.5);
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.08), rgba(255, 255, 255, 0.04));
}

.upload-icon {
  font-size: 48px;
  color: rgba(0, 212, 255, 0.6);
  margin-bottom: 16px;
}

:deep(.upload-area .el-upload__text) {
  color: #e5f5ff;
  font-size: 14px;
}

:deep(.upload-area .el-upload__text em) {
  color: #00d4ff;
  font-style: normal;
  font-weight: 500;
}

:deep(.upload-area .el-upload__tip) {
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
  margin-top: 12px;
}

/* 移动端适配 */
@media (max-width: 600px) {
  :deep(.photo-upload-dialog) {
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

  :deep(.photo-upload-dialog .el-dialog__body) {
    padding: 12px 12px 80px;
  }

  :deep(.photo-upload-dialog .el-dialog__footer) {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 60px;
    z-index: 2000;
    background: linear-gradient(to top, rgba(10, 14, 35, 0.98), transparent);
    padding-top: 8px;
  }
}

/* 分类管理对话框样式 */
:deep(.category-manage-dialog .el-dialog) {
  background: linear-gradient(145deg, rgba(10, 14, 35, 0.95), rgba(15, 20, 45, 0.95));
  border: 1px solid rgba(0, 212, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4), 0 0 40px rgba(0, 212, 255, 0.1);
}

:deep(.category-manage-dialog .el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

:deep(.category-manage-dialog .el-dialog__title) {
  color: #e5f5ff;
  font-weight: 600;
  font-size: 18px;
}

:deep(.category-manage-dialog .el-dialog__body) {
  padding: 24px;
}

.category-manage-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.category-manage-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.category-count {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}

.category-count .el-icon {
  color: #00d4ff;
}

.category-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-icon {
  color: rgba(255, 255, 255, 0.3);
  margin-bottom: 16px;
}

.empty-text {
  color: rgba(255, 255, 255, 0.6);
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
}

.empty-hint {
  color: rgba(255, 255, 255, 0.4);
  font-size: 14px;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.category-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.05), rgba(255, 255, 255, 0.02));
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.category-item:hover {
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.08), rgba(255, 255, 255, 0.04));
  border-color: rgba(0, 212, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 212, 255, 0.1);
}

.category-info {
  flex: 1;
  min-width: 0;
}

.category-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.category-name {
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
  margin: 0;
}

.category-count-badge {
  padding: 2px 8px;
  background: rgba(0, 212, 255, 0.2);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 12px;
  color: #00d4ff;
  font-size: 12px;
  font-weight: 500;
}

.category-description {
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
  margin: 0;
  line-height: 1.5;
}

.category-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

/* 分类表单对话框样式 - 参考影视模块 */
:deep(.category-form-dialog) {
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

:deep(.category-form-dialog .el-dialog__header) {
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

:deep(.category-form-dialog .el-dialog__title) {
  color: #e5f5ff;
  font-weight: 600;
  letter-spacing: 0.04em;
}

:deep(.category-form-dialog .el-dialog__body) {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 16px;
}

:deep(.category-form-dialog .el-dialog__footer) {
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.category-form-wrapper {
  padding: 16px 18px 4px;
  border-radius: 14px;
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.04),
    rgba(0, 212, 255, 0.05),
    rgba(0, 0, 0, 0.3)
  );
}

.category-form {
  --el-text-color-regular: #e5e7f0;
}

:deep(.category-form-dialog .el-form-item__label) {
  color: #cbd5ff;
}

.form-hint {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  margin-top: 8px;
}

/* 移动端适配 */
@media (max-width: 600px) {
  :deep(.category-form-dialog) {
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

  :deep(.category-form-dialog .el-dialog__body) {
    padding: 12px 12px 80px;
  }

  :deep(.category-form-dialog .el-dialog__footer) {
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
