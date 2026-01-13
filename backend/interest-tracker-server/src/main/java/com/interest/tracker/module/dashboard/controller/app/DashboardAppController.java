package com.interest.tracker.module.dashboard.controller.app;

import com.interest.tracker.framework.common.pojo.CommonResult;
import com.interest.tracker.module.dashboard.controller.app.vo.DashboardSummaryReqVO;
import com.interest.tracker.module.dashboard.controller.app.vo.DashboardSummaryRespVO;
import com.interest.tracker.module.dashboard.controller.app.vo.InsightsRespVO;
import com.interest.tracker.module.dashboard.controller.app.vo.RecentActivityRespVO;
import com.interest.tracker.module.dashboard.controller.app.vo.TimelineItemRespVO;
import com.interest.tracker.module.dashboard.controller.app.vo.TimelinePageReqVO;
import com.interest.tracker.module.dashboard.service.DashboardService;
import com.interest.tracker.framework.common.pojo.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.interest.tracker.framework.common.pojo.CommonResult.success;

/**
 * Dashboard App - 仪表盘接口
 *
 * @author interest-tracker
 */
@Tag(name = "Dashboard App - 仪表盘")
@RestController
@RequestMapping("/api/dashboard")
@Validated
public class DashboardAppController {

    @Resource
    private DashboardService dashboardService;

    /**
     * 获取统计概览
     */
    @GetMapping("/summary")
    @Operation(summary = "获取统计概览")
    public CommonResult<DashboardSummaryRespVO> getSummary(@Valid DashboardSummaryReqVO reqVO) {
        DashboardSummaryRespVO result = dashboardService.getSummary(reqVO);
        return success(result);
    }

    /**
     * 获取数据洞察
     */
    @GetMapping("/insights")
    @Operation(summary = "获取数据洞察")
    public CommonResult<InsightsRespVO> getInsights(@Valid DashboardSummaryReqVO reqVO) {
        InsightsRespVO result = dashboardService.getInsights(reqVO);
        return success(result);
    }

    /**
     * 获取最近活动
     */
    @GetMapping("/recent-activities")
    @Operation(summary = "获取最近活动")
    public CommonResult<List<RecentActivityRespVO>> getRecentActivities(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit
    ) {
        List<RecentActivityRespVO> result = dashboardService.getRecentActivities(limit);
        return success(result);
    }

    /**
     * 获取时间线数据
     */
    @GetMapping("/timeline")
    @Operation(summary = "获取时间线数据")
    public CommonResult<PageResult<TimelineItemRespVO>> getTimeline(@Valid TimelinePageReqVO reqVO) {
        PageResult<TimelineItemRespVO> result = dashboardService.getTimeline(reqVO);
        return success(result);
    }

}

