<template>
  <div class="concert-page">
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 sm:mb-8">
      <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-4 sm:mb-0 float-animation">演唱会</h1>
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
            v-for="tag in tagOptions"
            :key="tag.value"
            :variant="filterTag === tag.value ? 'primary' : 'secondary'"
            size="small"
            @click="changeTag(tag.value)"
          >
            {{ tag.label }}
          </AnimatedButton>
        </div>
        <AnimatedSearch
          v-model="keyword"
          placeholder="搜索艺人、地点…"
          @enter="handleSearch"
          @clear="handleSearch"
        />
      </div>
    </div>

    <!-- 演唱会卡片网格 -->
    <div class="min-h-[120px] relative">
      <!-- 骨架屏加载 -->
      <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6">
        <SkeletonCard v-for="i in pageSize" :key="`skeleton-${i}`" />
      </div>
      
      <EmptyState
        v-else-if="concertList.length === 0"
        title="暂无记录"
        description="点击右上角「添加记录」开始你的演唱会之旅"
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
          v-for="(concert, index) in concertList"
          :key="concert.id"
        >
          <div class="flex flex-col gap-2 concert-card-wrapper" :style="{ animationDelay: `${index * 50}ms` }">
            <!-- 演唱会卡片 -->
            <AnimatedCard
              variant="3d"
              class="group cursor-pointer"
              @click="(e) => handleCardClick(e, concert)"
            >
              <div class="rounded-2xl overflow-hidden">
                <!-- 图片 -->
                <div class="relative aspect-[4/3] overflow-hidden concert-poster-container">
                  <div class="poster-overlay"></div>
                  <img
                    v-if="concert.imageUrl && !imageErrorMap[concert.id]"
                    :src="concert.imageUrl"
                    :alt="concert.artist"
                    class="concert-poster w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
                    loading="lazy"
                    @error="handleImageError(concert.id)"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center bg-gradient-to-br from-[#00d4ff]/20 to-[#00ffcc]/10 poster-placeholder">
                    <el-icon :size="48" class="text-[#c3cfe2]/40">
                      <Headset />
                    </el-icon>
                  </div>

                  <!-- 评分 -->
                  <div
                    v-if="concert.rating != null"
                    class="rating-badge absolute bottom-3 left-3 flex items-center space-x-1 bg-black/60 backdrop-blur-sm px-2 py-1 rounded-lg"
                  >
                    <el-icon class="text-yellow-400 rating-star"><StarFilled /></el-icon>
                    <span class="text-white text-sm font-semibold rating-value">{{ concert.rating }}</span>
                  </div>
                </div>

                <!-- 信息 -->
                <div class="p-4 flex flex-col gap-2 glass-effect">
                  <h3 class="text-lg font-semibold text-white line-clamp-1">{{ concert.artist }}</h3>
                  <p class="text-sm text-[#00d4ff]/80 line-clamp-1">{{ concert.venue }}</p>
                  <p class="text-xs text-gray-400">
                    <span v-if="concert.city">{{ concert.city }}</span>
                    <span v-if="concert.concertDate"> · {{ concert.concertDate }}</span>
                  </p>

                  <!-- 标签 -->
                  <div
                    v-if="concert.tags"
                    class="flex flex-wrap gap-2 mt-1 concert-tags"
                  >
                    <AnimatedTag
                      v-for="(tag, tagIndex) in concert.tags.split(',')"
                      :key="tag"
                      variant="glow"
                      :animated="tagIndex % 2 === 0"
                      class="concert-tag-item"
                    >
                      {{ tag }}
                    </AnimatedTag>
                  </div>

                  <div class="mt-2 flex justify-between items-center concert-actions">
                    <AnimatedButton
                      variant="outline"
                      size="small"
                      @click.stop="openEditDialog()"
                      class="action-btn edit-btn"
                    >
                      编辑
                    </AnimatedButton>
                    <el-popconfirm
                      title="确定删除该记录？"
                      confirm-button-text="删除"
                      cancel-button-text="取消"
                      confirm-button-type="danger"
                      @confirm="handleDelete(concert.id)"
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

            <!-- 照片展示区域 -->
            <AnimatedCard
              v-if="concert.photos && concert.photos.length > 0"
              variant="glass"
              class="photo-gallery-card"
            >
              <div class="p-4">
                <div class="flex items-center justify-between mb-3">
                  <div class="flex items-center gap-2">
                    <el-icon class="text-[#00d4ff]/60">
                      <Picture />
                    </el-icon>
                    <span class="text-sm text-gray-300">照片 ({{ concert.photos.length }})</span>
                  </div>
                  <div class="flex gap-2">
                    <el-button
                      v-if="concert.photos.length > 6"
                      text
                      size="small"
                      class="!text-[#00d4ff] hover:!text-[#00ffcc]"
                      @click="openAllPhotosDialog(concert)"
                    >
                      查看全部
                    </el-button>
                    <el-button
                      text
                      size="small"
                      class="!text-[#00d4ff] hover:!text-[#00ffcc]"
                      @click="openPhotoUploadDialog(concert.id)"
                    >
                      添加照片
                    </el-button>
                  </div>
                </div>
                <div class="grid grid-cols-3 gap-2">
                  <div
                    v-for="photo in concert.photos.slice(0, 6)"
                    :key="photo.id"
                    class="relative aspect-square rounded-lg overflow-hidden cursor-pointer group"
                    @click="previewPhoto(photo)"
                  >
                    <img
                      :src="photo.thumbnailPath || photo.filePath"
                      :alt="photo.title"
                      class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110"
                    />
                    <div
                      v-if="concert.photos.length > 6 && photo === concert.photos[5]"
                      class="absolute inset-0 bg-black/60 flex items-center justify-center text-white text-xs font-semibold cursor-pointer hover:bg-black/70 transition-colors"
                      @click.stop="openAllPhotosDialog(concert)"
                    >
                      +{{ concert.photos.length - 6 }}
                    </div>
                  </div>
                </div>
              </div>
            </AnimatedCard>

            <!-- 评价卡片 -->
            <AnimatedCard
              v-if="concert.comment"
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
                      <span class="text-[#00d4ff]/60">"</span>{{ concert.comment }}<span class="text-[#00d4ff]/60">"</span>
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
          @current-change="loadConcerts"
          @size-change="loadConcerts"
          class="animated-pagination"
        />
      </div>
    </div>

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
      :title="`${allPhotosConcert?.artist || ''} - 全部照片 (${allPhotosList.length})`"
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Headset, StarFilled, Search, ChatLineRound, Picture, Upload, Delete } from '@element-plus/icons-vue'
import { uploadPhoto, getPhotoList, deletePhoto, linkPhotoToConcert } from '@/api/photo'
import type { Photo } from '@/api/photo'
import AnimatedButton from '@/components/uiverse/AnimatedButton.vue'
import AnimatedCard from '@/components/uiverse/AnimatedCard.vue'
import EmptyState from '@/components/uiverse/EmptyState.vue'
import SkeletonCard from '@/components/uiverse/SkeletonCard.vue'
import AnimatedSearch from '@/components/uiverse/AnimatedSearch.vue'
import AnimatedTag from '@/components/uiverse/AnimatedTag.vue'

const router = useRouter()

const loading = ref(false)
const concertList = ref<any[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(8)
const filterTag = ref<string | 'all'>('all')
const keyword = ref('')

const tagOptions = [
  { label: '全部', value: 'all' as const },
  { label: '流行', value: '流行' },
  { label: '摇滚', value: '摇滚' },
  { label: '民谣', value: '民谣' },
  { label: '电子', value: '电子' },
  { label: '说唱', value: '说唱' },
]

const imageErrorMap = reactive<Record<number, boolean>>({})

// 假数据
const mockConcerts = [
  {
    id: 1,
    artist: '周杰伦',
    venue: '北京鸟巢',
    city: '北京',
    concertDate: '2024-12-25',
    rating: 9.5,
    tags: '流行,华语',
    comment: '现场氛围太棒了，经典歌曲一首接一首，全场大合唱！',
    imageUrl: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop',
    photos: [
      { id: 1001, title: '演唱会现场', filePath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=300&h=300&fit=crop' },
      { id: 1002, title: '舞台全景', filePath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop' },
      { id: 1003, title: '观众席', filePath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=300&h=300&fit=crop' },
      { id: 1004, title: '灯光秀', filePath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop' },
    ] as Photo[],
  },
  {
    id: 2,
    artist: '五月天',
    venue: '上海梅赛德斯奔驰文化中心',
    city: '上海',
    concertDate: '2024-11-20',
    rating: 9.8,
    tags: '摇滚,华语',
    comment: '五月天的现场感染力太强了，三个小时完全不够！',
    imageUrl: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop',
    photos: [
      { id: 2001, title: '演唱会现场', filePath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop' },
      { id: 2002, title: '舞台', filePath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=300&h=300&fit=crop' },
      { id: 2003, title: '全场大合唱', filePath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop' },
    ] as Photo[],
  },
  {
    id: 3,
    artist: 'Taylor Swift',
    venue: 'Tokyo Dome',
    city: '东京',
    concertDate: '2024-10-15',
    rating: 9.7,
    tags: '流行,欧美',
    comment: 'Taylor的舞台设计太震撼了，每一首歌都是视觉盛宴！',
    imageUrl: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop',
    photos: [
      { id: 3001, title: '演唱会现场', filePath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=300&h=300&fit=crop' },
      { id: 3002, title: '舞台设计', filePath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop' },
      { id: 3003, title: '烟花表演', filePath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=300&h=300&fit=crop' },
      { id: 3004, title: '观众互动', filePath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop' },
      { id: 3005, title: '服装造型', filePath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=300&h=300&fit=crop' },
      { id: 3006, title: '开场表演', filePath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop' },
      { id: 3007, title: '灯光秀', filePath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=300&h=300&fit=crop' },
      { id: 3008, title: 'Encore环节', filePath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop' },
      { id: 3009, title: '全场大合唱', filePath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=300&h=300&fit=crop' },
      { id: 3010, title: '后台花絮', filePath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop' },
      { id: 3011, title: '签名环节', filePath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=300&h=300&fit=crop' },
      { id: 3012, title: '结束合影', filePath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop' },
    ] as Photo[],
  },
  {
    id: 4,
    artist: '李健',
    venue: '深圳湾体育中心',
    city: '深圳',
    concertDate: '2024-09-10',
    rating: 9.3,
    tags: '民谣,华语',
    comment: '李健的声音太治愈了，现场听《贝加尔湖畔》简直是一种享受。',
    imageUrl: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop',
    photos: [
      { id: 4001, title: '演唱会现场', filePath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop' },
      { id: 4002, title: '舞台', filePath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=300&h=300&fit=crop' },
    ] as Photo[],
  },
  {
    id: 5,
    artist: 'Coldplay',
    venue: 'Singapore National Stadium',
    city: '新加坡',
    concertDate: '2024-08-05',
    rating: 9.6,
    tags: '摇滚,欧美',
    comment: 'Coldplay的现场太震撼了，全场手环同步闪烁，美轮美奂！',
    imageUrl: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop',
    photos: [
      { id: 5001, title: '演唱会现场', filePath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=300&h=300&fit=crop' },
      { id: 5002, title: '手环闪烁', filePath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop' },
      { id: 5003, title: '舞台效果', filePath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=300&h=300&fit=crop' },
    ] as Photo[],
  },
  {
    id: 6,
    artist: '陈奕迅',
    venue: '香港红磡体育馆',
    city: '香港',
    concertDate: '2024-07-20',
    rating: 9.4,
    tags: '流行,华语',
    comment: 'Eason的演唱会总是那么精彩，每一首歌都唱到心里。',
    imageUrl: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop',
    photos: [
      { id: 6001, title: '演唱会现场', filePath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop' },
    ] as Photo[],
  },
  {
    id: 7,
    artist: 'Billie Eilish',
    venue: '台北小巨蛋',
    city: '台北',
    concertDate: '2024-06-15',
    rating: 9.2,
    tags: '流行,电子,欧美',
    comment: 'Billie的现场太有感染力了，年轻一代的偶像！',
    imageUrl: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop',
    photos: [
      { id: 7001, title: '演唱会现场', filePath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=300&h=300&fit=crop' },
      { id: 7002, title: '舞台设计', filePath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop' },
    ] as Photo[],
  },
  {
    id: 8,
    artist: '朴树',
    venue: '成都东郊记忆',
    city: '成都',
    concertDate: '2024-05-10',
    rating: 9.0,
    tags: '民谣,摇滚,华语',
    comment: '朴树的现场很真实，没有太多花哨，就是纯粹的音乐。',
    imageUrl: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop',
    photos: [
      { id: 8001, title: '演唱会现场', filePath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop' },
      { id: 8002, title: '舞台', filePath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=300&h=300&fit=crop' },
    ] as Photo[],
  },
]

function loadConcerts() {
  loading.value = true
  setTimeout(() => {
    let filtered = [...mockConcerts]
    
    // 标签筛选
    if (filterTag.value !== 'all') {
      filtered = filtered.filter(concert => 
        concert.tags && concert.tags.includes(filterTag.value)
      )
    }
    
    // 关键词搜索
    if (keyword.value) {
      const kw = keyword.value.toLowerCase()
      filtered = filtered.filter(concert => 
        concert.artist.toLowerCase().includes(kw) || 
        (concert.venue && concert.venue.toLowerCase().includes(kw)) ||
        (concert.city && concert.city.toLowerCase().includes(kw))
      )
    }
    
    // 计算总数
    total.value = filtered.length
    
    // 分页
    const start = (pageNo.value - 1) * pageSize.value
    const end = start + pageSize.value
    concertList.value = filtered.slice(start, end)
    
    loading.value = false
  }, 300)
}

function changeTag(value: string | 'all') {
  filterTag.value = value
  pageNo.value = 1
  loadConcerts()
}

function handleSearch() {
  pageNo.value = 1
  loadConcerts()
}

function handleImageError(id: number) {
  imageErrorMap[id] = true
}

function goDetail(concertId?: number) {
  if (!concertId) {
    // 暂时不实现详情页
    ElMessage.info('详情页开发中...')
    return
  }
  router.push(`/concert/${concertId}`)
}

function handleCardClick(event: MouseEvent, concert: any) {
  // 如果点击的是按钮或链接，不触发卡片点击
  const target = event.target as HTMLElement
  if (target.closest('button') || target.closest('a') || target.closest('.action-btn') || target.closest('.el-popconfirm')) {
    return
  }
  goDetail(concert.id)
}

function openCreateDialog() {
  ElMessage.info('添加功能开发中...')
}

function openEditDialog() {
  ElMessage.info('编辑功能开发中...')
}

function handleDelete(id: number) {
  const index = mockConcerts.findIndex(c => c.id === id)
  if (index > -1) {
    mockConcerts.splice(index, 1)
    loadConcerts()
    ElMessage.success('删除成功')
  }
}

// 照片相关功能
const photoUploadDialogVisible = ref(false)
const currentConcertId = ref<number | null>(null)
const uploadFileList = ref<any[]>([])
const uploading = ref(false)

function openPhotoUploadDialog(concertId: number) {
  currentConcertId.value = concertId
  photoUploadDialogVisible.value = true
  uploadFileList.value = []
}

function removeFile(index: number) {
  uploadFileList.value.splice(index, 1)
}

async function handlePhotoUpload() {
  if (!currentConcertId.value || uploadFileList.value.length === 0) {
    ElMessage.warning('请选择要上传的照片')
    return
  }

  try {
    uploading.value = true
    const concert = mockConcerts.find(c => c.id === currentConcertId.value)
    if (!concert) return

    // 模拟上传照片
    const uploadPromises = uploadFileList.value.map((uploadFile) => {
      return new Promise<void>((resolve) => {
        const file = uploadFile.raw || uploadFile
        const reader = new FileReader()
        reader.onload = (e) => {
          const photo: Photo = {
            id: Date.now() + Math.random(),
            title: file.name,
            filePath: e.target?.result as string,
            thumbnailPath: e.target?.result as string,
          }
          if (!concert.photos) {
            concert.photos = []
          }
          concert.photos.push(photo)
          resolve()
        }
        reader.readAsDataURL(file)
      })
    })

    await Promise.all(uploadPromises)

    // 实际项目中应该调用API
    // const formData = new FormData()
    // uploadFileList.value.forEach(uploadFile => {
    //   const file = uploadFile.raw || uploadFile
    //   formData.append('files', file)
    // })
    // formData.append('concertRecordId', currentConcertId.value.toString())
    // await uploadPhoto(formData)

    ElMessage.success('照片上传成功')
    photoUploadDialogVisible.value = false
    uploadFileList.value = []
    loadConcerts()
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
const allPhotosConcert = ref<any>(null)

function previewPhoto(photo: Photo) {
  // 找到包含这张照片的演唱会记录
  const concert = concertList.value.find(cr => cr.photos?.some((p: Photo) => p.id === photo.id))
  if (!concert || !concert.photos) return

  // 找到当前照片在列表中的索引
  const currentIndex = concert.photos.findIndex(p => p.id === photo.id)
  if (currentIndex === -1) return

  // 构建预览图片列表
  previewPhotoList.value = concert.photos.map(p => p.filePath)
  previewIndex.value = currentIndex
  photoPreviewVisible.value = true
}

function openAllPhotosDialog(concert: any) {
  allPhotosConcert.value = concert
  allPhotosList.value = concert.photos || []
  allPhotosDialogVisible.value = true
}

function previewPhotoFromAll(photo: Photo) {
  const currentIndex = allPhotosList.value.findIndex(p => p.id === photo.id)
  if (currentIndex === -1) return
  previewPhotoList.value = allPhotosList.value.map(p => p.filePath)
  previewIndex.value = currentIndex
  photoPreviewVisible.value = true
}

function handleDeletePhoto(concertId: number, photoId: number) {
  ElMessageBox.confirm('确定删除这张照片吗？', '提示', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const concert = mockConcerts.find(c => c.id === concertId)
    if (concert && concert.photos) {
      const index = concert.photos.findIndex(p => p.id === photoId)
      if (index > -1) {
        concert.photos.splice(index, 1)
        loadConcerts()
        ElMessage.success('删除成功')
      }
    }
    // 实际项目中应该调用API
    // await deletePhoto(photoId)
  }).catch(() => {})
}

onMounted(() => {
  loadConcerts()
})
</script>

<style scoped>
.concert-page {
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

/* 演唱会卡片进入动画 */
.concert-card-wrapper {
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
.concert-tags {
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

.concert-tag-item {
  animation: tagPop 0.3s ease-out both;
}

.concert-tag-item:nth-child(1) { animation-delay: 0.1s; }
.concert-tag-item:nth-child(2) { animation-delay: 0.2s; }
.concert-tag-item:nth-child(3) { animation-delay: 0.3s; }

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
.concert-actions {
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
.concert-poster-container {
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

.concert-poster {
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

