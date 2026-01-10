<template>
  <div
    :class="[
      'uiverse-card',
      `uiverse-card-${variant}`,
      { 'uiverse-card-hover': hover }
    ]"
    @click="handleClick"
  >
    <div class="uiverse-card-inner">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  variant?: 'glass' | 'gradient' | 'glow' | '3d'
  hover?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'glass',
  hover: true,
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

function handleClick(event: MouseEvent) {
  emit('click', event)
}
</script>

<style scoped>
.uiverse-card {
  position: relative;
  border-radius: 16px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.uiverse-card-inner {
  position: relative;
  width: 100%;
  height: 100%;
  z-index: 1;
}

/* 玻璃态卡片 */
.uiverse-card-glass {
  background: linear-gradient(
    to bottom right,
    rgba(255, 255, 255, 0.05),
    rgba(255, 255, 255, 0.03),
    rgba(255, 255, 255, 0.05)
  );
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

.uiverse-card-glass::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(0, 212, 255, 0.1),
    transparent
  );
  transition: left 0.6s ease;
}

.uiverse-card-glass:hover::before {
  left: 100%;
}

.uiverse-card-glass:hover {
  border-color: rgba(0, 212, 255, 0.4);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4),
    0 0 20px rgba(0, 212, 255, 0.2),
    0 0 40px rgba(0, 255, 204, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
  transform: translateY(-4px);
}

/* 渐变卡片 */
.uiverse-card-gradient {
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.1), rgba(0, 255, 204, 0.05));
  border: 1px solid rgba(0, 212, 255, 0.2);
  backdrop-filter: blur(10px);
}

.uiverse-card-gradient::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 16px;
  padding: 1px;
  background: linear-gradient(135deg, #00d4ff, #00ffcc, #00d4ff);
  background-size: 200% 200%;
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.3s;
  animation: gradient-border 3s linear infinite;
}

.uiverse-card-gradient:hover::after {
  opacity: 1;
}

.uiverse-card-gradient:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 12px 40px rgba(0, 212, 255, 0.3);
}

@keyframes gradient-border {
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

/* 发光卡片 */
.uiverse-card-glow {
  background: rgba(0, 212, 255, 0.05);
  border: 1px solid rgba(0, 212, 255, 0.2);
  backdrop-filter: blur(10px);
}

.uiverse-card-glow:hover {
  background: rgba(0, 212, 255, 0.1);
  border-color: rgba(0, 212, 255, 0.5);
  box-shadow: 0 0 30px rgba(0, 212, 255, 0.4),
    0 0 60px rgba(0, 255, 204, 0.2);
  transform: translateY(-4px);
}

/* 3D卡片 */
.uiverse-card-3d {
  perspective: 1000px;
  transform-style: preserve-3d;
}

.uiverse-card-3d .uiverse-card-inner {
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  transform-style: preserve-3d;
  background: linear-gradient(
    to bottom right,
    rgba(255, 255, 255, 0.05),
    rgba(255, 255, 255, 0.03)
  );
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-radius: 16px;
}

.uiverse-card-3d:hover .uiverse-card-inner {
  transform: rotateY(5deg) rotateX(5deg) translateZ(10px);
  box-shadow: 0 20px 60px rgba(0, 212, 255, 0.3);
}

/* 悬停动画 */
.uiverse-card-hover {
  cursor: pointer;
}

.uiverse-card-hover:hover {
  transform: translateY(-4px);
}
</style>

