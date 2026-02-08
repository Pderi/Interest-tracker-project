package com.interest.tracker.module.music.controller.app.vo;

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
 * 更新听歌记录请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "更新听歌记录请求 VO")
@Data
public class AlbumRecordUpdateReqVO {

    @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "记录ID不能为空")
    private Long id;

    @Schema(description = "听歌状态：1-想听 2-在听 3-已听 4-弃听", example = "3")
    @Min(value = 1, message = "听歌状态最小为 1")
    @Max(value = 4, message = "听歌状态最大为 4")
    private Integer listenStatus;

    @Schema(description = "个人评分（0-10）", example = "8.5")
    @DecimalMin(value = "0.0", inclusive = true, message = "个人评分不能小于 0")
    @DecimalMax(value = "10.0", inclusive = true, message = "个人评分不能大于 10")
    private BigDecimal personalRating;

    @Schema(description = "听歌日期", example = "2025-01-15")
    private LocalDate listenDate;

    @Schema(description = "听歌次数", example = "10")
    private Integer listenCount;

    @Schema(description = "评价", example = "很好听")
    private String comment;

    @Schema(description = "标签列表", example = "[\"摇滚\",\"经典\"]")
    private List<String> tags;

}

