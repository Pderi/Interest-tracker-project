<template>
  <div class="photo-page">
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 sm:mb-8">
      <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-4 sm:mb-0 float-animation">摄影</h1>
      <AnimatedButton variant="primary" @click="handleUpload">
        <el-icon><Plus /></el-icon>
        上传照片
      </AnimatedButton>
    </div>

    <!-- 筛选栏 -->
    <div class="mb-6 flex flex-wrap gap-3">
      <AnimatedButton
        v-for="tag in tagOptions"
        :key="tag.value"
        :variant="filterTag === tag.value ? 'primary' : 'secondary'"
        size="small"
        @click="filterTag = tag.value"
      >
        {{ tag.label }}
      </AnimatedButton>
    </div>

    <!-- 照片网格 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 sm:gap-6">
      <AnimatedCard
        v-for="(photo, index) in filteredPhotos"
        :key="photo.id"
        variant="3d"
        class="photo-card-wrapper group"
        :style="{ animationDelay: `${index * 50}ms` }"
      >
        <div class="rounded-2xl overflow-hidden">
          <!-- 照片 -->
          <div class="relative aspect-square overflow-hidden bg-gradient-to-br from-[#c3cfe2]/20 to-[#f5f7fa]/10">
            <img 
              v-if="photo.url && !photo.imageError" 
              :src="photo.url" 
              :alt="photo.title"
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
            <div class="absolute top-3 left-3 right-3 flex flex-wrap gap-2 photo-tags">
              <AnimatedTag
                v-for="tag in photo.tags.slice(0, 2)"
                :key="tag"
                variant="glow"
                size="small"
                class="photo-tag-item"
              >
                {{ tag }}
              </AnimatedTag>
            </div>

            <!-- 拍摄时间 -->
            <div class="absolute bottom-3 left-3 right-3">
              <div class="bg-black/60 backdrop-blur-sm px-2 py-1 rounded-lg">
                <p class="text-white text-xs">{{ photo.shootTime }}</p>
              </div>
            </div>
          </div>

          <!-- 信息 -->
          <div class="p-4">
            <h3 class="text-lg font-semibold text-white mb-1 line-clamp-1">{{ photo.title }}</h3>
            <p class="text-sm text-[#00d4ff]/70 mb-3 line-clamp-2">{{ photo.description }}</p>
            
            <!-- 设备信息 -->
            <div v-if="photo.device" class="flex items-center text-xs text-gray-400 mb-2">
              <el-icon class="mr-1"><Camera /></el-icon>
              <span>{{ photo.device }}</span>
            </div>

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
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Plus, Picture, Camera, Location } from '@element-plus/icons-vue'
import { 
  AnimatedButton, 
  AnimatedCard,
  AnimatedTag
} from '@/components/uiverse'

// 假数据
const photos = ref([
  {
    id: 1,
    title: '日落时分',
    description: '在海边拍摄的美丽日落，天空被染成了橙红色',
    url: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=500&h=500&fit=crop&q=80',
    tags: ['风景', '日落', '海边'],
    shootTime: '2024-01-15 18:30',
    device: 'Canon EOS R5',
    location: '三亚',
    imageError: false,
  },
  {
    id: 2,
    title: '城市夜景',
    description: '繁华都市的夜晚，霓虹灯闪烁',
    url: 'https://images.unsplash.com/photo-1514565131-fce0801e5785?w=500&h=500&fit=crop&q=80',
    tags: ['城市', '夜景', '建筑'],
    shootTime: '2024-01-10 20:15',
    device: 'Sony A7III',
    location: '上海',
    imageError: false,
  },
  {
    id: 3,
    title: '樱花盛开',
    description: '春天的樱花，粉色的花瓣飘落',
    url: 'https://images.unsplash.com/photo-1522383225653-ed111181a951?w=500&h=500&fit=crop&q=80',
    tags: ['自然', '花卉', '春天'],
    shootTime: '2024-03-20 14:20',
    device: 'Nikon D850',
    location: '北京',
    imageError: false,
  },
  {
    id: 4,
    title: '雪山之巅',
    description: '高耸入云的雪山，白雪皑皑',
    url: 'https://images.unsplash.com/photo-1464822759844-d150ad6d0f78?w=500&h=500&fit=crop&q=80',
    tags: ['风景', '雪山', '自然'],
    shootTime: '2024-02-05 10:00',
    device: 'Canon EOS R5',
    location: '西藏',
    imageError: false,
  },
  {
    id: 5,
    title: '街头人文',
    description: '捕捉生活中的美好瞬间',
    url: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&h=500&fit=crop&q=80',
    tags: ['人文', '街头', '生活'],
    shootTime: '2024-01-25 16:45',
    device: 'Fujifilm X-T4',
    location: '成都',
    imageError: false,
  },
  {
    id: 6,
    title: '星空银河',
    description: '远离城市光污染，拍摄的壮丽银河',
    url: 'https://images.unsplash.com/photo-1446776653964-20c1d3a81b06?w=500&h=500&fit=crop&q=80',
    tags: ['星空', '银河', '天文'],
    shootTime: '2024-01-01 23:30',
    device: 'Sony A7III',
    location: '内蒙古',
    imageError: false,
  },
  {
    id: 7,
    title: '美食特写',
    description: '精心制作的美食，色彩丰富',
    url: 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=500&h=500&fit=crop&q=80',
    tags: ['美食', '特写', '生活'],
    shootTime: '2024-01-20 12:00',
    device: 'Canon EOS R5',
    location: '杭州',
    imageError: false,
  },
  {
    id: 8,
    title: '古建筑',
    description: '传统建筑的精致细节',
    url: 'https://images.unsplash.com/photo-1515542622106-78bda8ba0e5b?w=500&h=500&fit=crop&q=80',
    tags: ['建筑', '传统', '文化'],
    shootTime: '2024-02-10 15:30',
    device: 'Nikon D850',
    location: '苏州',
    imageError: false,
  },
])

const tagOptions = [
  { label: '全部', value: 'all' },
  { label: '风景', value: '风景' },
  { label: '城市', value: '城市' },
  { label: '自然', value: '自然' },
  { label: '人文', value: '人文' },
  { label: '美食', value: '美食' },
]

const filterTag = ref('all')

const filteredPhotos = computed(() => {
  if (filterTag.value === 'all') {
    return photos.value
  }
  return photos.value.filter(p => p.tags.includes(filterTag.value))
})

function handleUpload() {
  console.log('上传照片')
}

function handleImageError(photoId: number) {
  const photo = photos.value.find(p => p.id === photoId)
  if (photo) {
    photo.imageError = true
  }
}

function handleImageLoad(photoId: number) {
  const photo = photos.value.find(p => p.id === photoId)
  if (photo) {
    photo.imageError = false
  }
}
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
</style>

