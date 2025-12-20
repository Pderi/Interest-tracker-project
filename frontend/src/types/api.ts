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
 * 影视分页列表响应（包含统计信息）
 */
export interface MoviePageWithStats {
  page: PageResult<MoviePageItem>
  statusCounts: Record<number, number> // key为状态值，value为数量
}

/**
 * 专辑分页列表响应（包含统计信息）
 */
export interface AlbumPageWithStats {
  page: PageResult<AlbumPageItem>
  statusCounts: Record<number, number> // key为状态值，value为数量
}

/**
 * 影视分页列表项
 */
export interface MoviePageItem {
  recordId: number
  movieId: number
  title: string
  type: number // 1 电影 2 电视剧
  posterUrl?: string
  watchStatus: number // 1 想看 2 在看 3 已看 4 弃剧
  personalRating?: number
  watchDate?: string
  tags?: string
  comment?: string
  createTime: string
}

/**
 * 影视详情
 */
export interface MovieDetail {
  movie: {
    id: number
    title: string
    type: number
    genre?: string
    releaseYear?: number
    director?: string
    actors?: string
    description?: string
    posterUrl?: string
    rating?: number
    duration?: number
  }
  record?: {
    id: number
    watchStatus: number
    personalRating?: number
    watchDate?: string
    watchDuration?: number
    progress?: number
    comment?: string
    tags?: string
  } | null
}

/**
 * 影视创建请求（手动录入为主）
 */
export interface MovieCreateReq {
  tmdbId?: number
  title: string
  type: number
  watchStatus?: number
  personalRating?: number
  posterUrl?: string
  tags?: string
  comment?: string
}

export interface MovieCreateRes {
  movieId: number
  recordId: number
}

/**
 * 观影记录更新
 */
export interface MovieRecordUpdateReq {
  id?: number
  watchStatus?: number
  personalRating?: number
  watchDate?: string
  watchDuration?: number
  progress?: number
  comment?: string
  posterUrl?: string
  tags?: string
}

/**
 * 用户信息
 */
export interface UserInfo {
  id: number
  username: string
  nickname?: string
  avatar?: string
  email?: string
  phone?: string
  status?: number
  createTime?: string
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
export type LoginRes = string

/**
 * 专辑分页列表项
 */
export interface AlbumPageItem {
  recordId: number
  albumId: number
  title: string
  artist: string
  releaseYear?: number
  coverUrl?: string
  listenStatus: number // 1 想听 2 在听 3 已听 4 弃听
  personalRating?: number
  listenDate?: string
  listenCount?: number
  tags?: string
  comment?: string
  createTime: string
}

/**
 * 专辑详情
 */
export interface AlbumDetail {
  album: {
    id: number
    title: string
    artist: string
    releaseYear?: number
    genre?: string
    description?: string
    coverUrl?: string
    totalTracks?: number
    duration?: number
  }
  record?: {
    id: number
    listenStatus: number
    personalRating?: number
    listenDate?: string
    listenCount?: number
    comment?: string
    tags?: string
  } | null
}

/**
 * 专辑创建请求（手动录入）
 */
export interface AlbumCreateReq {
  title: string
  artist: string
  releaseYear?: number
  genre?: string
  description?: string
  coverUrl?: string
  totalTracks?: number
  duration?: number
  listenStatus?: number
  personalRating?: number
  tags?: string
  comment?: string
}

export interface AlbumCreateRes {
  albumId: number
  recordId: number
}

/**
 * 听歌记录更新
 */
export interface AlbumRecordUpdateReq {
  id?: number
  listenStatus?: number
  personalRating?: number
  listenDate?: string
  listenCount?: number
  comment?: string
  tags?: string
}

