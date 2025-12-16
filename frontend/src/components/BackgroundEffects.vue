<template>
  <div class="background-effects pointer-events-none" style="position: fixed; inset: 0; z-index: 0;">
    <!-- 方案1: 网格背景 -->
    <div v-if="effectType === 'grid'" class="grid-background"></div>
    
    <!-- 方案2: 渐变光晕 -->
    <div v-if="effectType === 'gradient'" class="gradient-background">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
    </div>
    
    <!-- 方案3: 波浪动画 -->
    <div v-if="effectType === 'waves'" class="waves-background">
      <svg class="waves" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320">
        <path fill="rgba(0, 212, 255, 0.1)" fill-opacity="1" d="M0,96L48,112C96,128,192,160,288,160C384,160,480,128,576,112C672,96,768,96,864,112C960,128,1056,160,1152,160C1248,160,1344,128,1392,112L1440,96L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path>
      </svg>
    </div>
    
    <!-- 方案4: 星空效果 -->
    <div v-if="effectType === 'stars'" class="stars-background">
      <div 
        v-for="i in 100" 
        :key="`star-${i}`" 
        class="star" 
        :style="getStarStyle(i)"
      ></div>
    </div>
    
    <!-- 方案5: 几何图形 -->
    <div v-if="effectType === 'geometric'" class="geometric-background">
      <div class="geometric-shape shape-1"></div>
      <div class="geometric-shape shape-2"></div>
      <div class="geometric-shape shape-3"></div>
      <div class="geometric-shape shape-4"></div>
    </div>
    
    <!-- 方案6: 粒子连线网络 -->
    <div v-if="effectType === 'network'" class="network-background">
      <canvas ref="networkCanvas" class="network-canvas"></canvas>
    </div>
    
    <!-- 方案7: 能量波纹 -->
    <div v-if="effectType === 'ripple'" class="ripple-background">
      <div v-for="i in 5" :key="`ripple-${i}`" class="ripple-circle" :style="getRippleStyle(i)"></div>
    </div>
    
    <!-- 方案8: 流动光带 -->
    <div v-if="effectType === 'flow'" class="flow-background">
      <div v-for="i in 8" :key="`flow-${i}`" class="flow-line" :style="getFlowStyle(i)"></div>
    </div>
    
    <!-- 方案9: 数字雨 -->
    <div v-if="effectType === 'matrix'" class="matrix-background">
      <canvas ref="matrixCanvas" class="matrix-canvas"></canvas>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'

// 可以通过props传入效果类型，默认使用gradient
const props = withDefaults(defineProps<{
  effectType?: 'grid' | 'gradient' | 'waves' | 'stars' | 'geometric' | 'network' | 'ripple' | 'flow' | 'matrix'
}>(), {
  effectType: 'gradient'
})

// 预生成星星位置，避免每次渲染都随机
const starPositions = Array.from({ length: 100 }, () => ({
  left: Math.random() * 100,
  top: Math.random() * 100,
  delay: Math.random() * 3,
  duration: 2 + Math.random() * 3,
}))

function getStarStyle(index: number) {
  const pos = starPositions[index - 1] || starPositions[0]
  return {
    left: `${pos.left}%`,
    top: `${pos.top}%`,
    animationDelay: `${pos.delay}s`,
    animationDuration: `${pos.duration}s`,
  }
}

// 粒子连线网络 Canvas
const networkCanvas = ref<HTMLCanvasElement | null>(null)
let networkAnimationId: number | null = null

