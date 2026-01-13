package com.interest.tracker.module.dashboard.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Dashboard 统计概览响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "Dashboard 统计概览响应 VO")
@Data
public class DashboardSummaryRespVO {

    @Schema(description = "照片总数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long photoCount;

    @Schema(description = "影视总数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long movieCount;

    @Schema(description = "音乐总数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long musicCount;

    @Schema(description = "阅读总数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long bookCount;

    @Schema(description = "旅游总数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long travelCount;

    @Schema(description = "演唱会总数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long concertCount;

    @Schema(description = "球赛总数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long matchCount;

    @Schema(description = "本周新增统计")
    private WeeklyStats weeklyStats;

    @Schema(description = "上周新增统计（用于计算增长率）")
    private WeeklyStats lastWeekStats;

    @Schema(description = "本月新增统计")
    private MonthlyStats monthlyStats;

    @Schema(description = "今日统计")
    private TodayStats todayStats;

    @Schema(description = "影视状态统计：key为状态值（1-想看 2-在看 3-已看 4-弃剧），value为数量")
    private java.util.Map<Integer, Long> movieStatusCounts;

    @Schema(description = "音乐状态统计：key为状态值（1-想听 2-在听 3-已听 4-弃听），value为数量")
    private java.util.Map<Integer, Long> musicStatusCounts;

    @Schema(description = "阅读状态统计：key为状态值（1-想读 2-在读 3-已读 4-弃读），value为数量")
    private java.util.Map<Integer, Long> bookStatusCounts;

    @Schema(description = "最近7天趋势数据（用于图表展示）")
    private TrendData trendData;

    @Data
    @Schema(description = "本周新增统计")
    public static class WeeklyStats {
        @Schema(description = "本周新增照片数")
        private Long photo;

        @Schema(description = "本周新增影视数")
        private Long movie;

        @Schema(description = "本周新增音乐数")
        private Long music;

        @Schema(description = "本周新增阅读数")
        private Long book;

        @Schema(description = "本周新增旅游数")
        private Long travel;

        @Schema(description = "本周新增演唱会数")
        private Long concert;

        @Schema(description = "本周新增球赛数")
        private Long match;
    }

    @Data
    @Schema(description = "本月新增统计")
    public static class MonthlyStats {
        @Schema(description = "本月新增照片数")
        private Long photo;

        @Schema(description = "本月新增影视数")
        private Long movie;

        @Schema(description = "本月新增音乐数")
        private Long music;

        @Schema(description = "本月新增阅读数")
        private Long book;

        @Schema(description = "本月新增旅游数")
        private Long travel;

        @Schema(description = "本月新增演唱会数")
        private Long concert;

        @Schema(description = "本月新增球赛数")
        private Long match;
    }

    @Data
    @Schema(description = "今日统计")
    public static class TodayStats {
        @Schema(description = "今日新增照片数")
        private Long photo;

        @Schema(description = "今日新增影视数")
        private Long movie;

        @Schema(description = "今日新增音乐数")
        private Long music;

        @Schema(description = "今日新增阅读数")
        private Long book;

        @Schema(description = "今日新增旅游数")
        private Long travel;

        @Schema(description = "今日新增演唱会数")
        private Long concert;

        @Schema(description = "今日新增球赛数")
        private Long match;
    }

    @Data
    @Schema(description = "趋势数据")
    public static class TrendData {
        @Schema(description = "日期列表（最近7天，格式：MM-dd）")
        private java.util.List<String> dates;

        @Schema(description = "照片每日新增数量（对应dates的顺序）")
        private java.util.List<Long> photo;

        @Schema(description = "影视每日新增数量")
        private java.util.List<Long> movie;

        @Schema(description = "音乐每日新增数量")
        private java.util.List<Long> music;

        @Schema(description = "阅读每日新增数量")
        private java.util.List<Long> book;

        @Schema(description = "旅游每日新增数量")
        private java.util.List<Long> travel;

        @Schema(description = "演唱会每日新增数量")
        private java.util.List<Long> concert;

        @Schema(description = "球赛每日新增数量")
        private java.util.List<Long> match;
    }

}

