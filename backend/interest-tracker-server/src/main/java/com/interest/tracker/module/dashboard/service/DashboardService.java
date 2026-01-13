package com.interest.tracker.module.dashboard.service;

import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.module.dashboard.controller.app.vo.DashboardSummaryReqVO;
import com.interest.tracker.module.dashboard.controller.app.vo.DashboardSummaryRespVO;
import com.interest.tracker.module.dashboard.controller.app.vo.InsightsRespVO;
import com.interest.tracker.module.dashboard.controller.app.vo.RecentActivityRespVO;
import com.interest.tracker.module.dashboard.controller.app.vo.TimelineItemRespVO;
import com.interest.tracker.module.dashboard.controller.app.vo.TimelinePageReqVO;

import java.util.List;

/**
 * Dashboard Service 接口
 *
 * @author interest-tracker
 */
public interface DashboardService {

    /**
     * 获取统计概览
     *
     * @param reqVO 请求参数（包含时间范围）
     * @return 统计概览数据
     */
    DashboardSummaryRespVO getSummary(DashboardSummaryReqVO reqVO);

    /**
     * 获取数据洞察
     *
     * @param reqVO 请求参数（包含时间范围）
     * @return 数据洞察
     */
    InsightsRespVO getInsights(DashboardSummaryReqVO reqVO);

    /**
     * 获取最近活动
     *
     * @param limit 返回数量限制
     * @return 最近活动列表
     */
    List<RecentActivityRespVO> getRecentActivities(Integer limit);

    /**
     * 获取时间线数据
     *
     * @param reqVO 分页请求参数
     * @return 时间线数据分页结果
     */
    PageResult<TimelineItemRespVO> getTimeline(TimelinePageReqVO reqVO);

}

