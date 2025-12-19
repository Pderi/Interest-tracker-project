package com.interest.tracker.module.movie.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 影视详情响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "影视详情响应 VO")
@Data
public class MovieRespVO {

    @Schema(description = "影视信息")
    private MovieInfo movie;

    @Schema(description = "观看记录")
    private RecordInfo record;

    /**
     * 影视信息
     */
    @Data
    @Schema(description = "影视信息")
    public static class MovieInfo {
        @Schema(description = "影视ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "标题", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "搏击俱乐部")
        private String title;

        @Schema(description = "类型：1-电影 2-电视剧", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
        private Integer type;

        @Schema(description = "类型（动作、科幻等，逗号分隔）", example = "动作,悬疑")
        private String genre;

        @Schema(description = "上映年份", example = "1999")
        private Integer releaseYear;

        @Schema(description = "导演", example = "大卫·芬奇")
        private String director;

        @Schema(description = "演员（逗号分隔）", example = "爱德华·诺顿,布拉德·皮特")
        private String actors;

        @Schema(description = "简介", example = "简介...")
        private String description;

        @Schema(description = "海报URL", example = "https://image.tmdb.org/t/p/w500/xxx.jpg")
        private String posterUrl;

        @Schema(description = "评分（0-10）", example = "8.4")
        private BigDecimal rating;

        @Schema(description = "时长（分钟）", example = "139")
        private Integer duration;
    }

    /**
     * 观看记录信息
     */
    @Data
    @Schema(description = "观看记录信息")
    public static class RecordInfo {
        @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "观看状态：1-想看 2-在看 3-已看 4-弃剧", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "3")
        private Integer watchStatus;

        @Schema(description = "个人评分（0-10）", example = "8.5")
        private BigDecimal personalRating;

        @Schema(description = "观看日期", example = "2025-01-15")
        private LocalDate watchDate;

        @Schema(description = "观看时长（分钟）", example = "139")
        private Integer watchDuration;

        @Schema(description = "观看进度（0-100）", example = "100.0")
        private BigDecimal progress;

        @Schema(description = "评价", example = "很好看")
        private String comment;

        @Schema(description = "标签（逗号分隔）", example = "动作,悬疑")
        private String tags;
    }

}

