import request from '@/utils/request'
import type { CommonResult } from '@/types/api'

/**
 * 照片信息
 */
export interface Photo {
  id: number
  title?: string
  description?: string
  filePath: string
  thumbnailPath?: string
  location?: string
  shootTime?: string
  travelRecordId?: number
  concertRecordId?: number
}

/**
 * 上传照片
 */
export function uploadPhoto(data: FormData) {
  return request.post<CommonResult<Photo>>('/api/photo/upload', data, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

/**
 * 获取照片列表
 */
export function getPhotoList(params?: {
  travelRecordId?: number
  concertRecordId?: number
  pageNo?: number
  pageSize?: number
}) {
  return request.get<CommonResult<{ list: Photo[]; total: number }>>('/api/photo/list', { params })
}

/**
 * 删除照片
 */
export function deletePhoto(id: number) {
  return request.delete<CommonResult<boolean>>(`/api/photo/${id}`)
}

/**
 * 关联照片到旅游记录
 */
export function linkPhotoToTravel(photoId: number, travelRecordId: number) {
  return request.post<CommonResult<boolean>>(`/api/photo/${photoId}/link-travel`, { travelRecordId })
}

/**
 * 关联照片到观演记录
 */
export function linkPhotoToConcert(photoId: number, concertRecordId: number) {
  return request.post<CommonResult<boolean>>(`/api/photo/${photoId}/link-concert`, { concertRecordId })
}

/**
 * 取消照片关联
 */
export function unlinkPhoto(photoId: number) {
  return request.post<CommonResult<boolean>>(`/api/photo/${photoId}/unlink`)
}

