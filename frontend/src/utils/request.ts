import axios, { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import { CommonResult } from '@/types/api'
import router from '@/router'

// 是否正在刷新 token
let isRefreshing = false
// 等待刷新 token 的请求队列
let refreshSubscribers: Array<(token: string) => void> = []

// 将请求加入队列
function subscribeTokenRefresh(cb: (token: string) => void) {
  refreshSubscribers.push(cb)
}

// 刷新成功后执行队列中的请求
function onRefreshed(token: string) {
  refreshSubscribers.forEach((cb) => cb(token))
  refreshSubscribers = []
}

// 清除 token 并跳转登录页
function handleLogout() {
  localStorage.removeItem('token')
  sessionStorage.removeItem('token')
  router.push('/login')
}

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
  },
})

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 添加token（优先 localStorage，其次 sessionStorage）
    const token = localStorage.getItem('token') || sessionStorage.getItem('token')
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error: AxiosError) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse<CommonResult>) => {
    const res = response.data

    // 如果返回的状态码不是0/200，说明有错误
    if (res.code !== 0 && res.code !== 200) {
      // 401: token 过期或无效
      if (res.code === 401) {
        const originalRequest = response.config as InternalAxiosRequestConfig & { _retry?: boolean }

        // 如果是刷新 token 接口本身返回 401，直接退出
        if (originalRequest.url?.includes('/refresh-token')) {
          ElMessage.error('登录已过期，请重新登录')
          handleLogout()
          return Promise.reject(new Error('Token refresh failed'))
        }

        // 尝试刷新 token
        if (!originalRequest._retry) {
          originalRequest._retry = true

          if (!isRefreshing) {
            isRefreshing = true

            // 调用刷新 token 接口
            return service
              .post<CommonResult<string>>('/api/user/refresh-token')
              .then((refreshRes) => {
                const newToken = refreshRes.data
                if (newToken) {
                  // 保持原有存储方式
                  if (localStorage.getItem('remember_me')) {
                    localStorage.setItem('token', newToken)
                  } else {
                    sessionStorage.setItem('token', newToken)
                  }
                  onRefreshed(newToken)

                  // 重试原请求
                  if (originalRequest.headers) {
                    originalRequest.headers.Authorization = `Bearer ${newToken}`
                  }
                  return service(originalRequest)
                } else {
                  handleLogout()
                  return Promise.reject(new Error('Token refresh failed'))
                }
              })
              .catch(() => {
                ElMessage.error('登录已过期，请重新登录')
                handleLogout()
                return Promise.reject(new Error('Token refresh failed'))
              })
              .finally(() => {
                isRefreshing = false
              })
          } else {
            // 已经在刷新中，将请求加入队列
            return new Promise((resolve) => {
              subscribeTokenRefresh((newToken: string) => {
                if (originalRequest.headers) {
                  originalRequest.headers.Authorization = `Bearer ${newToken}`
                }
                resolve(service(originalRequest))
              })
            })
          }
        }
      }

      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    } else {
      return res
    }
  },
  (error: AxiosError<CommonResult>) => {
    console.error('响应错误:', error)

    let message = '请求失败'
    const status = error.response?.status

    if (status === 401) {
      // HTTP 401 也尝试刷新 token
      const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean }

      if (originalRequest?.url?.includes('/refresh-token')) {
        ElMessage.error('登录已过期，请重新登录')
        handleLogout()
        return Promise.reject(error)
      }

      if (originalRequest && !originalRequest._retry) {
        originalRequest._retry = true

        if (!isRefreshing) {
          isRefreshing = true

          return service
            .post<CommonResult<string>>('/api/user/refresh-token')
            .then((refreshRes) => {
              const newToken = refreshRes.data
              if (newToken) {
                // 保持原有存储方式
                if (localStorage.getItem('remember_me')) {
                  localStorage.setItem('token', newToken)
                } else {
                  sessionStorage.setItem('token', newToken)
                }
                onRefreshed(newToken)

                if (originalRequest.headers) {
                  originalRequest.headers.Authorization = `Bearer ${newToken}`
                }
                return service(originalRequest)
              } else {
                handleLogout()
                return Promise.reject(new Error('Token refresh failed'))
              }
            })
            .catch(() => {
              ElMessage.error('登录已过期，请重新登录')
              handleLogout()
              return Promise.reject(error)
            })
            .finally(() => {
              isRefreshing = false
            })
        } else {
          return new Promise((resolve) => {
            subscribeTokenRefresh((newToken: string) => {
              if (originalRequest.headers) {
                originalRequest.headers.Authorization = `Bearer ${newToken}`
              }
              resolve(service(originalRequest))
            })
          })
        }
      }

      message = '未授权，请重新登录'
      handleLogout()
    } else if (error.response) {
      switch (status) {
        case 400:
          message = '请求参数错误'
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求地址不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = `连接错误${status}`
      }
      ElMessage.error(message)
    } else if (error.request) {
      message = '网络连接失败'
      ElMessage.error(message)
    }

    return Promise.reject(error)
  }
)

export default service
