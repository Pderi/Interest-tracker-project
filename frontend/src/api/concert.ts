import request from '@/utils/request'
import type {
  CommonResult,
  ConcertPageWithStats,
  ConcertPageItem,
  ConcertDetail,
  ConcertCreateReq,
  ConcertCreateRes,
  ConcertRecordUpdateReq,
  ConcertUpdateReq,
} from '@/types/api'

// 分页获取当前用户的演唱会 + 观演记录列表
export function getConcertPage(params: {
  pageNo?: number
  pageSize?: number
  status?: number
  artist?: string
  city?: string
  concertType?: number
  tag?: string
  keyword?: string
  startWatchDate?: string
  endWatchDate?: string
  sort?: string
}) {
  return request.get<CommonResult<ConcertPageWithStats>>('/concert/concerts', {
    params,
  })
}

// 创建演唱会记录（含观演记录）
export function createConcert(data: ConcertCreateReq) {
  return request.post<CommonResult<ConcertCreateRes>>('/concert/concerts', data)
}

// 获取演唱会详情
export function getConcertDetail(id: number) {
  return request.get<CommonResult<ConcertDetail>>(`/concert/concerts/${id}`)
}

// 更新观演记录
export function updateConcertRecord(recordId: number, data: ConcertRecordUpdateReq) {
  return request.put<CommonResult<boolean>>(`/concert/concerts/records/${recordId}`, data)
}

// 更新演唱会信息
export function updateConcert(concertId: number, data: ConcertUpdateReq) {
  return request.put<CommonResult<boolean>>(`/concert/concerts/${concertId}`, { id: concertId, ...data })
}

// 删除观演记录
export function deleteConcertRecord(recordId: number) {
  return request.delete<CommonResult<boolean>>(`/concert/concerts/records/${recordId}`)
}
