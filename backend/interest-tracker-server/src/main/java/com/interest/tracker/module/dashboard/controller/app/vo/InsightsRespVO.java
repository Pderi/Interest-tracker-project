package com.interest.tracker.module.dashboard.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 数据洞察响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "数据洞察响应 VO")
@Data
public class InsightsRespVO {

    @Schema(description = "最活跃类型")
    private MostActiveType mostActiveType;

    @Schema(description = "评分趋势")
    private RatingTrend ratingTrend;

    @Schema(description = "活跃度峰值")
    private PeakActivity peakActivity;

    @Schema(description = "完成率")
    private CompletionRate completionRate;

    @Data
    @Schema(description = "最活跃类型")
    public static class MostActiveType {
        @Schema(description = "类型：photo, movie, music, book, travel, concert, match")
        private String type;

        @Schema(description = "类型中文名称")
        private String typeName;

        @Schema(description = "新增数量")
        private Long count;

        @Schema(description = "占比（百分比）")
        private BigDecimal percentage;
    }

    @Data
    @Schema(description = "评分趋势")
    public static class RatingTrend {
        @Schema(description = "当前平均评分")
        private BigDecimal currentAvgRating;

        @Schema(description = "上期平均评分")
        private BigDecimal previousAvgRating;

        @Schema(description = "变化百分比（正数表示上升，负数表示下降）")
        private BigDecimal changePercentage;

        @Schema(description = "趋势：up-上升, down-下降, stable-稳定")
        private String trend;
    }

    @Data
    @Schema(description = "活跃度峰值")
    public static class PeakActivity {
        @Schema(description = "峰值日期")
        private String date;

        @Schema(description = "峰值数量")
        private Long count;

        @Schema(description = "峰值类型（如果有多个类型，显示主要类型）")
        private String type;

        @Schema(description = "类型中文名称")
        private String typeName;
    }

    @Data
    @Schema(description = "完成率")
    public static class CompletionRate {
        @Schema(description = "已完成数量")
        private Long completed;

        @Schema(description = "总计划数量（已完成 + 进行中 + 计划中）")
        private Long total;

        @Schema(description = "完成率（百分比）")
        private BigDecimal percentage;

        @Schema(description = "类型：movie, music, book")
        private String type;

        @Schema(description = "类型中文名称")
        private String typeName;
    }

}

