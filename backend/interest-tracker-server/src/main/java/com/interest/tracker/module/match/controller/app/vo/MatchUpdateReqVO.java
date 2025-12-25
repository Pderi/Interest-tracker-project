package com.interest.tracker.module.match.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * 更新比赛记录请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "更新比赛记录请求 VO")
@Data
public class MatchUpdateReqVO {

    @Schema(description = "记录ID", requiredMode = REQUIRED, example = "1")
    @NotNull(message = "记录ID不能为空")
    private Long id;

    @Schema(description = "主队名称", example = "巴塞罗那")
    private String homeTeamName;

    @Schema(description = "客队名称", example = "皇家马德里")
    private String awayTeamName;

    @Schema(description = "比赛日期", example = "2025-01-15")
    private LocalDate matchDate;

    @Schema(description = "主队得分", example = "2")
    private Integer homeScore;

    @Schema(description = "客队得分", example = "1")
    private Integer awayScore;

    /**
     * 比赛类型：1-联赛 2-杯赛 3-友谊赛
     * 枚举 {@link com.interest.tracker.module.match.enums.MatchTypeEnum}
     */
    @Schema(description = "比赛类型：1-联赛 2-杯赛 3-友谊赛", example = "1")
    private Integer matchType;

    /**
     * 观赛方式：1-现场 2-直播 3-回放
     * 枚举 {@link com.interest.tracker.module.match.enums.WatchTypeEnum}
     */
    @Schema(description = "观赛方式：1-现场 2-直播 3-回放", example = "2")
    private Integer watchType;

    @Schema(description = "比赛场地", example = "老特拉福德球场")
    private String venue;

    @Schema(description = "备注", example = "精彩比赛")
    private String notes;

}

