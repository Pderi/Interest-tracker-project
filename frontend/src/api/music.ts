import request from '@/utils/request'
import type {
  CommonResult,
  PageResult,
  AlbumPageItem,
  AlbumDetail,
  AlbumCreateReq,
  AlbumCreateRes,
  AlbumRecordUpdateReq,
  AlbumPageWithStats,
} from '@/types/api'

// 分页获取当前用户的专辑 + 听歌记录列表
export function getAlbumPage(params: {
  pageNo?: number
  pageSize?: number
  status?: number
  artist?: string
  genre?: string
  tag?: string
  keyword?: string
  startListenDate?: string
  endListenDate?: string
  sort?: string
}) {
  return request.get<CommonResult<AlbumPageWithStats>>('/music/albums', {
    params,
  })
}

// 创建专辑记录（含听歌记录）
export function createAlbum(data: AlbumCreateReq) {
  return request.post<CommonResult<AlbumCreateRes>>('/music/albums', data)
}

// 获取专辑详情
export function getAlbumDetail(id: number) {
  return request.get<CommonResult<AlbumDetail>>(`/music/albums/${id}`)
}

// 更新听歌记录
export function updateAlbumRecord(recordId: number, data: AlbumRecordUpdateReq) {
  return request.put<CommonResult<boolean>>(`/music/albums/records/${recordId}`, data)
}

// 更新专辑信息
export function updateAlbum(albumId: number, data: {
  title?: string
  artist?: string
  releaseYear?: number
  genre?: string
  description?: string
  coverUrl?: string
  totalTracks?: number
  duration?: number
}) {
  return request.put<CommonResult<boolean>>(`/music/albums/${albumId}`, { id: albumId, ...data })
}

// 删除听歌记录
export function deleteAlbumRecord(recordId: number) {
  return request.delete<CommonResult<boolean>>(`/music/albums/records/${recordId}`)
}

