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

    <div
      v-else-if="detail"
      class="grid grid-cols-1 lg:grid-cols-[260px,1fr] gap-8 glass-effect rounded-2xl border border-white/10 p-6 sm:p-8"
    >
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
        <div class="text-sm text-gray-400">
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
              <el-input
                v-model="editForm.tags"
                size="small"
                placeholder="用逗号分隔，例如：自然,历史（标签暂不支持编辑）"
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
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Location, Loading } from '@element-plus/icons-vue'
import { getTravelDetail, updateTravelRecord } from '@/api/travel'
import type { TravelDetail } from '@/types/api'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const detail = ref<TravelDetail | null>(null)

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

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.travel-detail {
  min-height: 100%;
}
</style>

