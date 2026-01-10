<template>
  <div class="match-page">
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 sm:mb-8">
      <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-4 sm:mb-0 float-animation">球赛</h1>
      <AnimatedButton variant="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加记录
      </AnimatedButton>
    </div>

    <!-- 筛选栏 -->
    <div class="mb-6 flex flex-wrap gap-3">
      <AnimatedButton
        v-for="type in typeOptions"
        :key="type.value"
        :variant="filterType === type.value ? 'primary' : 'secondary'"
        size="small"
        @click="changeType(type.value)"
      >
        <span>{{ type.label }}</span>
        <span
          v-if="type.value !== 'all' && typeCounts[type.value]"
          class="ml-1.5 px-1.5 py-0.5 rounded text-xs font-medium"
          :class="filterType === type.value 
            ? 'bg-white/20 text-white' 
            : 'bg-white/10 text-gray-300'"
        >
          {{ typeCounts[type.value] }}
        </span>
      </AnimatedButton>
    </div>

    <!-- 比赛记录列表 -->
    <div class="min-h-[120px] relative">
      <!-- 骨架屏加载 -->
      <div v-if="loading" class="space-y-4 sm:space-y-6">
        <SkeletonCard v-for="i in 3" :key="`skeleton-${i}`" />
      </div>
      
      <EmptyState
        v-else-if="filteredMatches.length === 0"
        title="暂无记录"
        description="点击右上角「添加记录」开始你的观赛之旅"
      >
        <template #icon>
          <el-icon :size="64" class="text-gray-500">
            <Trophy />
          </el-icon>
        </template>
        <template #action>
          <AnimatedButton variant="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加第一条记录
          </AnimatedButton>
        </template>
      </EmptyState>

      <div v-else class="space-y-4 sm:space-y-6">
        <AnimatedCard
          v-for="(match, index) in filteredMatches"
          :key="match.recordId"
          variant="glass"
          class="match-card-wrapper group"
          :style="{ animationDelay: `${index * 100}ms` }"
        >
          <div class="rounded-2xl overflow-hidden relative match-card">
            <!-- 背景装饰 -->
            <div class="absolute inset-0 opacity-30 pointer-events-none">
              <div class="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-[#00d4ff]/20 to-transparent rounded-full blur-3xl"></div>
              <div class="absolute bottom-0 left-0 w-24 h-24 bg-gradient-to-tr from-[#00ffcc]/20 to-transparent rounded-full blur-2xl"></div>
            </div>

            <div class="relative p-4 sm:p-6">
              <!-- 比赛信息头部 -->
              <div class="flex items-center justify-between mb-4 flex-wrap gap-2">
                <div class="flex items-center gap-2 flex-wrap min-w-0">
                  <el-tag 
                    :type="getTypeTag(match.matchType) as any" 
                    size="small" 
                    effect="dark"
                    class="backdrop-blur-md shadow-lg !px-2 !py-1"
                  >
                    {{ getTypeLabel(match.matchType) }}
                  </el-tag>
                  <div class="flex items-center gap-1 text-xs text-gray-400 flex-shrink-0">
                    <el-icon class="text-[#00d4ff]/60"><Calendar /></el-icon>
                    <span>{{ formatDateTime(match.matchDate) }}</span>
                  </div>
                </div>
                <div class="flex gap-2 match-actions">
                  <AnimatedButton
                    variant="outline"
                    size="small"
                    @click.stop="openEditDialog(match)"
                    class="action-btn edit-btn"
                  >
                    编辑
                  </AnimatedButton>
                  <el-popconfirm
                    title="确定删除该记录？"
                    confirm-button-text="删除"
                    cancel-button-text="取消"
                    confirm-button-type="danger"
                    @confirm="handleDelete(match.recordId)"
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

              <!-- 比分区域 -->
              <div class="relative mb-5">
                <div class="flex items-center justify-center p-6 sm:p-8 bg-gradient-to-br from-[#00d4ff]/15 via-[#00d4ff]/10 to-[#00ffcc]/15 rounded-2xl border border-[#00d4ff]/30 backdrop-blur-md shadow-lg shadow-[#00d4ff]/20 relative overflow-hidden">
                  <!-- 比分背景装饰 -->
                  <div class="absolute inset-0 opacity-20">
                    <div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-40 h-40 bg-gradient-to-r from-[#00d4ff] to-[#00ffcc] rounded-full blur-3xl"></div>
                  </div>
                  
                  <div class="relative flex items-center justify-center w-full">
                    <!-- 主队 -->
                    <div class="flex-1 text-center relative z-10">
                      <div class="text-lg sm:text-xl font-bold text-white mb-2 line-clamp-2 drop-shadow-lg">{{ match.homeTeamName }}</div>
                      <div v-if="match.homeScore !== null" class="text-5xl sm:text-6xl font-black text-white mb-1 drop-shadow-lg">
                        {{ match.homeScore }}
                      </div>
                      <div v-else class="text-3xl sm:text-4xl font-bold text-[#00d4ff]/60">—</div>
                      <div class="text-xs text-gray-400 mt-1">主队</div>
                    </div>

                    <!-- 比分分隔符 -->
                    <div class="mx-3 sm:mx-6 flex-shrink-0 relative z-10">
                      <div class="text-4xl sm:text-5xl font-black gradient-text leading-none drop-shadow-lg" v-if="match.homeScore !== null">:</div>
                      <div class="text-2xl sm:text-3xl font-bold text-[#00d4ff]/60" v-else>VS</div>
                    </div>

                    <!-- 客队 -->
                    <div class="flex-1 text-center relative z-10">
                      <div class="text-lg sm:text-xl font-bold text-white mb-2 line-clamp-2 drop-shadow-lg">{{ match.awayTeamName }}</div>
                      <div v-if="match.awayScore !== null" class="text-5xl sm:text-6xl font-black text-white mb-1 drop-shadow-lg">
                        {{ match.awayScore }}
                      </div>
                      <div v-else class="text-3xl sm:text-4xl font-bold text-[#00d4ff]/60">—</div>
                      <div class="text-xs text-gray-400 mt-1">客队</div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 观赛信息 -->
              <div class="flex flex-wrap items-center gap-3 sm:gap-4 text-sm mb-4">
                <div class="flex items-center gap-2 px-3 py-1.5 bg-white/5 rounded-lg border border-white/10 backdrop-blur-sm">
                  <el-icon class="text-[#00d4ff]"><Location /></el-icon>
                  <span class="text-gray-300">{{ getWatchTypeLabel(match.watchType) }}</span>
                </div>
                <div v-if="match.venue" class="flex items-center gap-2 px-3 py-1.5 bg-white/5 rounded-lg border border-white/10 backdrop-blur-sm">
                  <el-icon class="text-[#00ffcc]"><MapLocation /></el-icon>
                  <span class="text-gray-300 line-clamp-1">{{ match.venue }}</span>
                </div>
              </div>

              <!-- 备注 -->
              <div v-if="match.notes" class="mt-4 p-4 bg-gradient-to-br from-white/5 to-white/3 rounded-xl border border-[#00d4ff]/20 backdrop-blur-sm">
                <div class="flex items-start gap-3">
                  <el-icon class="text-[#00d4ff]/60 mt-0.5 flex-shrink-0">
                    <ChatLineRound />
                  </el-icon>
                  <div class="flex-1 min-w-0">
                    <p class="text-sm text-gray-300 leading-relaxed">
                      <span class="text-[#00d4ff]/60">"</span>{{ match.notes }}<span class="text-[#00d4ff]/60">"</span>
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </AnimatedCard>
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
          :page-sizes="[10, 20, 50]"
          @current-change="loadMatches"
          @size-change="loadMatches"
          class="animated-pagination"
        />
      </div>
    </div>

    <!-- 新建 / 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '添加比赛记录' : '编辑比赛记录'"
      width="600px"
      destroy-on-close
      class="match-dialog"
    >
      <div class="match-form-wrapper glass-effect">
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
          class="match-form"
        >
          <el-form-item
            label="主队名称"
            prop="homeTeamName"
          >
            <el-input
              v-model="form.homeTeamName"
              placeholder="请输入主队名称"
            />
          </el-form-item>

          <el-form-item
            label="客队名称"
            prop="awayTeamName"
          >
            <el-input
              v-model="form.awayTeamName"
              placeholder="请输入客队名称"
            />
          </el-form-item>

          <el-form-item
            label="比赛日期"
            prop="matchDate"
          >
            <el-date-picker
              v-model="form.matchDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              class="w-full"
            />
          </el-form-item>

          <el-form-item label="主队得分">
            <el-input-number
              v-model="form.homeScore"
              :min="0"
              controls-position="right"
              class="w-full"
              placeholder="主队得分"
            />
          </el-form-item>

          <el-form-item label="客队得分">
            <el-input-number
              v-model="form.awayScore"
              :min="0"
              controls-position="right"
              class="w-full"
              placeholder="客队得分"
            />
          </el-form-item>

          <el-form-item label="比赛类型">
            <el-select v-model="form.matchType" class="w-full">
              <el-option :value="1" label="联赛" />
              <el-option :value="2" label="杯赛" />
              <el-option :value="3" label="友谊赛" />
            </el-select>
          </el-form-item>

          <el-form-item label="观赛方式">
            <el-select v-model="form.watchType" class="w-full">
              <el-option :value="1" label="现场" />
              <el-option :value="2" label="直播" />
              <el-option :value="3" label="回放" />
            </el-select>
          </el-form-item>

          <el-form-item label="比赛场地">
            <el-input
              v-model="form.venue"
              placeholder="请输入比赛场地"
            />
          </el-form-item>

          <el-form-item label="备注">
            <el-input
              v-model="form.notes"
              type="textarea"
              :rows="3"
              placeholder="请输入备注"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <AnimatedButton variant="secondary" @click="dialogVisible = false">
            取消
          </AnimatedButton>
          <AnimatedButton
            variant="primary"
            :loading="submitLoading"
            @click="handleSubmit"
          >
            确认
          </AnimatedButton>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { ElMessage, ElDialog, ElForm, ElFormItem, ElInput, ElInputNumber, ElButton, ElSelect, ElDatePicker, FormInstance, FormRules } from 'element-plus'
import { Plus, Trophy, Location, MapLocation, Calendar, ChatLineRound } from '@element-plus/icons-vue'
import { getMatchPage, createMatch, updateMatch, deleteMatch } from '@/api/match'
import type { MatchPageItem, MatchCreateReq, MatchUpdateReq } from '@/types/api'
import dayjs from 'dayjs'
import { 
  AnimatedButton, 
  AnimatedCard, 
  EmptyState,
  SkeletonCard
} from '@/components/uiverse'

const loading = ref(false)
const matchList = ref<MatchPageItem[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(10)
const filterType = ref<number | 'all'>('all')
const typeCounts = ref<Record<number, number>>({})

const typeOptions = [
  { label: '全部', value: 'all' as const },
  { label: '联赛', value: 1 },
  { label: '杯赛', value: 2 },
  { label: '友谊赛', value: 3 },
]

async function loadMatches() {
  loading.value = true
  try {
    const params: any = {
      pageNo: pageNo.value,
      pageSize: pageSize.value,
    }
    if (filterType.value !== 'all') {
      params.matchType = filterType.value
    }
    const res = await getMatchPage(params)
    matchList.value = res.data.page?.list || []
    total.value = res.data.page?.total || 0
    typeCounts.value = res.data.typeCounts || {}
  } catch (e) {
    // 已统一处理
  } finally {
    loading.value = false
  }
}

function changeType(value: number | 'all') {
  filterType.value = value
  pageNo.value = 1
  loadMatches()
}

function getTypeLabel(type: number) {
  const map: Record<number, string> = {
    1: '联赛',
    2: '杯赛',
    3: '友谊赛',
  }
  return map[type] || '-'
}

function getTypeTag(type: number) {
  const map: Record<number, string> = {
    1: 'success',
    2: 'warning',
    3: 'info',
  }
  return map[type] || 'info'
}

function getWatchTypeLabel(type: number) {
  const map: Record<number, string> = {
    1: '现场',
    2: '直播',
    3: '回放',
  }
  return map[type] || '-'
}

function formatDateTime(dateTime: string) {
  return dayjs(dateTime).format('YYYY-MM-DD')
}

// ========== 新建 / 编辑表单 ==========
type DialogMode = 'create' | 'edit'

const dialogVisible = ref(false)
const dialogMode = ref<DialogMode>('create')
const formRef = ref<FormInstance>()

const form = reactive<{
  recordId?: number
  homeTeamName: string
  awayTeamName: string
  matchDate: string | null
  homeScore: number | null
  awayScore: number | null
  matchType: number | null
  watchType: number | null
  venue: string
  notes: string
}>({
  homeTeamName: '',
  awayTeamName: '',
  matchDate: null,
  homeScore: null,
  awayScore: null,
  matchType: 1,
  watchType: 2,
  venue: '',
  notes: '',
})

const rules: FormRules = {
  homeTeamName: [{ required: true, message: '请输入主队名称', trigger: 'blur' }],
  awayTeamName: [{ required: true, message: '请输入客队名称', trigger: 'blur' }],
  matchDate: [{ required: true, message: '请选择比赛日期', trigger: 'change' }],
}

function resetForm() {
  form.recordId = undefined
  form.homeTeamName = ''
  form.awayTeamName = ''
  form.matchDate = null
  form.homeScore = null
  form.awayScore = null
  form.matchType = 1
  form.watchType = 2
  form.venue = ''
  form.notes = ''
}

function handleAdd() {
  dialogMode.value = 'create'
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(item: MatchPageItem) {
  dialogMode.value = 'edit'
  form.recordId = item.recordId
  form.homeTeamName = item.homeTeamName || ''
  form.awayTeamName = item.awayTeamName || ''
  if (item.matchDate) {
    const matchDateTime = dayjs(item.matchDate)
    form.matchDate = matchDateTime.format('YYYY-MM-DD')
  } else {
    form.matchDate = null
  }
  form.homeScore = item.homeScore ?? null
  form.awayScore = item.awayScore ?? null
  form.matchType = item.matchType ?? 1
  form.watchType = item.watchType ?? 2
  form.venue = item.venue || ''
  form.notes = item.notes || ''
  dialogVisible.value = true
}

const submitLoading = ref(false)

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()

  // 校验主队和客队不能相同
  if (form.homeTeamName.trim() === form.awayTeamName.trim()) {
    ElMessage.error('主队和客队不能相同')
    return
  }

  try {
    submitLoading.value = true
    
    if (dialogMode.value === 'create') {
      await createMatch({
        homeTeamName: form.homeTeamName.trim(),
        awayTeamName: form.awayTeamName.trim(),
        matchDate: form.matchDate!,
        homeScore: form.homeScore ?? undefined,
        awayScore: form.awayScore ?? undefined,
        matchType: form.matchType ?? undefined,
        watchType: form.watchType ?? undefined,
        venue: form.venue || undefined,
        notes: form.notes || undefined,
      })
      ElMessage.success('创建成功')
    } else if (dialogMode.value === 'edit' && form.recordId) {
      await updateMatch(form.recordId, {
        id: form.recordId,
        homeTeamName: form.homeTeamName.trim(),
        awayTeamName: form.awayTeamName.trim(),
        matchDate: form.matchDate!,
        homeScore: form.homeScore ?? undefined,
        awayScore: form.awayScore ?? undefined,
        matchType: form.matchType ?? undefined,
        watchType: form.watchType ?? undefined,
        venue: form.venue || undefined,
        notes: form.notes || undefined,
      })
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    loadMatches()
  } catch (e) {
    // 已统一处理
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(recordId: number) {
  try {
    await deleteMatch(recordId)
    ElMessage.success('删除成功')
    if (matchList.value.length === 1 && pageNo.value > 1) {
      pageNo.value -= 1
    }
    loadMatches()
  } catch (e) {
    // 已统一处理
  }
}

const filteredMatches = computed(() => {
  if (filterType.value === 'all') {
    return matchList.value
  }
  return matchList.value.filter(m => m.matchType === filterType.value)
})

onMounted(() => {
  loadMatches()
})
</script>

<style scoped>
.match-page {
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

/* 比赛卡片进入动画 */
.match-card-wrapper {
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

/* 比赛卡片样式 */
.match-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.05) 0%, rgba(0, 212, 255, 0.03) 50%, rgba(0, 255, 204, 0.03) 100%);
  transition: all 0.3s ease;
}

.match-card-wrapper:hover .match-card {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(0, 212, 255, 0.15), 0 0 30px rgba(0, 212, 255, 0.1);
}

.match-card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  padding: 1px;
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.3), rgba(0, 255, 204, 0.3));
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.3s;
}

.match-card-wrapper:hover .match-card::before {
  opacity: 1;
}

/* 操作按钮动画 */
.match-actions {
  animation: actionsSlideUp 0.4s ease-out 0.2s both;
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

/* 弹窗样式 */
:deep(.match-dialog) {
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

:deep(.match-dialog .el-dialog__header) {
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

:deep(.match-dialog .el-dialog__title) {
  color: #e5f5ff;
  font-weight: 600;
  letter-spacing: 0.04em;
}

:deep(.match-dialog .el-dialog__body) {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 16px;
}

:deep(.match-dialog .el-dialog__footer) {
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.match-form-wrapper {
  padding: 16px 18px 4px;
  border-radius: 14px;
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.04),
    rgba(0, 212, 255, 0.05),
    rgba(0, 0, 0, 0.3)
  );
}

.match-form {
  --el-text-color-regular: #e5e7f0;
}

:deep(.match-dialog .el-form-item__label) {
  color: #cbd5ff;
}

/* 移动端适配 */
@media (max-width: 600px) {
  :deep(.match-dialog) {
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

  :deep(.match-dialog .el-dialog__body) {
    padding: 12px 12px 80px;
  }

  :deep(.match-dialog .el-dialog__footer) {
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

