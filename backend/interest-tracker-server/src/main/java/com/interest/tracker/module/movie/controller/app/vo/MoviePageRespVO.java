package com.interest.tracker.module.movie.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 影视分页列表响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "影视分页列表响应 VO")
@Data
public class MoviePageRespVO {

    @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long recordId;

    @Schema(description = "影视ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long movieId;

    @Schema(description = "标题", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "搏击俱乐部")
    private String title;

    @Schema(description = "类型：1-电影 2-电视剧", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Integer type;

    @Schema(description = "海报URL", example = "https://image.tmdb.org/t/p/w500/xxx.jpg")
    private String posterUrl;

    @Schema(description = "观看状态：1-想看 2-在看 3-已看 4-弃剧", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "3")
    private Integer watchStatus;

    @Schema(description = "个人评分（0-10）", example = "8.5")
    private BigDecimal personalRating;

    @Schema(description = "观看日期", example = "2025-01-15")
    private LocalDate watchDate;

    @Schema(description = "标签（逗号分隔）", example = "动作,悬疑")
    private String tags;

    @Schema(description = "评价", example = "很好看")
    private String comment;

    @Schema(description = "创建时间", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

