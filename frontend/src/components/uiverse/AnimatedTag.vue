<template>
  <span
    :class="['uiverse-tag', `uiverse-tag-${variant}`, { 'uiverse-tag-animated': animated }]"
    @click="handleClick"
  >
    <slot></slot>
  </span>
</template>

<script setup lang="ts">
interface Props {
  variant?: 'default' | 'primary' | 'glow'
  animated?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'default',
  animated: true,
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

function handleClick(event: MouseEvent) {
  emit('click', event)
}
</script>

<style scoped>
.uiverse-tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
  user-select: none;
}

/* 默认样式 */
.uiverse-tag-default {
  color: #00d4ff;
  background: rgba(0, 212, 255, 0.1);
  border: 1px solid rgba(0, 212, 255, 0.3);
}

.uiverse-tag-default:hover {
  background: rgba(0, 212, 255, 0.2);
  border-color: rgba(0, 212, 255, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 212, 255, 0.3);
}

/* 主要样式 */
.uiverse-tag-primary {
  color: #1a1a2e;
  background: linear-gradient(135deg, #00d4ff, #00ffcc);
  border: none;
}

.uiverse-tag-primary:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.4);
}

/* 发光样式 */
.uiverse-tag-glow {
  color: #00d4ff;
  background: rgba(0, 212, 255, 0.1);
  border: 1px solid rgba(0, 212, 255, 0.3);
  position: relative;
  overflow: hidden;
}

.uiverse-tag-glow::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  padding: 1px;
  background: linear-gradient(135deg, #00d4ff, #00ffcc);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.3s;
}

.uiverse-tag-glow:hover::before {
  opacity: 1;
}

.uiverse-tag-glow:hover {
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.5);
}

/* 动画效果 */
.uiverse-tag-animated {
  animation: tag-pulse 2s ease-in-out infinite;
}

@keyframes tag-pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.8;
  }
}
</style>

