<template>
  <div class="travel-page">
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 sm:mb-8">
      <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-4 sm:mb-0 float-animation">旅游</h1>
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
            v-for="tag in tagOptions"
            :key="tag.value"
            :type="filterTag === tag.value ? 'primary' : 'default'"
            @click="changeTag(tag.value)"
            :class="filterTag === tag.value ? '!bg-[#00d4ff] !border-[#00d4ff]' : ''"
          >
            {{ tag.label }}
          </el-button>
        </div>
        <el-input
          v-model="keyword"
          placeholder="搜索地点名称…"
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

    <!-- 旅游卡片网格 -->
    <div
      v-loading="loading"
      class="min-h-[120px]"
    >
      <div
        v-if="travelList.length === 0 && !loading"
        class="py-10 text-center text-gray-400"
      >
        暂无记录，点击右上角「添加记录」开始你的旅行之旅。
      </div>

      <div
        v-else
        class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6"
      >
        <template
          v-for="travel in travelList"
          :key="travel.id"
        >
          <div class="flex flex-col gap-2">
            <!-- 旅游卡片 -->
            <div
              class="card-3d group cursor-pointer"
              @click="goDetail()"
            >
              <div class="card-3d-inner rounded-2xl overflow-hidden border border-white/10 hover:border-[#00d4ff]/30 transition-all duration-500 glow-effect bg-transparent">
                <!-- 图片 -->
                <div class="relative aspect-[4/3] overflow-hidden">
                  <img
                    v-if="travel.imageUrl && !imageErrorMap[travel.id]"
                    :src="travel.imageUrl"
                    :alt="travel.location"
                    class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
                    loading="lazy"
                    @error="handleImageError(travel.id)"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center bg-gradient-to-br from-[#00d4ff]/20 to-[#00ffcc]/10">
                    <el-icon :size="48" class="text-[#c3cfe2]/40">
                      <Location />
                    </el-icon>
                  </div>

                  <!-- 评分 -->
                  <div
                    v-if="travel.rating != null"
                    class="absolute bottom-3 left-3 flex items-center space-x-1 bg-black/60 backdrop-blur-sm px-2 py-1 rounded-lg"
                  >
                    <el-icon class="text-yellow-400"><StarFilled /></el-icon>
                    <span class="text-white text-sm font-semibold">{{ travel.rating }}</span>
                  </div>
                </div>

                <!-- 信息 -->
                <div class="p-4 flex flex-col gap-2 glass-effect">
                  <h3 class="text-lg font-semibold text-white line-clamp-1">{{ travel.location }}</h3>
                  <p class="text-xs text-gray-400">
                    <span v-if="travel.country">{{ travel.country }}</span>
                    <span v-if="travel.travelDate"> · {{ travel.travelDate }}</span>
                  </p>

                  <!-- 标签 -->
                  <div
                    v-if="travel.tags"
                    class="flex flex-wrap gap-2 mt-1"
                  >
                    <el-tag
                      v-for="tag in travel.tags.split(',')"
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
                      @click.stop="openEditDialog()"
                    >
                      编辑
                    </el-button>
                    <el-popconfirm
                      title="确定删除该记录？"
                      confirm-button-text="删除"
                      cancel-button-text="取消"
                      confirm-button-type="danger"
                      @confirm="handleDelete(travel.id)"
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

            <!-- 照片展示区域 -->
            <div
              v-if="travel.photos && travel.photos.length > 0"
              class="card-3d"
            >
              <div class="card-3d-inner rounded-2xl glass-effect p-4 transition-all">
                <div class="flex items-center justify-between mb-3">
                  <div class="flex items-center gap-2">
                    <el-icon class="text-[#00d4ff]/60">
                      <Picture />
                    </el-icon>
                    <span class="text-sm text-gray-300">照片 ({{ travel.photos.length }})</span>
                  </div>
                  <div class="flex gap-2">
                    <el-button
                      v-if="travel.photos.length > 6"
                      text
                      size="small"
                      class="!text-[#00d4ff] hover:!text-[#00ffcc]"
                      @click="openAllPhotosDialog(travel)"
                    >
                      查看全部
                    </el-button>
                    <el-button
                      text
                      size="small"
                      class="!text-[#00d4ff] hover:!text-[#00ffcc]"
                      @click="openPhotoUploadDialog(travel.id)"
                    >
                      添加照片
                    </el-button>
                  </div>
                </div>
                <div class="grid grid-cols-3 gap-2">
                  <div
                    v-for="photo in travel.photos.slice(0, 6)"
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
                      v-if="travel.photos.length > 6 && photo === travel.photos[5]"
                      class="absolute inset-0 bg-black/60 flex items-center justify-center text-white text-xs font-semibold cursor-pointer hover:bg-black/70 transition-colors"
                      @click.stop="openAllPhotosDialog(travel)"
                    >
                      +{{ travel.photos.length - 6 }}
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 评价卡片 -->
            <div
              v-if="travel.comment"
              class="card-3d"
            >
              <div class="card-3d-inner rounded-2xl glass-effect p-4 transition-all">
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
          @current-change="loadTravels"
          @size-change="loadTravels"
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
      :title="`${allPhotosTravel?.location || ''} - 全部照片 (${allPhotosList.length})`"
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Location, StarFilled, Search, ChatLineRound, Picture, Upload, Delete } from '@element-plus/icons-vue'
import { uploadPhoto, getPhotoList, deletePhoto, linkPhotoToTravel } from '@/api/photo'
import type { Photo } from '@/api/photo'

