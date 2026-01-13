import request from '@/utils/request'
import type {
  CommonResult,
  PageResult,
  DashboardSummary,
  RecentActivity,
  TimelineItem,
  TimelinePageParams,
  Insights,
} from '@/types/api'

/**
 * 获取统计概览
 */
export function getDashboardSummary(params?: {
  timeRange?: 'today' | 'week' | 'month' | 'year' | 'custom'
  startDate?: string
  endDate?: string
}) {
  return request.get<CommonResult<DashboardSummary>>('/dashboard/summary', {
    params,
  })
}

/**
 * 获取数据洞察
 */
export function getDashboardInsights(params?: {
  timeRange?: 'today' | 'week' | 'month' | 'year' | 'custom'
  startDate?: string
  endDate?: string
}) {
  return request.get<CommonResult<Insights>>('/dashboard/insights', {
    params,
  })
}

/**
 * 获取最近活动
 */
export function getRecentActivities(limit?: number) {
  return request.get<CommonResult<RecentActivity[]>>('/dashboard/recent-activities', {
    params: { limit },
  })
}

/**
 * 获取时间线数据
 */
export function getTimeline(params: TimelinePageParams) {
  return request.get<CommonResult<PageResult<TimelineItem>>>('/dashboard/timeline', {
    params,
  })
}

