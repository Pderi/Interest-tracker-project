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
  genre?: string
  listenStatus: number // 1 想听 2 在听 3 已听 4 弃听
  personalRating?: number
  listenDate?: string
  listenCount?: number
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
}

/**
 * 图书分页列表响应（包含统计信息）
 */
export interface BookPageWithStats {
  page: PageResult<BookPageItem>
  statusCounts: Record<number, number> // key为状态值，value为数量
}

/**
 * 图书分页列表项
 */
export interface BookPageItem {
  recordId: number
  bookId: number
  title: string
  author?: string
  genre?: string
  coverUrl?: string
  readStatus: number // 1 想读 2 在读 3 已读 4 弃读
  personalRating?: number
  readDate?: string
  readProgress?: number
  tags?: string
  comment?: string
  createTime: string
}

/**
 * 图书详情
 */
export interface BookDetail {
  book: {
    id: number
    title: string
    author?: string
    publisher?: string
    publishYear?: number
    isbn?: string
    genre?: string
    description?: string
    coverUrl?: string
    pageCount?: number
    language?: string
  }
  record?: {
    id: number
    readStatus: number
    personalRating?: number
    readDate?: string
    readProgress?: number
    comment?: string
    tags?: string
  } | null
}

/**
 * 图书创建请求（手动录入）
 */
export interface BookCreateReq {
  title: string
  author?: string
  genre?: string
  description?: string
  coverUrl?: string
  readStatus?: number // 1 想读 2 在读 3 已读 4 弃读
  personalRating?: number
  comment?: string
}

export interface BookCreateRes {
  bookId: number
  recordId: number
}

/**
 * 阅读记录更新
 */
export interface BookRecordUpdateReq {
  id?: number
  readStatus?: number
  personalRating?: number
  readDate?: string
  readProgress?: number
  comment?: string
  coverUrl?: string
  tags?: string
}

/**
 * 比赛分页列表响应（包含统计信息）
 */
export interface MatchPageWithStats {
  page: PageResult<MatchPageItem>
  typeCounts: Record<number, number> // key为类型值，value为数量
}

/**
 * 比赛分页列表项
 */
export interface MatchPageItem {
  recordId: number
  homeTeamName: string
  awayTeamName: string
  matchDate: string
  homeScore?: number
  awayScore?: number
  matchType: number // 1 联赛 2 杯赛 3 友谊赛
  watchType: number // 1 现场 2 直播 3 回放
  venue?: string
  notes?: string
  createTime: string
}

/**
 * 比赛详情
 */
export interface MatchDetail {
  recordId: number
  homeTeamName: string
  awayTeamName: string
  matchDate: string
  homeScore?: number
  awayScore?: number
  matchType: number
  watchType: number
  venue?: string
  notes?: string
  createTime: string
}

/**
 * 比赛创建请求
 */
export interface MatchCreateReq {
  homeTeamName: string
  awayTeamName: string
  matchDate: string // YYYY-MM-DD
  homeScore?: number
  awayScore?: number
  matchType?: number // 1 联赛 2 杯赛 3 友谊赛
  watchType?: number // 1 现场 2 直播 3 回放
  venue?: string
  notes?: string
}

export interface MatchCreateRes {
  recordId: number
}

/**
 * 比赛更新请求
 */
export interface MatchUpdateReq {
  id?: number
  homeTeamName?: string
  awayTeamName?: string
  matchDate?: string // YYYY-MM-DD
  homeScore?: number
  awayScore?: number
  matchType?: number
  watchType?: number
  venue?: string
  notes?: string
}

/**
 * 演唱会分页列表响应（包含统计信息）
 */
export interface ConcertPageWithStats {
  page: PageResult<ConcertPageItem>
  statusCounts: Record<number, number> // key为状态值，value为数量
}

/**
 * 演唱会分页列表项
 */
