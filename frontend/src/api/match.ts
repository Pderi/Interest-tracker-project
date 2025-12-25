import request from '@/utils/request'
import type {
  CommonResult,
  PageResult,
  MatchPageItem,
  MatchPageWithStats,
  MatchDetail,
  MatchCreateReq,
  MatchCreateRes,
  MatchUpdateReq,
} from '@/types/api'

// 分页获取当前用户的比赛记录列表
export function getMatchPage(params: {
  pageNo?: number
  pageSize?: number
  matchType?: number
  watchType?: number
  homeTeamId?: number
  awayTeamId?: number
  venue?: string
  startMatchDate?: string
  endMatchDate?: string
}) {
  return request.get<CommonResult<MatchPageWithStats>>('/api/matches', {
    params,
  })
}

// 创建比赛记录
export function createMatch(data: MatchCreateReq) {
  return request.post<CommonResult<MatchCreateRes>>('/api/matches', data)
}

// 获取比赛详情
export function getMatchDetail(id: number) {
  return request.get<CommonResult<MatchDetail>>(`/api/matches/${id}`)
}

// 更新比赛记录
export function updateMatch(matchId: number, data: MatchUpdateReq) {
  return request.put<CommonResult<boolean>>(`/api/matches/${matchId}`, data)
}

// 删除比赛记录
export function deleteMatch(matchId: number) {
  return request.delete<CommonResult<boolean>>(`/api/matches/${matchId}`)
}