function initNetwork() {
  if (!networkCanvas.value) return
  
  const canvas = networkCanvas.value
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight
  
  const nodeCount = 30
  const nodes: Array<{ x: number; y: number; vx: number; vy: number }> = []
  
  for (let i = 0; i < nodeCount; i++) {
    nodes.push({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      vx: (Math.random() - 0.5) * 0.5,
      vy: (Math.random() - 0.5) * 0.5,
    })
  }
  
  function draw() {
    if (!ctx || !canvas) return
    
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    
    // 更新节点位置
    nodes.forEach(node => {
      node.x += node.vx
      node.y += node.vy
      
      if (node.x < 0 || node.x > canvas.width) node.vx *= -1
      if (node.y < 0 || node.y > canvas.height) node.vy *= -1
      
      node.x = Math.max(0, Math.min(canvas.width, node.x))
      node.y = Math.max(0, Math.min(canvas.height, node.y))
    })
    
    // 绘制连线
    const maxDistance = 150
    nodes.forEach((node, i) => {
      nodes.slice(i + 1).forEach(otherNode => {
        const dx = node.x - otherNode.x
        const dy = node.y - otherNode.y
        const distance = Math.sqrt(dx * dx + dy * dy)
        
        if (distance < maxDistance) {
          const opacity = 1 - distance / maxDistance
          ctx.strokeStyle = `rgba(0, 212, 255, ${opacity * 0.3})`
          ctx.lineWidth = 1
          ctx.beginPath()
          ctx.moveTo(node.x, node.y)
          ctx.lineTo(otherNode.x, otherNode.y)
          ctx.stroke()
        }
      })
    })
    
    // 绘制节点
    nodes.forEach(node => {
      const gradient = ctx.createRadialGradient(node.x, node.y, 0, node.x, node.y, 8)
      gradient.addColorStop(0, 'rgba(0, 212, 255, 1)')
      gradient.addColorStop(0.5, 'rgba(0, 212, 255, 0.6)')
      gradient.addColorStop(1, 'rgba(0, 255, 204, 0)')
      
      ctx.fillStyle = gradient
      ctx.beginPath()
      ctx.arc(node.x, node.y, 4, 0, Math.PI * 2)
      ctx.fill()
      
      ctx.shadowBlur = 10
      ctx.shadowColor = 'rgba(0, 212, 255, 0.8)'
      ctx.fillStyle = '#00d4ff'
      ctx.beginPath()
      ctx.arc(node.x, node.y, 2, 0, Math.PI * 2)
      ctx.fill()
      ctx.shadowBlur = 0
    })
    
    networkAnimationId = requestAnimationFrame(draw)
  }
  
  draw()
  
  const handleResize = () => {
    if (!canvas) return
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
  }
  
  window.addEventListener('resize', handleResize)
  
  return () => {
    window.removeEventListener('resize', handleResize)
    if (networkAnimationId) {
      cancelAnimationFrame(networkAnimationId)
    }
  }
}

// 波纹样式
const ripplePositions = Array.from({ length: 5 }, () => ({
  left: Math.random() * 100,
  top: Math.random() * 100,
  delay: Math.random() * 3,
  size: 200 + Math.random() * 300,
}))

function getRippleStyle(index: number) {
  const pos = ripplePositions[index - 1] || ripplePositions[0]
  return {
    left: `${pos.left}%`,
    top: `${pos.top}%`,
    width: `${pos.size}px`,
    height: `${pos.size}px`,
    animationDelay: `${pos.delay}s`,
  }
}

// 流动光带样式
const flowLines = Array.from({ length: 8 }, () => ({
  left: Math.random() * 100,
  angle: Math.random() * 360,
  delay: Math.random() * 5,
  length: 200 + Math.random() * 300,
}))

function getFlowStyle(index: number) {
  const line = flowLines[index - 1] || flowLines[0]
  return {
    left: `${line.left}%`,
    transform: `rotate(${line.angle}deg)`,
    width: `${line.length}px`,
    animationDelay: `${line.delay}s`,
  }
}

// 数字雨 Canvas
const matrixCanvas = ref<HTMLCanvasElement | null>(null)
let matrixAnimationId: number | null = null

function initMatrix() {
  if (!matrixCanvas.value) return
  
  const canvas = matrixCanvas.value
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight
  
  const chars = '01アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン'
  const fontSize = 14
  const columns = canvas.width / fontSize
  const drops: number[] = []
  
  for (let i = 0; i < columns; i++) {
    drops[i] = Math.random() * -100
  }
  
  function draw() {
    if (!ctx || !canvas) return
    
    ctx.fillStyle = 'rgba(26, 26, 46, 0.05)'
    ctx.fillRect(0, 0, canvas.width, canvas.height)
    
    ctx.fillStyle = '#00d4ff'
    ctx.font = `${fontSize}px monospace`
    
    for (let i = 0; i < drops.length; i++) {
      const text = chars[Math.floor(Math.random() * chars.length)]
      const x = i * fontSize
      const y = drops[i] * fontSize
      
      ctx.fillStyle = `rgba(0, 212, 255, ${Math.random() * 0.5 + 0.5})`
      ctx.fillText(text, x, y)
      
      if (y > canvas.height && Math.random() > 0.975) {
        drops[i] = 0
      }
      drops[i]++
    }
    
    matrixAnimationId = requestAnimationFrame(draw)
  }
  
  draw()
  
  const handleResize = () => {
    if (!canvas) return
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
  }
  
  window.addEventListener('resize', handleResize)
  
  return () => {
    window.removeEventListener('resize', handleResize)
    if (matrixAnimationId) {
      cancelAnimationFrame(matrixAnimationId)
    }
  }
}