const loading = ref(false)
const travelList = ref<any[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(8)
const filterTag = ref<string | 'all'>('all')
const keyword = ref('')

const tagOptions = [
  { label: '全部', value: 'all' as const },
  { label: '国内', value: '国内' },
  { label: '国外', value: '国外' },
  { label: '自然风光', value: '自然风光' },
  { label: '历史文化', value: '历史文化' },
  { label: '城市', value: '城市' },
]

const imageErrorMap = reactive<Record<number, boolean>>({})

// 假数据 - 使用更可靠的图片URL
const mockTravels = [
  {
    id: 1,
    location: '北京',
    country: '中国',
    imageUrl: 'https://picsum.photos/800/600?random=1',
    travelDate: '2025-01-10',
    rating: 9.0,
    tags: '国内,历史文化,城市',
    comment: '故宫和天坛都太震撼了，感受到了深厚的历史底蕴。',
    photos: [
      { id: 101, title: '故宫', filePath: 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=300&h=300&fit=crop' },
      { id: 102, title: '天坛', filePath: 'https://images.unsplash.com/photo-1555993539-0c0c0c0c0c0?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1555993539-0c0c0c0c0c0?w=300&h=300&fit=crop' },
      { id: 103, title: '天安门', filePath: 'https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=300&h=300&fit=crop' },
      { id: 104, title: '颐和园', filePath: 'https://images.unsplash.com/photo-1547036967-23d11aacaee0?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1547036967-23d11aacaee0?w=300&h=300&fit=crop' },
    ] as Photo[],
  },
  {
    id: 2,
    location: '杭州西湖',
    country: '中国',
    imageUrl: 'https://picsum.photos/800/600?random=2',
    travelDate: '2024-12-25',
    rating: 9.5,
    tags: '国内,自然风光,城市',
    comment: '西湖的美景让人流连忘返，特别是断桥残雪。',
    photos: [
      { id: 201, title: '断桥残雪', filePath: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=300&h=300&fit=crop' },
      { id: 202, title: '三潭印月', filePath: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=300&fit=crop' },
      { id: 203, title: '雷峰塔', filePath: 'https://images.unsplash.com/photo-1514565131-fce0801e5785?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1514565131-fce0801e5785?w=300&h=300&fit=crop' },
    ] as Photo[],
  },
  {
    id: 3,
    location: '日本京都',
    country: '日本',
    imageUrl: 'https://picsum.photos/800/600?random=3',
    travelDate: '2024-11-15',
    rating: 9.8,
    tags: '国外,历史文化,城市',
    comment: '京都的古建筑和樱花季真的太美了，文化氛围浓厚。',
    photos: [
      { id: 301, title: '清水寺', filePath: 'https://images.unsplash.com/photo-1522383225653-ed111181a951?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1522383225653-ed111181a951?w=300&h=300&fit=crop' },
      { id: 302, title: '金阁寺', filePath: 'https://images.unsplash.com/photo-1464822759844-d150ad6d0f78?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1464822759844-d150ad6d0f78?w=300&h=300&fit=crop' },
      { id: 303, title: '樱花', filePath: 'https://images.unsplash.com/photo-1515542622106-78bda8ba0e5b?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1515542622106-78bda8ba0e5b?w=300&h=300&fit=crop' },
      { id: 304, title: '伏见稻荷大社', filePath: 'https://images.unsplash.com/photo-1446776653964-20c1d3a81b06?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1446776653964-20c1d3a81b06?w=300&h=300&fit=crop' },
      { id: 305, title: '岚山', filePath: 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=300&h=300&fit=crop' },
      { id: 306, title: '祗园', filePath: 'https://images.unsplash.com/photo-1514565131-fce0801e5785?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1514565131-fce0801e5785?w=300&h=300&fit=crop' },
      { id: 307, title: '二条城', filePath: 'https://images.unsplash.com/photo-1522383225653-ed111181a951?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1522383225653-ed111181a951?w=300&h=300&fit=crop' },
      { id: 308, title: '银阁寺', filePath: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=300&h=300&fit=crop' },
      { id: 309, title: '哲学之道', filePath: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=300&fit=crop' },
      { id: 310, title: '八坂神社', filePath: 'https://images.unsplash.com/photo-1514565131-fce0801e5785?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1514565131-fce0801e5785?w=300&h=300&fit=crop' },
      { id: 311, title: '鸭川', filePath: 'https://images.unsplash.com/photo-1522383225653-ed111181a951?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1522383225653-ed111181a951?w=300&h=300&fit=crop' },
      { id: 312, title: '京都塔', filePath: 'https://images.unsplash.com/photo-1464822759844-d150ad6d0f78?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1464822759844-d150ad6d0f78?w=300&h=300&fit=crop' },
      { id: 313, title: '花见小路', filePath: 'https://images.unsplash.com/photo-1515542622106-78bda8ba0e5b?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1515542622106-78bda8ba0e5b?w=300&h=300&fit=crop' },
      { id: 314, title: '三十三间堂', filePath: 'https://images.unsplash.com/photo-1446776653964-20c1d3a81b06?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1446776653964-20c1d3a81b06?w=300&h=300&fit=crop' },
    ] as Photo[],
  },
  {
    id: 4,
    location: '西藏拉萨',
    country: '中国',
    imageUrl: 'https://picsum.photos/800/600?random=4',
    travelDate: '2024-10-01',
    rating: 9.2,
    tags: '国内,自然风光,历史文化',
    comment: '布达拉宫的庄严和纳木错的纯净让人心灵震撼。',
    photos: [
      { id: 401, title: '布达拉宫', filePath: 'https://images.unsplash.com/photo-1464822759844-d150ad6d0f78?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1464822759844-d150ad6d0f78?w=300&h=300&fit=crop' },
      { id: 402, title: '纳木错', filePath: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=300&h=300&fit=crop' },
    ] as Photo[],
  },
  {
    id: 5,
    location: '巴黎',
    country: '法国',
    imageUrl: 'https://picsum.photos/800/600?random=5',
    travelDate: '2024-09-20',
    rating: 9.3,
    tags: '国外,历史文化,城市',
    comment: '埃菲尔铁塔和卢浮宫都是必去的地方，艺术之都名不虚传。',
    photos: [
      { id: 501, title: '埃菲尔铁塔', filePath: 'https://images.unsplash.com/photo-1514565131-fce0801e5785?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1514565131-fce0801e5785?w=300&h=300&fit=crop' },
      { id: 502, title: '卢浮宫', filePath: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=300&fit=crop' },
      { id: 503, title: '塞纳河', filePath: 'https://images.unsplash.com/photo-1522383225653-ed111181a951?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1522383225653-ed111181a951?w=300&h=300&fit=crop' },
    ] as Photo[],
  },
  {
    id: 6,
    location: '云南大理',
    country: '中国',
    imageUrl: 'https://picsum.photos/800/600?random=6',
    travelDate: '2024-08-15',
    rating: 9.0,
    tags: '国内,自然风光',
    comment: '苍山洱海的美景让人心旷神怡，是一个放松身心的好地方。',
    photos: [
      { id: 601, title: '洱海', filePath: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=300&h=300&fit=crop' },
      { id: 602, title: '苍山', filePath: 'https://images.unsplash.com/photo-1464822759844-d150ad6d0f78?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1464822759844-d150ad6d0f78?w=300&h=300&fit=crop' },
    ] as Photo[],
  },
  {
    id: 7,
    location: '冰岛',
    country: '冰岛',
    imageUrl: 'https://picsum.photos/800/600?random=7',
    travelDate: '2024-07-10',
    rating: 9.5,
    tags: '国外,自然风光',
    comment: '极光和蓝湖温泉是此生难忘的体验，大自然的鬼斧神工。',
    photos: [
      { id: 701, title: '极光', filePath: 'https://images.unsplash.com/photo-1446776653964-20c1d3a81b06?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1446776653964-20c1d3a81b06?w=300&h=300&fit=crop' },
      { id: 702, title: '蓝湖温泉', filePath: 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=300&h=300&fit=crop' },
      { id: 703, title: '黄金瀑布', filePath: 'https://images.unsplash.com/photo-1514565131-fce0801e5785?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1514565131-fce0801e5785?w=300&h=300&fit=crop' },
    ] as Photo[],
  },
  {
    id: 8,
    location: '成都',
    country: '中国',
    imageUrl: 'https://picsum.photos/800/600?random=8',
    travelDate: '2024-06-20',
    rating: 8.8,
    tags: '国内,城市',
    comment: '美食之都，火锅和串串都太棒了，还有大熊猫基地。',
    photos: [
      { id: 801, title: '大熊猫基地', filePath: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&h=600&fit=crop', thumbnailPath: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=300&fit=crop' },
    ] as Photo[],
  },
]

function loadTravels() {
  loading.value = true
  setTimeout(() => {
    let filtered = [...mockTravels]
    
    // 标签筛选
    if (filterTag.value !== 'all') {
      filtered = filtered.filter(travel => 
        travel.tags && travel.tags.includes(filterTag.value)
      )
    }
    
    // 关键词搜索
    if (keyword.value) {
      const kw = keyword.value.toLowerCase()
      filtered = filtered.filter(travel => 
        travel.location.toLowerCase().includes(kw) || 
        (travel.country && travel.country.toLowerCase().includes(kw))
      )
    }
    
    // 计算总数
    total.value = filtered.length
    
    // 分页
    const start = (pageNo.value - 1) * pageSize.value
    const end = start + pageSize.value
    travelList.value = filtered.slice(start, end)
    
    loading.value = false
  }, 300)
}

function changeTag(value: string | 'all') {
  filterTag.value = value
  pageNo.value = 1
  loadTravels()
}

function handleSearch() {
  pageNo.value = 1
  loadTravels()
}

function handleImageError(travelId: number) {
  imageErrorMap[travelId] = true
}

function goDetail() {
  // 暂时不实现详情页
  ElMessage.info('详情页开发中...')
}

function openCreateDialog() {
  ElMessage.info('添加功能开发中...')
}

function openEditDialog() {
  ElMessage.info('编辑功能开发中...')
}

function handleDelete(id: number) {
  const index = mockTravels.findIndex(t => t.id === id)
  if (index > -1) {
    mockTravels.splice(index, 1)
    loadTravels()
    ElMessage.success('删除成功')
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
    const travel = mockTravels.find(t => t.id === currentTravelId.value)
    if (!travel) return

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
          if (!travel.photos) {
            travel.photos = []
          }
          travel.photos.push(photo)
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
    // formData.append('travelRecordId', currentTravelId.value.toString())
    // await uploadPhoto(formData)

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
  // 找到包含这张照片的旅游记录
  const travel = travelList.value.find(tr => tr.photos?.some((p: Photo) => p.id === photo.id))
  if (!travel || !travel.photos) return

  // 找到当前照片在列表中的索引
  const currentIndex = travel.photos.findIndex(p => p.id === photo.id)
  if (currentIndex === -1) return

  // 构建预览图片列表
  previewPhotoList.value = travel.photos.map(p => p.filePath)
  previewIndex.value = currentIndex
  photoPreviewVisible.value = true
}

function openAllPhotosDialog(travel: any) {
  allPhotosTravel.value = travel
  allPhotosList.value = travel.photos || []
  allPhotosDialogVisible.value = true
}

function previewPhotoFromAll(photo: Photo) {
  const currentIndex = allPhotosList.value.findIndex(p => p.id === photo.id)
  if (currentIndex === -1) return
  previewPhotoList.value = allPhotosList.value.map(p => p.filePath)
  previewIndex.value = currentIndex
  photoPreviewVisible.value = true
}

function handleDeletePhoto(travelId: number, photoId: number) {
  ElMessageBox.confirm('确定删除这张照片吗？', '提示', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const travel = mockTravels.find(t => t.id === travelId)
    if (travel && travel.photos) {
      const index = travel.photos.findIndex(p => p.id === photoId)
      if (index > -1) {
        travel.photos.splice(index, 1)
        loadTravels()
        ElMessage.success('删除成功')
      }
    }
    // 实际项目中应该调用API
    // await deletePhoto(photoId)
  }).catch(() => {})
}

onMounted(() => {
  loadTravels()
})
</script>

<style scoped>
.travel-page {
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
</style>

