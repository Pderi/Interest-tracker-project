package com.interest.tracker.module.dashboard.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * Dashboard 统计请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "Dashboard 统计请求 VO")
@Data
public class DashboardSummaryReqVO {

    @Schema(description = "时间范围类型：today-今日, week-本周, month-本月, year-本年, custom-自定义", example = "week")
    private String timeRange = "week";

    @Schema(description = "自定义开始日期（当timeRange为custom时必填）", example = "2024-01-01")
    private LocalDate startDate;

    @Schema(description = "自定义结束日期（当timeRange为custom时必填）", example = "2024-01-31")
    private LocalDate endDate;

}

