import request from '@/utils/request'
import type {
  CommonResult,
  PageResult,
  MoviePageItem,
  MovieDetail,
  MovieCreateReq,
  MovieCreateRes,
  MovieRecordUpdateReq,
  MoviePageWithStats,
} from '@/types/api'

// 分页获取当前用户的影视 + 观影记录列表
export function getMoviePage(params: {
  pageNo?: number
  pageSize?: number
  watchStatus?: number
  type?: number
  tag?: string
  keyword?: string
  startWatchDate?: string
  endWatchDate?: string
  sort?: string
}) {
  return request.get<CommonResult<MoviePageWithStats>>('/api/movies', {
    params,
  })
}

// 创建影视记录（含观影记录）
export function createMovie(data: MovieCreateReq) {
  return request.post<CommonResult<MovieCreateRes>>('/api/movies', data)
}

// 获取影视详情
export function getMovieDetail(id: number) {
  return request.get<CommonResult<MovieDetail>>(`/api/movies/${id}`)
}

// 更新观影记录
export function updateMovieRecord(recordId: number, data: MovieRecordUpdateReq) {
  return request.put<CommonResult<boolean>>(`/api/movies/records/${recordId}`, data)
}

// 删除观影记录
export function deleteMovieRecord(recordId: number) {
  return request.delete<CommonResult<boolean>>(`/api/movies/records/${recordId}`)
}


