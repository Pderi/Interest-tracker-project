<template>
  <div class="music-detail min-h-full">
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

    <div
      v-else-if="detail"
      class="grid grid-cols-1 lg:grid-cols-[260px,1fr] gap-8 glass-effect rounded-2xl border border-white/10 p-6 sm:p-8"
    >
      <!-- 左侧封面 -->
      <div class="flex flex-col items-center gap-4">
        <div class="w-52 h-52 rounded-2xl overflow-hidden bg-black/40 border border-white/15 shadow-xl">
          <img
            v-if="detail.album.coverUrl"
            :src="detail.album.coverUrl"
            :alt="detail.album.title"
            class="w-full h-full object-cover"
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
        <div class="text-sm text-gray-400">
          <div v-if="detail.record?.listenDate">
            听歌日期：{{ detail.record.listenDate }}
          </div>
          <div v-if="detail.album.releaseYear">
            发行年份：{{ detail.album.releaseYear }}
          </div>
          <div v-if="detail.album.totalTracks">
            总曲目数：{{ detail.album.totalTracks }} 首
          </div>
          <div v-if="detail.album.duration">
            总时长：{{ formatDuration(detail.album.duration) }}
          </div>
          <div v-if="detail.record?.listenCount">
            听歌次数：{{ detail.record.listenCount }} 次
          </div>
        </div>
      </div>

      <!-- 右侧信息 -->
      <div class="flex flex-col gap-4">
        <div>
          <h1 class="text-2xl sm:text-3xl font-bold text-white mb-2">
            {{ detail.album.title }}
          </h1>
          <p class="text-lg text-[#00d4ff]/80 mb-2">
            {{ detail.album.artist }}
          </p>
          <p
            v-if="detail.album.genre"
            class="text-sm text-[#00d4ff]/60 mb-1"
          >
            {{ detail.album.genre }}
          </p>
          <p
            v-if="detail.album.description"
            class="text-sm text-gray-300 leading-relaxed mt-2"
          >
            {{ detail.album.description }}
          </p>
        </div>

        <el-divider />

        <!-- 听歌记录 -->
        <div v-if="detail.record">
          <h2 class="text-lg font-semibold text-white mb-3">
            我的记录
          </h2>
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div class="space-y-2">
              <div class="text-sm text-gray-400">状态</div>
              <el-select
                v-model="editForm.listenStatus"
                size="small"
                class="w-40"
              >
                <el-option :value="1" label="想听" />
                <el-option :value="2" label="在听" />
                <el-option :value="3" label="已听" />
                <el-option :value="4" label="弃听" />
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
              <div class="text-sm text-gray-400">听歌日期</div>
              <el-date-picker
                v-model="editForm.listenDate"
                type="date"
                placeholder="选择日期"
                size="small"
                value-format="YYYY-MM-DD"
                class="w-full sm:w-40"
              />
            </div>
            <div class="space-y-2">
              <div class="text-sm text-gray-400">听歌次数</div>
              <el-input-number
                v-model="editForm.listenCount"
                :min="0"
                size="small"
                controls-position="right"
                class="w-full sm:w-40"
              />
            </div>
            <div class="space-y-2 sm:col-span-2">
              <div class="text-sm text-gray-400">标签</div>
              <el-input
                v-model="editForm.tags"
                size="small"
                placeholder="用逗号分隔，例如：摇滚,经典"
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
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Headset, Loading } from '@element-plus/icons-vue'
import { getAlbumDetail, updateAlbumRecord } from '@/api/music'
import type { AlbumDetail } from '@/types/api'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const detail = ref<AlbumDetail | null>(null)

const editForm = reactive<{
  recordId?: number
  listenStatus?: number
  personalRating?: number
  listenDate?: string
  listenCount?: number
  tags?: string
  comment?: string
}>({})

function formatDuration(seconds: number) {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  if (minutes > 0) {
    return `${minutes}分${remainingSeconds}秒`
  }
  return `${remainingSeconds}秒`
}

function goBack() {
  router.back()
}

async function fetchDetail() {
  const id = Number(route.params.id)
  if (!id) return
  try {
    loading.value = true
    const res = await getAlbumDetail(id)
    detail.value = res.data
    if (res.data.record) {
      editForm.recordId = res.data.record.id
      editForm.listenStatus = res.data.record.listenStatus
      editForm.personalRating = res.data.record.personalRating
      editForm.listenDate = res.data.record.listenDate
      editForm.listenCount = res.data.record.listenCount
      editForm.tags = res.data.record.tags
      editForm.comment = res.data.record.comment
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
    await updateAlbumRecord(editForm.recordId, {
      id: editForm.recordId,
      listenStatus: editForm.listenStatus,
      personalRating: editForm.personalRating,
      listenDate: editForm.listenDate,
      listenCount: editForm.listenCount,
      tags: editForm.tags,
      comment: editForm.comment,
    })
    ElMessage.success('已保存')
    fetchDetail()
  } catch {
    // 已统一处理
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.music-detail {
  min-height: 100%;
}
</style>

