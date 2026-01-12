import request from '@/utils/request'
import type {
  CommonResult,
  TravelPageWithStats,
  TravelPageItem,
  TravelDetail,
  TravelCreateReq,
  TravelCreateRes,
  TravelRecordUpdateReq,
  TravelPlaceUpdateReq,
} from '@/types/api'

// 分页获取当前用户的旅游 + 旅游记录列表
export function getTravelPage(params: {
  pageNo?: number
  pageSize?: number
  status?: number
  country?: string
  city?: string
  placeType?: number
  tag?: string
  keyword?: string
  startTravelDate?: string
  endTravelDate?: string
  sort?: string
}) {
  return request.get<CommonResult<TravelPageWithStats>>('/travel/places', {
    params,
  })
}

// 创建旅游记录（含旅游记录）
export function createTravel(data: TravelCreateReq) {
  return request.post<CommonResult<TravelCreateRes>>('/travel/places', data)
}

// 获取旅游详情
export function getTravelDetail(id: number) {
  return request.get<CommonResult<TravelDetail>>(`/travel/places/${id}`)
}

// 更新旅游记录
export function updateTravelRecord(recordId: number, data: TravelRecordUpdateReq) {
  return request.put<CommonResult<boolean>>(`/travel/places/records/${recordId}`, data)
}

// 更新旅游地点信息
export function updateTravelPlace(placeId: number, data: TravelPlaceUpdateReq) {
  return request.put<CommonResult<boolean>>(`/travel/places/${placeId}`, { id: placeId, ...data })
}

// 删除旅游记录
export function deleteTravelRecord(recordId: number) {
  return request.delete<CommonResult<boolean>>(`/travel/places/records/${recordId}`)
}
