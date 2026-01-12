package com.interest.tracker.module.travel.controller.app.vo;

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
 * 更新旅游记录请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "更新旅游记录请求 VO")
@Data
public class TravelRecordUpdateReqVO {

    @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "记录ID不能为空")
    private Long id;

    @Schema(description = "旅游状态：1-想去 2-计划中 3-已去", example = "3")
    @Min(value = 1, message = "旅游状态最小为 1")
    @Max(value = 3, message = "旅游状态最大为 3")
    private Integer travelStatus;

    @Schema(description = "个人评分（0-10）", example = "8.5")
    @DecimalMin(value = "0.0", inclusive = true, message = "个人评分不能小于 0")
    @DecimalMax(value = "10.0", inclusive = true, message = "个人评分不能大于 10")
    private BigDecimal personalRating;

    @Schema(description = "旅游日期", example = "2025-06-15")
    private LocalDate travelDate;

    @Schema(description = "旅游天数", example = "3")
    private Integer travelDuration;

    @Schema(description = "费用", example = "5000.00")
    private BigDecimal expense;

    @Schema(description = "评价", example = "很棒的旅行")
    private String comment;

}

