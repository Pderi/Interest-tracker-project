import request from '@/utils/request'
import type {
  CommonResult,
  PageResult,
  BookPageItem,
  BookDetail,
  BookCreateReq,
  BookCreateRes,
  BookRecordUpdateReq,
  BookPageWithStats,
} from '@/types/api'

// 分页获取当前用户的图书 + 阅读记录列表
export function getBookPage(params: {
  pageNo?: number
  pageSize?: number
  readStatus?: number
  tag?: string
  keyword?: string
  author?: string
  startReadDate?: string
  endReadDate?: string
  sort?: string
}) {
  return request.get<CommonResult<BookPageWithStats>>('/api/books', {
    params,
  })
}

// 创建图书记录（含阅读记录）
export function createBook(data: BookCreateReq) {
  return request.post<CommonResult<BookCreateRes>>('/api/books', data)
}

// 获取图书详情
export function getBookDetail(id: number) {
  return request.get<CommonResult<BookDetail>>(`/api/books/${id}`)
}

// 更新阅读记录
export function updateBookRecord(recordId: number, data: BookRecordUpdateReq) {
  return request.put<CommonResult<boolean>>(`/api/books/records/${recordId}`, data)
}

// 删除阅读记录
export function deleteBookRecord(recordId: number) {
  return request.delete<CommonResult<boolean>>(`/api/books/records/${recordId}`)
}

