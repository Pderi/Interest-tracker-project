<template>
  <div class="login-container h-screen flex items-center justify-center bg-gradient-to-b from-zinc-950 via-slate-950 to-black relative overflow-hidden">
    <!-- 背景特效：粒子连线网络，增强氛围感 -->
    <BackgroundEffects effect-type="network" />
    
    <div class="login-box relative z-10 backdrop-blur-2xl bg-white/[0.03] rounded-3xl p-6 sm:p-8 md:p-10 w-full max-w-md border border-white/10 shadow-[0_0_60px_rgba(0,212,255,0.25)]">
      <!-- 顶部发光边框 -->
      <div class="absolute inset-x-10 -top-px h-px bg-gradient-to-r from-transparent via-cyan-400/60 to-transparent opacity-70"></div>

      <div class="text-center mb-8">
        <div class="w-16 h-16 sm:w-20 sm:h-20 rounded-2xl bg-gradient-to-br from-purple-500 to-pink-500 flex items-center justify-center mx-auto mb-4 shadow-lg shadow-purple-500/50 transition-transform hover:scale-110">
          <el-icon :size="32" class="text-white">
            <Camera />
          </el-icon>
        </div>
        <h1 class="text-3xl sm:text-4xl font-bold bg-gradient-to-r from-cyan-300 via-sky-400 to-emerald-300 bg-clip-text text-transparent mb-2 tracking-wide">
          兴趣追踪平台
        </h1>
        <p class="text-gray-400 text-sm sm:text-base">
          {{ isRegister ? '加入我们，从此记录每一帧心动' : '欢迎回来，继续你的兴趣旅程' }}
        </p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="space-y-6"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            size="large"
            :prefix-icon="User"
            class="custom-input"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="!isRegister && handleLogin()"
            class="custom-input"
          />
        </el-form-item>

        <el-form-item v-if="isRegister" prop="confirmPassword">
          <el-input
            v-model="loginForm.confirmPassword"
            type="password"
            placeholder="确认密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleSubmit"
            class="custom-input"
          />
        </el-form-item>

        <el-form-item v-if="!isRegister">
          <el-checkbox v-model="loginForm.rememberMe" class="!text-gray-400">
            记住我
          </el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="w-full bg-gradient-to-r from-cyan-500 via-sky-500 to-emerald-400 border-0 hover:from-cyan-400 hover:to-emerald-300 transition-all duration-300 shadow-lg shadow-cyan-500/50 hover:shadow-cyan-400/70 tracking-wide"
            :loading="loading"
            @click="handleSubmit"
          >
            {{ isRegister ? '注册并进入世界' : '立即登录' }}
          </el-button>
        </el-form-item>

        <div class="text-center text-xs sm:text-sm text-gray-400 flex items-center justify-center gap-1">
          <span class="opacity-80">
            {{ isRegister ? '已经有账号了？' : '还没有账号？' }}
          </span>
          <el-link type="primary" @click="toggleMode" class="ml-1 !p-0">
            {{ isRegister ? '返回登录' : '立即注册' }}
          </el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock, Camera } from '@element-plus/icons-vue'
import { login, register } from '@/api/user'
import { useUserStore } from '@/store/user'
import BackgroundEffects from '@/components/BackgroundEffects.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)
const isRegister = ref(false)

const loginForm = reactive({
  username: localStorage.getItem('remembered_username') || '',
  password: '',
  confirmPassword: '',
  rememberMe: !!localStorage.getItem('remembered_username'),
})

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (isRegister.value && value !== loginForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

async function handleLogin() {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm)
        if (res.data) {
          // 后端返回的是纯 token 字符串
          userStore.setToken(res.data)
          await userStore.fetchUserInfo()

          // 记住我功能：勾选则用 localStorage 持久保存，否则用 sessionStorage（关闭浏览器失效）
          if (loginForm.rememberMe) {
            localStorage.setItem('remember_me', 'true')
          } else {
            localStorage.removeItem('remember_me')
            // 将 token 从 localStorage 移到 sessionStorage
            sessionStorage.setItem('token', res.data)
            localStorage.removeItem('token')
          }

          ElMessage.success('登录成功')

          const redirect = (route.query.redirect as string) || '/'
          router.push(redirect)
        }
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

async function handleRegister() {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      // 额外校验确认密码
      if (loginForm.password !== loginForm.confirmPassword) {
        ElMessage.error('两次输入的密码不一致')
        return
      }

      loading.value = true
      try {
        // 先注册，再自动登录，保持流畅体验
        await register(loginForm)
        ElMessage.success('注册成功，正在为你登录...')
        await handleLogin()
      } catch (error) {
        console.error('注册失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

function toggleMode() {
  isRegister.value = !isRegister.value
}

function handleSubmit() {
  if (isRegister.value) {
    handleRegister()
  } else {
    handleLogin()
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
}

.login-box {
  animation: fadeInUp 0.7s cubic-bezier(0.22, 0.61, 0.36, 1);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

:deep(.custom-input .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.custom-input .el-input__wrapper:hover) {
  border-color: rgba(102, 126, 234, 0.5);
  background: rgba(255, 255, 255, 0.08);
}

:deep(.custom-input .el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  background: rgba(255, 255, 255, 0.08);
}
</style>

