<template>
  <AnimatedCard variant="glass" class="insights-card overflow-hidden">
    <div class="p-4 sm:p-6">
      <!-- 标题 -->
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-semibold text-white flex items-center">
          <el-icon class="mr-2 text-[#00d4ff]" :size="20">
            <component :is="icon" />
          </el-icon>
          {{ title }}
        </h3>
      </div>

      <!-- 内容 -->
      <div class="space-y-3">
        <slot>
          <div v-if="loading" class="flex items-center justify-center py-8">
            <el-icon class="is-loading text-[#00d4ff]" :size="24">
              <Loading />
            </el-icon>
          </div>
          <div v-else-if="!value && value !== 0" class="text-gray-400 text-sm py-4 text-center">
            暂无数据
          </div>
          <div v-else class="space-y-2">
            <div class="flex items-baseline gap-2">
              <span class="text-3xl font-bold gradient-text">{{ displayValue }}</span>
              <span v-if="unit" class="text-gray-400 text-sm">{{ unit }}</span>
            </div>
            <p v-if="description" class="text-gray-400 text-xs">{{ description }}</p>
            <div v-if="trend !== undefined" class="flex items-center gap-1.5 mt-2">
              <el-icon 
                :size="14" 
                :class="trend > 0 ? 'text-green-400' : trend < 0 ? 'text-red-400' : 'text-gray-400'"
              >
                <component :is="trend > 0 ? ArrowUp : trend < 0 ? ArrowDown : Minus" />
              </el-icon>
              <span 
                :class="trend > 0 ? 'text-green-400' : trend < 0 ? 'text-red-400' : 'text-gray-400'"
                class="text-xs font-medium"
              >
                {{ trend > 0 ? '+' : '' }}{{ trend }}%
              </span>
            </div>
          </div>
        </slot>
      </div>
    </div>
  </AnimatedCard>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { AnimatedCard } from '@/components/uiverse'
import { ArrowUp, ArrowDown, Minus, Loading } from '@element-plus/icons-vue'

interface Props {
  title: string
  icon?: any
  value?: string | number
  unit?: string
  description?: string
  trend?: number
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
})

const displayValue = computed(() => {
  if (props.value === undefined || props.value === null) {
    return '—'
  }
  return props.value
})
</script>

<style scoped>
.insights-card {
  @apply h-full;
}
</style>

