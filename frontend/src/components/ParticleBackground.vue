<template>
  <canvas ref="canvasRef" class="particle-canvas fixed inset-0 pointer-events-none z-0"></canvas>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const canvasRef = ref<HTMLCanvasElement>()
let animationId: number | null = null
let mouseX = 0
let mouseY = 0

interface Particle {
  x: number
  y: number
  radius: number
  speedX: number
  speedY: number
  opacity: number
  color: string
  trail: Array<{ x: number; y: number; opacity: number }>
}

const particles: Particle[] = []
const particleCount = 30 // 减少粒子数量，更和谐

function initParticles() {
  if (!canvasRef.value) return

  const canvas = canvasRef.value
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  // 设置画布尺寸
  function resizeCanvas() {
    if (!canvas) return
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
  }

  resizeCanvas()
  window.addEventListener('resize', resizeCanvas)

  // 鼠标跟踪
  function handleMouseMove(e: MouseEvent) {
    mouseX = e.clientX
    mouseY = e.clientY
  }
  window.addEventListener('mousemove', handleMouseMove)

  // 创建粒子
  for (let i = 0; i < particleCount; i++) {
    particles.push({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      radius: Math.random() * 2 + 1,
      speedX: (Math.random() - 0.5) * 0.8,
      speedY: (Math.random() - 0.5) * 0.8,
      opacity: Math.random() * 0.4 + 0.15,
      color: Math.random() > 0.5 ? '#00d4ff' : '#00ffcc',
      trail: [],
    })
  }

  function animate() {
    if (!ctx || !canvas) return

    // 创建渐变背景清除（更淡）
    ctx.fillStyle = 'rgba(26, 26, 46, 0.02)'
    ctx.fillRect(0, 0, canvas.width, canvas.height)

    particles.forEach((particle, index) => {
      // 鼠标交互 - 粒子被鼠标吸引（降低强度）
      const dx = mouseX - particle.x
      const dy = mouseY - particle.y
      const distance = Math.sqrt(dx * dx + dy * dy)
      
      if (distance < 120) {
        const force = (120 - distance) / 120
        particle.speedX += (dx / distance) * force * 0.01
        particle.speedY += (dy / distance) * force * 0.01
      }

      // 更新位置
      particle.x += particle.speedX
      particle.y += particle.speedY

      // 边界检测 - 环绕效果
      if (particle.x < 0) particle.x = canvas.width
      if (particle.x > canvas.width) particle.x = 0
      if (particle.y < 0) particle.y = canvas.height
      if (particle.y > canvas.height) particle.y = 0

      // 速度衰减
      particle.speedX *= 0.99
      particle.speedY *= 0.99

      // 添加轨迹点（减少轨迹长度）
      particle.trail.push({ x: particle.x, y: particle.y, opacity: particle.opacity })
      if (particle.trail.length > 4) {
        particle.trail.shift()
      }

      // 绘制轨迹（降低透明度）
      if (particle.trail.length > 1) {
        ctx.beginPath()
        ctx.moveTo(particle.trail[0].x, particle.trail[0].y)
        for (let i = 1; i < particle.trail.length; i++) {
          const trailOpacity = (particle.trail[i].opacity * (i / particle.trail.length)) * 0.15
          ctx.lineTo(particle.trail[i].x, particle.trail[i].y)
          ctx.strokeStyle = particle.color + Math.floor(trailOpacity * 255).toString(16).padStart(2, '0')
          ctx.lineWidth = 1
          ctx.stroke()
        }
      }

      // 绘制粒子光晕（降低强度）
      const gradient = ctx.createRadialGradient(
        particle.x,
        particle.y,
        0,
        particle.x,
        particle.y,
        particle.radius * 2.5
      )
      gradient.addColorStop(0, particle.color + '80')
      gradient.addColorStop(0.5, particle.color + '40')
      gradient.addColorStop(1, particle.color + '00')
      
      ctx.fillStyle = gradient
      ctx.beginPath()
      ctx.arc(particle.x, particle.y, particle.radius * 2.5, 0, Math.PI * 2)
      ctx.fill()

      // 绘制粒子核心
      ctx.beginPath()
      ctx.arc(particle.x, particle.y, particle.radius, 0, Math.PI * 2)
      ctx.fillStyle = particle.color + Math.floor(particle.opacity * 255).toString(16).padStart(2, '0')
      ctx.fill()

      // 绘制连接线 - 降低强度，更和谐
      particles.slice(index + 1).forEach((otherParticle) => {
        const dx = particle.x - otherParticle.x
        const dy = particle.y - otherParticle.y
        const distance = Math.sqrt(dx * dx + dy * dy)

        if (distance < 150) {
          const opacity = (1 - distance / 150) * 0.15
          ctx.beginPath()
          ctx.moveTo(particle.x, particle.y)
          ctx.lineTo(otherParticle.x, otherParticle.y)
          
          // 创建渐变连接线
          const lineGradient = ctx.createLinearGradient(
            particle.x,
            particle.y,
            otherParticle.x,
            otherParticle.y
          )
          lineGradient.addColorStop(0, particle.color + Math.floor(opacity * 255).toString(16).padStart(2, '0'))
          lineGradient.addColorStop(1, otherParticle.color + Math.floor(opacity * 255).toString(16).padStart(2, '0'))
          
          ctx.strokeStyle = lineGradient
          ctx.lineWidth = 0.8
          ctx.stroke()
        }
      })
    })

    animationId = requestAnimationFrame(animate)
  }

  animate()

  return () => {
    window.removeEventListener('resize', resizeCanvas)
    window.removeEventListener('mousemove', handleMouseMove)
    if (animationId) {
      cancelAnimationFrame(animationId)
    }
  }
}

onMounted(() => {
  initParticles()
})

onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
})
</script>

<style scoped>
.particle-canvas {
  background: transparent;
}
</style>
