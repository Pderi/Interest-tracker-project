/**
 * 统一响应格式
 */
export interface CommonResult<T = any> {
  code: number
  data: T
  msg: string
}

/**
 * 分页参数
 */
export interface PageParam {
  page?: number
  size?: number
}

/**
 * 分页结果
 */
export interface PageResult<T> {
  list: T[]
  total: number
}

/**
 * 用户信息
 */
export interface UserInfo {
  id: number
  username: string
  email?: string
  avatar?: string
}

/**
 * 登录请求
 */
export interface LoginReq {
  username: string
  password: string
}

/**
 * 登录响应
 */
export interface LoginRes {
  token: string
  userInfo: UserInfo
}

