<template>
  <div class="time-range-selector">
    <div class="flex items-center gap-2 flex-wrap">
      <button
        v-for="option in options"
        :key="option.value"
        @click="handleSelect(option.value)"
        :class="[
          'px-4 py-2 rounded-lg text-sm font-medium transition-all duration-300',
          selectedValue === option.value
            ? 'bg-[#00d4ff] text-white shadow-lg shadow-[#00d4ff]/30'
            : 'bg-white/5 text-gray-400 hover:bg-white/10 hover:text-gray-300 border border-white/10'
        ]"
      >
        <el-icon v-if="option.icon" class="mr-1.5" :size="14">
          <component :is="option.icon" />
        </el-icon>
        {{ option.label }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { Calendar, Clock } from '@element-plus/icons-vue'

export interface TimeRangeOption {
  value: 'today' | 'week' | 'month' | 'year'
  label: string
  icon?: any
}

const props = defineProps<{
  modelValue: 'today' | 'week' | 'month' | 'year'
}>()

const emit = defineEmits<{
  'update:modelValue': [value: 'today' | 'week' | 'month' | 'year']
  'change': [value: 'today' | 'week' | 'month' | 'year']
}>()

const selectedValue = ref(props.modelValue)

const options: TimeRangeOption[] = [
  { value: 'today', label: '今日', icon: Clock },
  { value: 'week', label: '本周', icon: Calendar },
  { value: 'month', label: '本月', icon: Calendar },
  { value: 'year', label: '本年', icon: Calendar },
]

function handleSelect(value: 'today' | 'week' | 'month' | 'year') {
  selectedValue.value = value
  emit('update:modelValue', value)
  emit('change', value)
}

watch(() => props.modelValue, (newVal) => {
  selectedValue.value = newVal
})
</script>

<style scoped>
.time-range-selector {
  @apply w-full;
}
</style>

