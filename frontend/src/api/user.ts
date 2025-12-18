import request from '@/utils/request'
import type { CommonResult, LoginReq, LoginRes, UserInfo } from '@/types/api'

/**
 * 用户登录
 */
export function login(data: LoginReq) {
  return request.post<CommonResult<LoginRes>>('/api/user/login', data)
}

/**
 * 用户注册
 */
export function register(data: LoginReq & { email?: string }) {
  return request.post<CommonResult<number>>('/api/user/register', data)
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return request.get<CommonResult<UserInfo>>('/api/user/info')
}

/**
 * 用户登出
 */
export function logout() {
  // 当前后端未提供登出接口，这里仅做前端本地登出处理的占位
  return Promise.resolve({} as CommonResult<boolean>)
}

/**
 * 刷新 Token
 */
export function refreshToken() {
  return request.post<CommonResult<string>>('/api/user/refresh-token')
}