export interface ConcertPageItem {
  recordId: number
  concertId: number
  artist: string
  title?: string
  concertDate?: string
  venue?: string
  city?: string
  country?: string
  concertType?: number // 1 演唱会 2 音乐节 3 演出 4 其他
  posterUrl?: string
  watchStatus: number // 1 想看 2 已看
  personalRating?: number
  watchDate?: string
  ticketPrice?: number
  seatInfo?: string
  comment?: string
  createTime: string
}

/**
 * 演唱会详情
 */
export interface ConcertDetail {
  concert: {
    id: number
    artist: string
    title?: string
    concertDate?: string
    venue?: string
    city?: string
    country?: string
    concertType?: number
    description?: string
    posterUrl?: string
  }
  record?: {
    id: number
    watchStatus: number
    personalRating?: number
    watchDate?: string
    ticketPrice?: number
    seatInfo?: string
    comment?: string
    tags?: string
  } | null
}

/**
 * 演唱会创建请求（手动录入）
 */
export interface ConcertCreateReq {
  artist: string
  title?: string
  concertDate?: string
  venue?: string
  city?: string
  country?: string
  concertType?: number
  description?: string
  posterUrl?: string
  watchStatus?: number
  personalRating?: number
  watchDate?: string
  ticketPrice?: number
  seatInfo?: string
  tags?: string
  comment?: string
}

export interface ConcertCreateRes {
  concertId: number
  recordId: number
}

/**
 * 观演记录更新
 */
export interface ConcertRecordUpdateReq {
  id?: number
  watchStatus?: number
  personalRating?: number
  watchDate?: string
  ticketPrice?: number
  seatInfo?: string
  comment?: string
}

/**
 * 演唱会信息更新
 */
export interface ConcertUpdateReq {
  id?: number
  artist?: string
  title?: string
  concertDate?: string
  venue?: string
  city?: string
  country?: string
  concertType?: number
  description?: string
  posterUrl?: string
}

/**
 * 旅游分页列表响应（包含统计信息）
 */
export interface TravelPageWithStats {
  page: PageResult<TravelPageItem>
  statusCounts: Record<number, number> // key为状态值，value为数量
}

/**
 * 旅游分页列表项
 */
export interface TravelPageItem {
  recordId: number
  placeId: number
  name: string
  country?: string
  city?: string
  address?: string
  placeType?: number // 1 城市 2 景点 3 国家 4 其他
  coverUrl?: string
  travelStatus: number // 1 想去 2 计划中 3 已去
  personalRating?: number
  travelDate?: string
  travelDuration?: number
  expense?: number
  comment?: string
  createTime: string
}

/**
 * 旅游详情
 */
export interface TravelDetail {
  place: {
    id: number
    name: string
    country?: string
    city?: string
    address?: string
    latitude?: number
    longitude?: number
    placeType?: number
    description?: string
    coverUrl?: string
  }
  record?: {
    id: number
    travelStatus: number
    personalRating?: number
    travelDate?: string
    travelDuration?: number
    expense?: number
    comment?: string
    tags?: string
  } | null
}

/**
 * 旅游创建请求（手动录入）
 */
export interface TravelCreateReq {
  name: string
  country?: string
  city?: string
  address?: string
  latitude?: number
  longitude?: number
  placeType?: number
  description?: string
  coverUrl?: string
  travelStatus?: number
  personalRating?: number
  travelDate?: string
  travelDuration?: number
  expense?: number
  tags?: string
  comment?: string
}

export interface TravelCreateRes {
  placeId: number
  recordId: number
}

/**
 * 旅游记录更新
 */
export interface TravelRecordUpdateReq {
  id?: number
  travelStatus?: number
  personalRating?: number
  travelDate?: string
  travelDuration?: number
  expense?: number
  comment?: string
}

/**
 * 旅游地点信息更新
 */
export interface TravelPlaceUpdateReq {
  id?: number
  name?: string
  country?: string
  city?: string
  address?: string
  latitude?: number
  longitude?: number
  placeType?: number
  description?: string
  coverUrl?: string
}

