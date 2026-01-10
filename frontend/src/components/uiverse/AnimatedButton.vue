<template>
  <button
    :class="[
      'uiverse-btn',
      `uiverse-btn-${variant}`,
      `uiverse-btn-${size}`,
      { 'uiverse-btn-loading': loading }
    ]"
    :disabled="disabled || loading"
    @click="handleClick"
  >
    <span v-if="loading" class="uiverse-btn-spinner"></span>
    <span v-else class="uiverse-btn-content">
      <slot></slot>
    </span>
  </button>
</template>

<script setup lang="ts">
interface Props {
  variant?: 'primary' | 'secondary' | 'outline' | 'glow'
  size?: 'small' | 'medium' | 'large'
  loading?: boolean
  disabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'medium',
  loading: false,
  disabled: false,
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

function handleClick(event: MouseEvent) {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}
</script>

<style scoped>
/* 尺寸 */
.uiverse-btn-small {
  padding: 8px 16px;
  font-size: 12px;
}

.uiverse-btn-medium {
  padding: 12px 24px;
  font-size: 14px;
}

.uiverse-btn-large {
  padding: 16px 32px;
  font-size: 16px;
}

/* 主要按钮 - 科技蓝渐变 */
.uiverse-btn-primary {
  position: relative;
  font-weight: 600;
  color: #1a1a2e;
  background: linear-gradient(135deg, #00d4ff 0%, #00ffcc 100%);
  border: none;
  border-radius: 12px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.3);
}

.uiverse-btn-primary::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.uiverse-btn-primary:hover::before {
  width: 300px;
  height: 300px;
}

.uiverse-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 212, 255, 0.5);
}

.uiverse-btn-primary:active {
  transform: translateY(0);
}

.uiverse-btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 次要按钮 - 玻璃态效果 */
.uiverse-btn-secondary {
  position: relative;
  font-weight: 600;
  color: #00d4ff;
  background: rgba(0, 212, 255, 0.1);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 12px;
  cursor: pointer;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.uiverse-btn-secondary::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(0, 212, 255, 0.2), transparent);
  transition: left 0.5s;
}

.uiverse-btn-secondary:hover::after {
  left: 100%;
}

.uiverse-btn-secondary:hover {
  background: rgba(0, 212, 255, 0.2);
  border-color: rgba(0, 212, 255, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.3);
}

/* 轮廓按钮 */
.uiverse-btn-outline {
  position: relative;
  font-weight: 600;
  color: #00d4ff;
  background: transparent;
  border: 2px solid #00d4ff;
  border-radius: 12px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
}

.uiverse-btn-outline::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #00d4ff, #00ffcc);
  transition: left 0.3s ease;
  z-index: -1;
}

.uiverse-btn-outline:hover::before {
  left: 0;
}

.uiverse-btn-outline:hover {
  color: #1a1a2e;
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.4);
}

/* 发光按钮 */
.uiverse-btn-glow {
  position: relative;
  font-weight: 600;
  color: #00d4ff;
  background: rgba(0, 212, 255, 0.1);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.uiverse-btn-glow::before {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: 12px;
  padding: 2px;
  background: linear-gradient(45deg, #00d4ff, #00ffcc, #00d4ff);
  background-size: 200% 200%;
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  animation: border-glow 2s linear infinite;
  opacity: 0;
  transition: opacity 0.3s;
}

.uiverse-btn-glow:hover::before {
  opacity: 1;
}

.uiverse-btn-glow:hover {
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.6);
  transform: translateY(-2px);
}

@keyframes border-glow {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

/* 加载状态 */
.uiverse-btn-loading {
  pointer-events: none;
}

.uiverse-btn-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(26, 26, 46, 0.3);
  border-top-color: #1a1a2e;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.uiverse-btn-content {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
</style>

