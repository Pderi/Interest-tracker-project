import request from '@/utils/request'
import type { CommonResult, LoginReq, LoginRes, UserInfo } from '@/types/api'

/**
 * 用户登录
 */
export function login(data: LoginReq) {
  return request.post<CommonResult<LoginRes>>('/auth/login', data)
}

/**
 * 用户注册
 */
export function register(data: LoginReq & { email?: string }) {
  return request.post<CommonResult<LoginRes>>('/auth/register', data)
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return request.get<CommonResult<UserInfo>>('/user/info')
}

/**
 * 用户登出
 */
export function logout() {
  return request.post<CommonResult<boolean>>('/auth/logout')
}

