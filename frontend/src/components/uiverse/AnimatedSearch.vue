<template>
  <div class="uiverse-search-wrapper">
    <div class="uiverse-search-input-wrapper" :class="{ 'is-focused': isFocused, 'has-value': modelValue }">
      <el-icon class="search-icon">
        <Search />
      </el-icon>
      <input
        :value="modelValue"
        :placeholder="placeholder"
        class="uiverse-search-input"
        @input="handleInput"
        @keyup.enter="handleEnter"
        @focus="isFocused = true"
        @blur="isFocused = false"
      />
      <div v-if="modelValue" class="search-clear" @click="handleClear">
        <el-icon><Close /></el-icon>
      </div>
    </div>
    <div class="search-underline"></div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Search, Close } from '@element-plus/icons-vue'

interface Props {
  modelValue?: string
  placeholder?: string
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '搜索...',
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
  clear: []
  enter: []
}>()

const isFocused = ref(false)

function handleInput(event: Event) {
  const target = event.target as HTMLInputElement
  emit('update:modelValue', target.value)
}

function handleEnter() {
  emit('enter')
}

function handleClear() {
  emit('update:modelValue', '')
  emit('clear')
}
</script>

<style scoped>
.uiverse-search-wrapper {
  position: relative;
  width: 100%;
}

.uiverse-search-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.uiverse-search-input-wrapper.is-focused {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(0, 212, 255, 0.5);
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.2);
}

.uiverse-search-input-wrapper.has-value {
  border-color: rgba(0, 212, 255, 0.3);
}

.search-icon {
  color: rgba(255, 255, 255, 0.5);
  margin-right: 8px;
  transition: color 0.3s ease;
}

.uiverse-search-input-wrapper.is-focused .search-icon {
  color: #00d4ff;
}

.uiverse-search-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: #fff;
  font-size: 14px;
}

.uiverse-search-input::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.search-clear {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  margin-left: 8px;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.5);
  transition: all 0.2s ease;
  border-radius: 50%;
}

.search-clear:hover {
  color: #00d4ff;
  background: rgba(0, 212, 255, 0.1);
  transform: rotate(90deg);
}

.search-underline {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #00d4ff, #00ffcc);
  transition: width 0.3s ease;
  border-radius: 0 0 12px 12px;
}

.uiverse-search-input-wrapper.is-focused ~ .search-underline {
  width: 100%;
}
</style>

