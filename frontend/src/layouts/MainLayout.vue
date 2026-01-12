<template>
  <div class="main-layout h-screen flex flex-col overflow-hidden relative">
    <!-- 背景特效 -->
    <BackgroundEffects effect-type="gradient" />
    
    <!-- Header -->
    <Header />
    
    <div class="flex flex-1 overflow-hidden relative" style="z-index: 10;">
      <!-- Sidebar - 桌面端显示 -->
      <Sidebar class="hidden lg:block" />
      
      <!-- Main Content -->
      <main
        ref="mainRef"
        class="flex-1 overflow-y-auto pb-16 lg:pb-0 relative"
        style="z-index: 10; background: linear-gradient(135deg, rgba(26, 26, 46, 0.6) 0%, rgba(22, 33, 62, 0.6) 50%, rgba(15, 52, 96, 0.6) 100%);"
      >
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 sm:py-6 lg:py-8 relative" style="z-index: 10;">
          <router-view v-slot="{ Component }">
            <transition name="page" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </main>
    </div>
    
    <!-- Mobile Navigation - 移动端显示 -->
    <MobileNav />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import Header from '@/components/layout/Header.vue'
import Sidebar from '@/components/layout/Sidebar.vue'
import MobileNav from '@/components/layout/MobileNav.vue'
import BackgroundEffects from '@/components/BackgroundEffects.vue'

// 主内容滚动容器
const mainRef = ref<HTMLElement | null>(null)
const route = useRoute()

// 每次路由切换后，将主内容区域滚动到顶部（兼容手机端底部导航）
watch(
  () => route.fullPath,
  async () => {
    await nextTick()
    if (mainRef.value) {
      mainRef.value.scrollTo?.({ top: 0, behavior: 'auto' })
      // 兼容不支持 scrollTo 的环境
      if ('scrollTop' in mainRef.value) {
        ;(mainRef.value as HTMLElement).scrollTop = 0
      }
    }
  }
)
</script>

<style scoped>
.main-layout {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}

.page-enter-active {
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-enter-from {
  opacity: 0;
  transform: translateY(30px) scale(0.95);
  filter: blur(10px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-20px) scale(1.05);
  filter: blur(10px);
}
</style>

