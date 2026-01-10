<template>
  <div class="uiverse-empty-state">
    <div class="uiverse-empty-icon">
      <slot name="icon">
        <el-icon :size="64" class="text-gray-500">
          <component :is="icon" />
        </el-icon>
      </slot>
    </div>
    <h3 class="uiverse-empty-title">{{ title }}</h3>
    <p class="uiverse-empty-description">{{ description }}</p>
    <div v-if="$slots.action" class="uiverse-empty-action">
      <slot name="action"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Document } from '@element-plus/icons-vue'

interface Props {
  title?: string
  description?: string
  icon?: any
}

withDefaults(defineProps<Props>(), {
  title: '暂无数据',
  description: '点击上方按钮添加记录',
  icon: Document,
})
</script>

<style scoped>
.uiverse-empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.uiverse-empty-icon {
  margin-bottom: 24px;
  animation: float 3s ease-in-out infinite;
  opacity: 0.6;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.uiverse-empty-title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 8px;
}

.uiverse-empty-description {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 24px;
}

.uiverse-empty-action {
  margin-top: 16px;
}
</style>

