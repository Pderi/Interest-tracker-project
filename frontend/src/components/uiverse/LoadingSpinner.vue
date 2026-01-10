<template>
  <div :class="['uiverse-spinner', `uiverse-spinner-${size}`, `uiverse-spinner-${variant}`]">
    <div class="uiverse-spinner-ring"></div>
    <div class="uiverse-spinner-ring"></div>
    <div class="uiverse-spinner-ring"></div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  size?: 'small' | 'medium' | 'large'
  variant?: 'primary' | 'secondary' | 'glow'
}

withDefaults(defineProps<Props>(), {
  size: 'medium',
  variant: 'primary',
})
</script>

<style scoped>
.uiverse-spinner {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

/* 尺寸 */
.uiverse-spinner-small {
  --spinner-size: 16px;
  --ring-size: 2px;
}

.uiverse-spinner-medium {
  --spinner-size: 24px;
  --ring-size: 3px;
}

.uiverse-spinner-large {
  --spinner-size: 40px;
  --ring-size: 4px;
}

.uiverse-spinner-ring {
  width: var(--spinner-size);
  height: var(--spinner-size);
  border: var(--ring-size) solid transparent;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

/* 主要样式 - 科技蓝 */
.uiverse-spinner-primary .uiverse-spinner-ring:nth-child(1) {
  border-top-color: #00d4ff;
  animation-delay: 0s;
}

.uiverse-spinner-primary .uiverse-spinner-ring:nth-child(2) {
  border-top-color: #00ffcc;
  animation-delay: 0.2s;
}

.uiverse-spinner-primary .uiverse-spinner-ring:nth-child(3) {
  border-top-color: #00d4ff;
  animation-delay: 0.4s;
}

/* 次要样式 */
.uiverse-spinner-secondary .uiverse-spinner-ring {
  border-color: rgba(0, 212, 255, 0.2);
  border-top-color: #00d4ff;
}

/* 发光样式 */
.uiverse-spinner-glow .uiverse-spinner-ring {
  border-color: rgba(0, 212, 255, 0.2);
  border-top-color: #00d4ff;
  box-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>

