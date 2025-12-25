package com.interest.tracker.module.book.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 更新阅读记录请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "更新阅读记录请求 VO")
@Data
public class BookRecordUpdateReqVO {

    @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "记录ID不能为空")
    private Long id;

    /**
     * 阅读状态：1-想读 2-在读 3-已读 4-弃读
     * 枚举 {@link com.interest.tracker.module.book.enums.ReadStatusEnum}
     */
    @Schema(description = "阅读状态：1-想读 2-在读 3-已读 4-弃读", example = "3")
    @Min(value = 1, message = "阅读状态最小为 1")
    @Max(value = 4, message = "阅读状态最大为 4")
    private Integer readStatus;

    @Schema(description = "个人评分（0-10）", example = "9.0")
    @DecimalMin(value = "0.0", inclusive = true, message = "个人评分不能小于 0")
    @DecimalMax(value = "10.0", inclusive = true, message = "个人评分不能大于 10")
    private BigDecimal personalRating;

    @Schema(description = "阅读日期", example = "2025-01-15")
    private LocalDate readDate;

    @Schema(description = "阅读进度（0-100）", example = "100.0")
    @DecimalMin(value = "0.0", inclusive = true, message = "阅读进度不能小于 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "阅读进度不能大于 100")
    private BigDecimal readProgress;

    @Schema(description = "评价", example = "很经典")
    private String comment;

    @Schema(description = "标签（逗号分隔）", example = "经典,文学")
    private String tags;

    @Schema(description = "封面URL（可选）", example = "https://example.com/cover.jpg")
    private String coverUrl;

}