onMounted(() => {
  if (props.effectType === 'matrix') {
    setTimeout(() => {
      const cleanup = initMatrix()
      if (cleanup) {
        onUnmounted(() => cleanup())
      }
    }, 100)
  } else if (props.effectType === 'network') {
    setTimeout(() => {
      const cleanup = initNetwork()
      if (cleanup) {
        onUnmounted(() => cleanup())
      }
    }, 100)
  }
})

watch(() => props.effectType, (newType) => {
  if (matrixAnimationId) {
    cancelAnimationFrame(matrixAnimationId)
    matrixAnimationId = null
  }
  if (networkAnimationId) {
    cancelAnimationFrame(networkAnimationId)
    networkAnimationId = null
  }
  
  if (newType === 'matrix') {
    setTimeout(() => {
      const cleanup = initMatrix()
      if (cleanup) {
        onUnmounted(() => cleanup())
      }
    }, 100)
  } else if (newType === 'network') {
    setTimeout(() => {
      const cleanup = initNetwork()
      if (cleanup) {
        onUnmounted(() => cleanup())
      }
    }, 100)
  }
})

onUnmounted(() => {
  if (matrixAnimationId) {
    cancelAnimationFrame(matrixAnimationId)
  }
  if (networkAnimationId) {
    cancelAnimationFrame(networkAnimationId)
  }
})
</script>

<style scoped>
.background-effects {
  background: transparent;
}

/* 方案1: 网格背景 */
.grid-background {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100vw;
  height: 100vh;
  z-index: 0;
  background-image: 
    linear-gradient(rgba(0, 212, 255, 0.15) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 212, 255, 0.15) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: grid-move 20s linear infinite;
}

@keyframes grid-move {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(50px, 50px);
  }
}

/* 方案2: 渐变光晕 */
.gradient-background {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  z-index: 0;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.8;
  animation: orb-float 20s ease-in-out infinite;
  will-change: transform;
  mix-blend-mode: screen;
}

.orb-1 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(0, 212, 255, 0.8) 0%, rgba(0, 212, 255, 0.4) 40%, rgba(0, 212, 255, 0.1) 70%, transparent 100%);
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.orb-2 {
  width: 700px;
  height: 700px;
  background: radial-gradient(circle, rgba(0, 255, 204, 0.7) 0%, rgba(0, 255, 204, 0.3) 40%, rgba(0, 255, 204, 0.1) 70%, transparent 100%);
  bottom: 20%;
  right: 15%;
  animation-delay: 5s;
}

.orb-3 {
  width: 550px;
  height: 550px;
  background: radial-gradient(circle, rgba(0, 212, 255, 0.7) 0%, rgba(0, 212, 255, 0.3) 40%, rgba(0, 212, 255, 0.1) 70%, transparent 100%);
  top: 50%;
  right: 30%;
  animation-delay: 10s;
}

@keyframes orb-float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(50px, -50px) scale(1.1);
  }
  66% {
    transform: translate(-30px, 30px) scale(0.9);
  }
}

/* 方案3: 波浪动画 */
.waves-background {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100vw;
  height: 300px;
  z-index: 0;
  overflow: hidden;
}

.waves {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 100%;
  animation: wave-animation 10s ease-in-out infinite;
}

@keyframes wave-animation {
  0%, 100% {
    transform: translateX(0);
  }
  50% {
    transform: translateX(-25%);
  }
}

/* 方案4: 星空效果 */
.stars-background {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100vw;
  height: 100vh;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
}

.star {
  position: absolute;
  width: 6px;
  height: 6px;
  background: #00d4ff;
  border-radius: 50%;
  animation: star-twinkle 3s ease-in-out infinite;
  box-shadow: 
    0 0 12px rgba(0, 212, 255, 1),
    0 0 24px rgba(0, 212, 255, 0.8),
    0 0 36px rgba(0, 255, 204, 0.6);
  will-change: transform, opacity;
  display: block;
  visibility: visible;
}

