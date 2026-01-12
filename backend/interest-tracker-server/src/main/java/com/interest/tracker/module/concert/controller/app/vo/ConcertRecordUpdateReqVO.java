package com.interest.tracker.module.concert.controller.app.vo;

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
 * 更新观演记录请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "更新观演记录请求 VO")
@Data
public class ConcertRecordUpdateReqVO {

    @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "记录ID不能为空")
    private Long id;

    @Schema(description = "观演状态：1-想看 2-已看", example = "2")
    @Min(value = 1, message = "观演状态最小为 1")
    @Max(value = 2, message = "观演状态最大为 2")
    private Integer watchStatus;

    @Schema(description = "个人评分（0-10）", example = "8.5")
    @DecimalMin(value = "0.0", inclusive = true, message = "个人评分不能小于 0")
    @DecimalMax(value = "10.0", inclusive = true, message = "个人评分不能大于 10")
    private BigDecimal personalRating;

    @Schema(description = "观演日期", example = "2025-06-15")
    private LocalDate watchDate;

    @Schema(description = "票价", example = "580.00")
    private BigDecimal ticketPrice;

    @Schema(description = "座位信息", example = "A区1排1号")
    private String seatInfo;

    @Schema(description = "评价", example = "很棒的演出")
    private String comment;

}

