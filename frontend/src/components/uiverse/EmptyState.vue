<template>
  <div class="uiverse-empty-state">
    <div class="uiverse-empty-visual">
      <div class="visual-orb orb-1"></div>
      <div class="visual-orb orb-2"></div>
      <div class="visual-orb orb-3"></div>
      <div class="visual-icon">
        <slot name="icon">
          <el-icon :size="64" class="text-gray-500">
            <component :is="icon" />
          </el-icon>
        </slot>
      </div>
    </div>
    <h3 class="uiverse-empty-title">{{ title }}</h3>
    <p class="uiverse-empty-description">{{ description }}</p>
    <div v-if="$slots.action" class="uiverse-empty-action">
      <slot name="action"></slot>
    </div>
    <div v-if="$slots.guide" class="uiverse-empty-guide">
      <slot name="guide"></slot>
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

.uiverse-empty-visual {
  position: relative;
  width: 164px;
  height: 164px;
  margin-bottom: 28px;
  display: grid;
  place-items: center;
}

.visual-icon {
  position: relative;
  z-index: 2;
  width: 96px;
  height: 96px;
  display: grid;
  place-items: center;
  border-radius: 24px;
  background: linear-gradient(145deg, rgba(0, 212, 255, 0.14), rgba(0, 255, 204, 0.08));
  box-shadow:
    0 10px 30px rgba(0, 0, 0, 0.35),
    0 0 25px rgba(0, 212, 255, 0.2);
  backdrop-filter: blur(12px);
}

.visual-orb {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  filter: blur(18px);
  opacity: 0.5;
  animation: slow-spin 14s linear infinite;
}

.orb-1 {
  background: radial-gradient(circle at 30% 30%, rgba(0, 212, 255, 0.35), transparent 55%);
  animation-duration: 16s;
}

.orb-2 {
  background: radial-gradient(circle at 70% 70%, rgba(0, 255, 204, 0.28), transparent 55%);
  animation-duration: 18s;
  animation-direction: reverse;
}

.orb-3 {
  background: radial-gradient(circle at 50% 50%, rgba(255, 255, 255, 0.08), transparent 50%);
  animation-duration: 22s;
}

.uiverse-empty-visual::after {
  content: '';
  position: absolute;
  inset: 12px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow:
    0 0 30px rgba(0, 212, 255, 0.2),
    inset 0 0 18px rgba(0, 255, 204, 0.14);
  animation: breathe 4.5s ease-in-out infinite;
  opacity: 0.6;
}

.uiverse-empty-icon {
  margin-bottom: 24px;
  animation: float 3s ease-in-out infinite;
  opacity: 0.6;
  display: flex;
  align-items: center;
  justify-content: center;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes slow-spin {
  0% {
    transform: rotate(0deg) scale(1);
  }
  50% {
    transform: rotate(180deg) scale(1.05);
  }
  100% {
    transform: rotate(360deg) scale(1);
  }
}

@keyframes breathe {
  0%, 100% {
    transform: scale(1);
    opacity: 0.45;
  }
  50% {
    transform: scale(1.04);
    opacity: 0.75;
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

.uiverse-empty-guide {
  margin-top: 20px;
  width: 100%;
  max-width: 520px;
}

@media (prefers-reduced-motion: reduce) {
  .visual-orb,
  .uiverse-empty-icon {
    animation: none;
  }

  .uiverse-empty-visual::after {
    animation: none;
  }
}
</style>

