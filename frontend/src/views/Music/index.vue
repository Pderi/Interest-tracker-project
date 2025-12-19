<template>
  <div class="music-page">
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 sm:mb-8">
      <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-4 sm:mb-0 float-animation">音乐</h1>
      <el-button 
        type="primary" 
        :icon="Plus" 
        @click="handleAdd"
        class="!bg-[#00d4ff] !border-[#00d4ff] hover:!bg-[#00ffcc] hover:!border-[#00ffcc] !text-[#1a1a2e] shadow-lg shadow-[#00d4ff]/30 hover:shadow-[#00d4ff]/50 transition-all glow-effect font-semibold"
      >
        添加记录
      </el-button>
    </div>

    <!-- 音乐列表 -->
    <div class="space-y-4">
      <div
        v-for="music in musicList"
        :key="music.id"
        class="card-3d group"
      >
        <div class="card-3d-inner rounded-2xl glass-effect border border-white/10 hover:border-[#00d4ff]/30 transition-all duration-500 overflow-hidden glow-effect">
          <div class="flex items-center p-4 sm:p-6">
            <!-- 封面 -->
              <div class="relative w-20 h-20 sm:w-24 sm:h-24 rounded-xl overflow-hidden bg-gradient-to-br from-[#00d4ff]/20 to-[#00ffcc]/10 flex-shrink-0 mr-4 sm:mr-6 rotate-glow">
              <img 
                v-if="music.cover && !music.imageError" 
                :src="music.cover" 
                :alt="music.title"
                class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
                loading="lazy"
                @error="handleImageError(music.id)"
                @load="handleImageLoad(music.id)"
              />
              <div v-else class="w-full h-full flex items-center justify-center">
                <el-icon :size="32" class="text-[#c3cfe2]/40">
                  <Headset />
                </el-icon>
              </div>
            </div>

            <!-- 信息 -->
            <div class="flex-1 min-w-0">
              <h3 class="text-lg sm:text-xl font-semibold text-white mb-1 line-clamp-1">{{ music.title }}</h3>
              <p class="text-sm sm:text-base text-[#00d4ff]/70 mb-2">{{ music.artist }}</p>
              <p class="text-xs sm:text-sm text-[#00d4ff]/50 mb-3">{{ music.year }} · {{ music.trackCount }} 首</p>
              
              <!-- 标签 -->
              <div class="flex flex-wrap gap-2">
                <el-tag
                  v-for="tag in music.tags"
                  :key="tag"
                  size="small"
                  effect="plain"
                  class="!border-[#00d4ff]/30 !text-[#00d4ff] flicker"
                >
                  {{ tag }}
                </el-tag>
              </div>
            </div>

            <!-- 播放按钮 -->
            <div class="ml-4 flex-shrink-0">
              <el-button
                circle
                size="large"
                class="!bg-[#00d4ff]/20 !border-[#00d4ff]/50 hover:!bg-[#00d4ff]/30 hover:!border-[#00d4ff] transition-all glow-effect rotate-glow"
              >
                <el-icon class="text-[#00d4ff]"><VideoPlay /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Plus, Headset, VideoPlay } from '@element-plus/icons-vue'

// 假数据 - 专辑列表
const musicList = ref([
  {
    id: 1,
    title: 'A Night at the Opera',
    artist: 'Queen',
    year: '1975',
    trackCount: 12,
    cover: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop',
    tags: ['摇滚', '经典', '史诗'],
    imageError: false,
  },
  {
    id: 2,
    title: 'After Hours',
    artist: 'The Weeknd',
    year: '2020',
    trackCount: 14,
    cover: 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=300&h=300&fit=crop',
    tags: ['流行', '电子', 'R&B'],
    imageError: false,
  },
  {
    id: 3,
    title: '÷ (Divide)',
    artist: 'Ed Sheeran',
    year: '2017',
    trackCount: 16,
    cover: 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=300&h=300&fit=crop',
    tags: ['流行', '民谣', '舞曲'],
    imageError: false,
  },
  {
    id: 4,
    title: '21',
    artist: 'Adele',
    year: '2011',
    trackCount: 11,
    cover: 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300&h=300&fit=crop',
    tags: ['流行', '灵魂', '抒情'],
    imageError: false,
  },
  {
    id: 5,
    title: 'Led Zeppelin IV',
    artist: 'Led Zeppelin',
    year: '1971',
    trackCount: 8,
    cover: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop',
    tags: ['摇滚', '经典', '前卫'],
    imageError: false,
  },
  {
    id: 6,
    title: 'Thriller',
    artist: 'Michael Jackson',
    year: '1982',
    trackCount: 9,
    cover: 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300&h=300&fit=crop',
    tags: ['流行', '放克', '舞曲'],
    imageError: false,
  },
])

function handleAdd() {
  console.log('添加专辑记录')
}

function handleImageError(musicId: number) {
  const music = musicList.value.find(m => m.id === musicId)
  if (music) {
    music.imageError = true
  }
}

function handleImageLoad(musicId: number) {
  const music = musicList.value.find(m => m.id === musicId)
  if (music) {
    music.imageError = false
  }
}
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
</style>