@keyframes star-twinkle {
  0%, 100% {
    opacity: 0.7;
    transform: scale(1);
    box-shadow: 
      0 0 10px rgba(0, 212, 255, 0.9),
      0 0 20px rgba(0, 212, 255, 0.6),
      0 0 30px rgba(0, 255, 204, 0.4);
  }
  50% {
    opacity: 1;
    transform: scale(2.2);
    box-shadow: 
      0 0 15px rgba(0, 212, 255, 1),
      0 0 30px rgba(0, 212, 255, 0.9),
      0 0 45px rgba(0, 255, 204, 0.7);
  }
}

/* 方案5: 几何图形 */
.geometric-background {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100vw;
  height: 100vh;
  z-index: 0;
  overflow: hidden;
}

.geometric-shape {
  position: absolute;
  border: 1px solid rgba(0, 212, 255, 0.2);
  animation: geometric-rotate 20s linear infinite;
}

.shape-1 {
  width: 300px;
  height: 300px;
  top: 10%;
  left: 10%;
  border-radius: 30% 70% 70% 30% / 30% 30% 70% 70%;
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.1) 0%, transparent 100%);
  animation-duration: 25s;
}

.shape-2 {
  width: 200px;
  height: 200px;
  bottom: 20%;
  right: 15%;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(0, 255, 204, 0.1) 0%, transparent 70%);
  animation-duration: 30s;
  animation-direction: reverse;
}

.shape-3 {
  width: 250px;
  height: 250px;
  top: 50%;
  right: 30%;
  clip-path: polygon(50% 0%, 0% 100%, 100% 100%);
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.08) 0%, transparent 100%);
  animation-duration: 35s;
}

.shape-4 {
  width: 180px;
  height: 180px;
  bottom: 10%;
  left: 20%;
  border-radius: 20px;
  background: linear-gradient(45deg, rgba(0, 255, 204, 0.08) 0%, transparent 100%);
  animation-duration: 28s;
  animation-direction: reverse;
}

@keyframes geometric-rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 方案6: 粒子连线网络 */
.network-background {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100vw;
  height: 100vh;
  z-index: 0;
  overflow: hidden;
}

.network-canvas {
  width: 100%;
  height: 100%;
  display: block;
}

/* 方案7: 能量波纹 */
.ripple-background {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100vw;
  height: 100vh;
  z-index: 0;
  overflow: hidden;
}

.ripple-circle {
  position: absolute;
  border: 2px solid #00d4ff;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  animation: ripple-expand 4s ease-out infinite;
  box-shadow: 
    0 0 20px rgba(0, 212, 255, 0.6),
    inset 0 0 20px rgba(0, 212, 255, 0.2);
}

@keyframes ripple-expand {
  0% {
    width: 0;
    height: 0;
    opacity: 1;
    border-width: 2px;
  }
  50% {
    opacity: 0.6;
  }
  100% {
    width: 600px;
    height: 600px;
    opacity: 0;
    border-width: 1px;
  }
}

/* 方案8: 流动光带 */
.flow-background {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100vw;
  height: 100vh;
  z-index: 0;
  overflow: hidden;
}

.flow-line {
  position: absolute;
  height: 2px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(0, 212, 255, 0.8) 20%,
    rgba(0, 255, 204, 1) 50%,
    rgba(0, 212, 255, 0.8) 80%,
    transparent 100%
  );
  box-shadow: 
    0 0 10px rgba(0, 212, 255, 0.8),
    0 0 20px rgba(0, 255, 204, 0.6);
  transform-origin: left center;
  animation: flow-move 8s linear infinite;
}

@keyframes flow-move {
  0% {
    transform: translateX(-100%) rotate(var(--flow-angle, 0deg));
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateX(calc(100vw + 100%)) rotate(var(--flow-angle, 0deg));
    opacity: 0;
  }
}

/* 方案9: 数字雨 */
.matrix-background {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100vw;
  height: 100vh;
  z-index: 0;
  overflow: hidden;
}

.matrix-canvas {
  width: 100%;
  height: 100%;
  display: block;
}
</style>

