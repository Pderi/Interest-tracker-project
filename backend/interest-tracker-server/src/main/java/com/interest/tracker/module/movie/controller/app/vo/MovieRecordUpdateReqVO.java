package com.interest.tracker.module.movie.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 更新观看记录请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "更新观看记录请求 VO")
@Data
public class MovieRecordUpdateReqVO {

    @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "记录ID不能为空")
    private Long id;

    @Schema(description = "观看状态：1-想看 2-在看 3-已看 4-弃剧", example = "3")
    @Min(value = 1, message = "观看状态最小为 1")
    @Max(value = 4, message = "观看状态最大为 4")
    private Integer watchStatus;

    @Schema(description = "个人评分（0-10）", example = "8.5")
    @DecimalMin(value = "0.0", inclusive = true, message = "个人评分不能小于 0")
    @DecimalMax(value = "10.0", inclusive = true, message = "个人评分不能大于 10")
    private BigDecimal personalRating;

    @Schema(description = "观看日期", example = "2025-01-15")
    private LocalDate watchDate;

    @Schema(description = "观看时长（分钟）", example = "139")
    private Integer watchDuration;

    @Schema(description = "观看进度（0-100）", example = "100.0")
    @DecimalMin(value = "0.0", inclusive = true, message = "观看进度不能小于 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "观看进度不能大于 100")
    private BigDecimal progress;

    @Schema(description = "评价", example = "很好看")
    private String comment;

    @Schema(description = "标签列表", example = "[\"动作\",\"悬疑\"]")
    private List<String> tags;

    @Schema(description = "海报URL（可选）", example = "https://example.com/poster.jpg")
    private String posterUrl;

}

