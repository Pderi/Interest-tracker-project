<template>
  <div class="movie-detail min-h-full">
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
      <!-- 左侧海报 -->
      <div class="flex flex-col items-center gap-4">
        <div class="w-52 h-78 rounded-2xl overflow-hidden bg-black/40 border border-white/15 shadow-xl">
          <img
            v-if="detail.movie.posterUrl"
            :src="detail.movie.posterUrl"
            :alt="detail.movie.title"
            class="w-full h-full object-cover"
          />
          <div
            v-else
            class="w-full h-full flex items-center justify-center text-gray-500"
          >
            <el-icon :size="48">
              <VideoPlay />
            </el-icon>
          </div>
        </div>
        <div class="text-sm text-gray-400">
          <div>类型：{{ formatType(detail.movie.type) }}</div>
          <div v-if="detail.record?.watchDate">
            观看日期：{{ detail.record.watchDate }}
          </div>
          <div v-if="detail.movie.releaseYear">
            年份：{{ detail.movie.releaseYear }}
          </div>
          <div v-if="detail.movie.duration">
            时长：{{ detail.movie.duration }} 分钟
          </div>
        </div>
      </div>

      <!-- 右侧信息 -->
      <div class="flex flex-col gap-4">
        <div>
          <h1 class="text-2xl sm:text-3xl font-bold text-white mb-2">
            {{ detail.movie.title }}
          </h1>
          <p
            v-if="detail.movie.genre"
            class="text-sm text-[#00d4ff]/80 mb-1"
          >
            {{ detail.movie.genre }}
          </p>
          <p
            v-if="detail.movie.description"
            class="text-sm text-gray-300 leading-relaxed"
          >
            {{ detail.movie.description }}
          </p>
        </div>

        <el-divider />

        <!-- 观看记录 -->
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
                <el-option :value="2" label="在看" />
                <el-option :value="3" label="已看" />
                <el-option :value="4" label="弃剧" />
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
              <div class="text-sm text-gray-400">观看日期</div>
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
              <div class="text-sm text-gray-400">标签</div>
              <el-input
                v-model="editForm.tags"
                size="small"
                placeholder="用逗号分隔，例如：科幻,经典"
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
import { VideoPlay, Loading } from '@element-plus/icons-vue'
import { getMovieDetail, updateMovieRecord } from '@/api/movie'
import type { MovieDetail } from '@/types/api'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const detail = ref<MovieDetail | null>(null)

const editForm = reactive<{
  recordId?: number
  watchStatus?: number
  personalRating?: number
  watchDate?: string
  tags?: string
  comment?: string
}>({})

function formatType(type: number) {
  return type === 1 ? '电影' : type === 2 ? '电视剧' : '未知'
}

function goBack() {
  router.back()
}

async function fetchDetail() {
  const id = Number(route.params.id)
  if (!id) return
  try {
    loading.value = true
    const res = await getMovieDetail(id)
    detail.value = res.data
    if (res.data.record) {
      editForm.recordId = res.data.record.id
      editForm.watchStatus = res.data.record.watchStatus
      editForm.personalRating = res.data.record.personalRating
      editForm.watchDate = res.data.record.watchDate
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
    await updateMovieRecord(editForm.recordId, {
      id: editForm.recordId,
      watchStatus: editForm.watchStatus,
      personalRating: editForm.personalRating,
      watchDate: editForm.watchDate,
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
.movie-detail {
  min-height: 100%;
}
</style>


