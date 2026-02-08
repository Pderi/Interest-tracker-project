package com.interest.tracker.module.movie.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * 创建影视记录请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "创建影视记录请求 VO")
@Data
public class MovieCreateReqVO {

    @Schema(description = "TMDB ID（从TMDB创建时必填）", example = "550")
    private Long tmdbId;

    @Schema(description = "标题（手动创建时必填）", example = "搏击俱乐部")
    private String title;

    @Schema(description = "类型：1-电影 2-电视剧", requiredMode = REQUIRED, example = "1")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @Schema(description = "观看状态：1-想看 2-在看 3-已看 4-弃剧", example = "1")
    private Integer watchStatus;

    @Schema(description = "观看日期", example = "2025-01-15")
    private LocalDate watchDate;

    @Schema(description = "个人评分（0-10）", example = "8.5")
    private BigDecimal personalRating;

    @Schema(description = "海报URL（手动创建时可选）", example = "https://example.com/poster.jpg")
    private String posterUrl;

    @Schema(description = "标签列表", example = "[\"动作\",\"悬疑\"]")
    private List<String> tags;

    @Schema(description = "评价", example = "很好看")
    private String comment;

}

