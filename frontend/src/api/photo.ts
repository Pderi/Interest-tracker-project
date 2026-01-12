import request from '@/utils/request'
import type { CommonResult, PageResult } from '@/types/api'

/**
 * 照片信息
 */
export interface Photo {
  id: number
  title?: string
  description?: string
  filePath: string
  thumbnailPath?: string
  fileSize?: number
  width?: number
  height?: number
  mimeType?: string
  location?: string
  tags?: string
  category?: string
  categoryId?: number
  shootTime?: string
  travelRecordId?: number
  concertRecordId?: number
  viewCount?: number
  likeCount?: number
  createTime: string
  updateTime?: string
}

/**
 * 照片分页列表项
 */
export interface PhotoPageItem {
  id: number
  title?: string
  filePath: string
  thumbnailPath?: string
  category?: string
  categoryId?: number
  shootTime?: string
  location?: string
  tags?: string
  viewCount?: number
  likeCount?: number
  createTime: string
}

/**
 * 照片详情
 */
export interface PhotoDetail extends Photo {
  exifData?: string
  createTime: string
  updateTime: string
}

/**
 * 照片上传请求
 */
export interface PhotoUploadReq {
  title?: string
  description?: string
  tags?: string
  categoryId?: number
  travelRecordId?: number
  concertRecordId?: number
  location?: string
}

/**
 * 照片上传响应
 */
export interface PhotoUploadResp {
  id: number
  filePath: string
  thumbnailPath?: string
  fileSize?: number
  width?: number
  height?: number
  createTime: string
}

/**
 * 照片更新请求
 */
export interface PhotoUpdateReq {
  id?: number // 虽然从路径参数传递，但后端验证需要此字段
  title?: string
  description?: string
  tags?: string
  categoryId?: number
  travelRecordId?: number | null
  concertRecordId?: number | null
  location?: string
}

/**
 * 照片分页查询参数
 */
export interface PhotoPageParams {
  pageNo?: number
  pageSize?: number
  categoryId?: number
  travelRecordId?: number
  concertRecordId?: number
  unlinkedOnly?: boolean
  shootTimeStart?: string
  shootTimeEnd?: string
  keyword?: string
}

/**
 * 照片分类信息
 */
export interface PhotoCategory {
  id: number
  name: string
  color?: string
  icon?: string
  description?: string
  sortOrder: number
  photoCount: number
  createTime: string
  updateTime: string
}

/**
 * 创建分类请求
 */
export interface PhotoCategoryCreateReq {
  name: string
  color?: string
  icon?: string
  description?: string
  sortOrder?: number
}

/**
 * 更新分类请求
 */
export interface PhotoCategoryUpdateReq {
  name?: string
  color?: string
  icon?: string
  description?: string
  sortOrder?: number
}

/**
 * 上传照片
 */
export function uploadPhoto(file: File, data?: PhotoUploadReq) {
  const formData = new FormData()
  formData.append('file', file)
  
  if (data) {
    if (data.title) formData.append('title', data.title)
    if (data.description) formData.append('description', data.description)
    if (data.tags) formData.append('tags', data.tags)
    if (data.categoryId) formData.append('categoryId', data.categoryId.toString())
    if (data.travelRecordId) formData.append('travelRecordId', data.travelRecordId.toString())
    if (data.concertRecordId) formData.append('concertRecordId', data.concertRecordId.toString())
    if (data.location) formData.append('location', data.location)
  }
  
  return request.post<CommonResult<PhotoUploadResp>>('/photos/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

/**
 * 批量上传照片
 */
export function batchUploadPhotos(files: File[], data?: PhotoUploadReq) {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  
  if (data) {
    if (data.categoryId) formData.append('categoryId', data.categoryId.toString())
    if (data.travelRecordId) formData.append('travelRecordId', data.travelRecordId.toString())
    if (data.concertRecordId) formData.append('concertRecordId', data.concertRecordId.toString())
  }
  
  return request.post<CommonResult<PhotoUploadResp[]>>('/photos/batch-upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

/**
 * 获取照片详情
 */
export function getPhotoDetail(id: number) {
  return request.get<CommonResult<PhotoDetail>>(`/photos/${id}`)
}

/**
 * 更新照片信息
 */
export function updatePhoto(id: number, data: PhotoUpdateReq) {
  return request.put<CommonResult<boolean>>(`/photos/${id}`, data)
}

/**
 * 删除照片
 */
export function deletePhoto(id: number) {
  return request.delete<CommonResult<boolean>>(`/photos/${id}`)
}

/**
 * 获取照片列表（分页）
 */
export function getPhotoPage(params?: PhotoPageParams) {
  return request.get<CommonResult<PageResult<PhotoPageItem>>>('/photos', { params })
}

/**
 * 创建照片分类
 */
export function createPhotoCategory(data: PhotoCategoryCreateReq) {
  return request.post<CommonResult<PhotoCategory>>('/photos/categories', data)
}

/**
 * 更新照片分类
 */
export function updatePhotoCategory(id: number, data: PhotoCategoryUpdateReq) {
  return request.put<CommonResult<boolean>>(`/photos/categories/${id}`, data)
}

/**
 * 删除照片分类
 */
export function deletePhotoCategory(id: number) {
  return request.delete<CommonResult<boolean>>(`/photos/categories/${id}`)
}

/**
 * 获取分类详情
 */
export function getPhotoCategoryDetail(id: number) {
  return request.get<CommonResult<PhotoCategory>>(`/photos/categories/${id}`)
}

/**
 * 获取用户的所有分类列表
 */
export function getPhotoCategoryList() {
  return request.get<CommonResult<PhotoCategory[]>>('/photos/categories')
}

/**
 * 更新分类排序
 */
export function updatePhotoCategorySort(categoryIds: number[]) {
  return request.put<CommonResult<boolean>>('/photos/categories/sort', categoryIds)
}

/**
 * 上传封面图片（仅上传文件，不创建照片记录）
 * 供其他模块（影视、音乐、图书）使用
 */
export function uploadCoverImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request.post<CommonResult<string>>('/photos/upload-cover', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}
