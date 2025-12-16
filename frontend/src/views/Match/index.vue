<template>
  <div class="match-page">
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 sm:mb-8">
      <h1 class="text-3xl sm:text-4xl font-bold neon-text mb-4 sm:mb-0 float-animation">球赛</h1>
      <el-button 
        type="primary" 
        :icon="Plus" 
        @click="handleAdd"
        class="!bg-[#00d4ff] !border-[#00d4ff] hover:!bg-[#00ffcc] hover:!border-[#00ffcc] !text-[#1a1a2e] shadow-lg shadow-[#00d4ff]/30 hover:shadow-[#00d4ff]/50 transition-all glow-effect font-semibold"
      >
        添加记录
      </el-button>
    </div>

    <!-- 筛选栏 -->
    <div class="mb-6 flex flex-wrap gap-3">
      <el-button 
        v-for="type in typeOptions" 
        :key="type.value"
        :type="filterType === type.value ? 'primary' : 'default'"
        size="small"
        @click="filterType = type.value"
        :class="filterType === type.value ? '!bg-[#ff6b6b] !border-[#ff6b6b]' : ''"
      >
        {{ type.label }}
      </el-button>
    </div>

    <!-- 比赛记录列表 -->
    <div class="space-y-4">
      <div
        v-for="match in filteredMatches"
        :key="match.id"
        class="card-3d group"
      >
        <div class="card-3d-inner rounded-2xl glass-effect border border-white/10 hover:border-[#00d4ff]/30 transition-all duration-500 overflow-hidden glow-effect">
          <div class="p-4 sm:p-6">
            <!-- 比赛信息 -->
            <div class="flex items-center justify-between mb-4">
              <div class="flex-1">
                <div class="flex items-center mb-2">
                  <el-tag 
                    :type="getTypeTag(match.type)" 
                    size="small" 
                    effect="dark"
                    class="mr-2"
                  >
                    {{ getTypeLabel(match.type) }}
                  </el-tag>
                  <span class="text-xs text-gray-400">{{ match.matchTime }}</span>
                </div>
                <h3 class="text-lg sm:text-xl font-semibold text-white mb-3">{{ match.league }}</h3>
              </div>
              <div class="ml-4 flex-shrink-0">
                <el-icon :size="32" class="text-[#00d4ff] rotate-glow">
                  <Trophy />
                </el-icon>
              </div>
            </div>

            <!-- 比分 -->
            <div class="flex items-center justify-center mb-4 p-4 bg-gradient-to-r from-[#00d4ff]/10 to-[#00ffcc]/10 rounded-xl border border-[#00d4ff]/20">
              <div class="flex-1 text-center">
                <div class="text-2xl sm:text-3xl font-bold text-white mb-1">{{ match.homeTeam }}</div>
                <div class="text-xs text-gray-400">{{ match.homeScore !== null ? '主队' : '' }}</div>
              </div>
              <div class="mx-4 sm:mx-8">
                <div class="text-3xl sm:text-4xl font-bold gradient-text">{{ match.homeScore !== null ? `${match.homeScore} : ${match.awayScore}` : 'VS' }}</div>
              </div>
              <div class="flex-1 text-center">
                <div class="text-2xl sm:text-3xl font-bold text-white mb-1">{{ match.awayTeam }}</div>
                <div class="text-xs text-gray-400">{{ match.awayScore !== null ? '客队' : '' }}</div>
              </div>
            </div>

            <!-- 观赛信息 -->
            <div class="flex flex-wrap items-center gap-4 text-sm">
              <div class="flex items-center text-gray-400">
                <el-icon class="mr-1"><Location /></el-icon>
                <span>{{ getWatchTypeLabel(match.watchType) }}</span>
              </div>
              <div v-if="match.venue" class="flex items-center text-gray-400">
                <el-icon class="mr-1"><MapLocation /></el-icon>
                <span>{{ match.venue }}</span>
              </div>
              <div class="flex items-center text-gray-400">
                <el-icon class="mr-1"><Calendar /></el-icon>
                <span>{{ match.watchTime }}</span>
              </div>
            </div>

            <!-- 评价 -->
            <div v-if="match.comment" class="mt-4 p-3 bg-white/5 rounded-lg">
              <p class="text-sm text-gray-300 line-clamp-2">{{ match.comment }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Plus, Trophy, Location, MapLocation, Calendar } from '@element-plus/icons-vue'

// 假数据
const matches = ref([
  {
    id: 1,
    league: '西甲联赛',
    homeTeam: '巴塞罗那',
    awayTeam: '皇家马德里',
    homeScore: 3,
    awayScore: 2,
    type: 'league',
    matchTime: '2024-01-15 20:00',
    watchType: 'live',
    watchTime: '2024-01-15 20:00',
    venue: '诺坎普球场',
    comment: '经典的西班牙国家德比，梅西的精彩表现让人难忘！',
  },
  {
    id: 2,
    league: '英超联赛',
    homeTeam: '曼城',
    awayTeam: '利物浦',
    homeScore: 2,
    awayScore: 2,
    type: 'league',
    matchTime: '2024-01-10 23:00',
    watchType: 'live',
    watchTime: '2024-01-10 23:00',
    venue: '伊蒂哈德球场',
    comment: '激烈的对抗，双方都展现了高水平的技战术',
  },
  {
    id: 3,
    league: '欧冠',
    homeTeam: '拜仁慕尼黑',
    awayTeam: '巴黎圣日耳曼',
    homeScore: 1,
    awayScore: 0,
    type: 'cup',
    matchTime: '2024-02-05 03:00',
    watchType: 'replay',
    watchTime: '2024-02-05 10:00',
    venue: '安联球场',
    comment: '回看了这场比赛，拜仁的防守非常出色',
  },
  {
    id: 4,
    league: '中超联赛',
    homeTeam: '上海海港',
    awayTeam: '北京国安',
    homeScore: null,
    awayScore: null,
    type: 'league',
    matchTime: '2024-03-20 19:35',
    watchType: 'live',
    watchTime: '2024-03-20 19:35',
    venue: '上海体育场',
    comment: '期待这场比赛，两支强队的对决',
  },
  {
    id: 5,
    league: '世界杯预选赛',
    homeTeam: '中国',
    awayTeam: '日本',
    homeScore: 0,
    awayScore: 1,
    type: 'cup',
    matchTime: '2024-01-25 19:00',
    watchType: 'live',
    watchTime: '2024-01-25 19:00',
    venue: '北京工人体育场',
    comment: '现场观赛，气氛非常热烈，虽然输了但球队表现不错',
  },
  {
    id: 6,
    league: '友谊赛',
    homeTeam: 'AC米兰',
    awayTeam: '国际米兰',
    homeScore: 2,
    awayScore: 1,
    type: 'friendly',
    matchTime: '2024-02-15 02:00',
    watchType: 'replay',
    watchTime: '2024-02-15 08:00',
    venue: '圣西罗球场',
    comment: '米兰德比总是那么精彩',
  },
])

const typeOptions = [
  { label: '全部', value: 'all' },
  { label: '联赛', value: 'league' },
  { label: '杯赛', value: 'cup' },
  { label: '友谊赛', value: 'friendly' },
]

const filterType = ref('all')

const filteredMatches = computed(() => {
  if (filterType.value === 'all') {
    return matches.value
  }
  return matches.value.filter(m => m.type === filterType.value)
})

function getTypeLabel(type: string) {
  const map: Record<string, string> = {
    league: '联赛',
    cup: '杯赛',
    friendly: '友谊赛',
  }
  return map[type] || type
}

function getTypeTag(type: string) {
  const map: Record<string, string> = {
    league: 'success',
    cup: 'warning',
    friendly: 'info',
  }
  return map[type] || 'info'
}

function getWatchTypeLabel(type: string) {
  const map: Record<string, string> = {
    live: '现场观赛',
    live_stream: '直播观看',
    replay: '回看',
  }
  return map[type] || type
}

function handleAdd() {
  console.log('添加球赛记录')
}
</script>

<style scoped>
.match-page {
  min-height: 100%;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>

