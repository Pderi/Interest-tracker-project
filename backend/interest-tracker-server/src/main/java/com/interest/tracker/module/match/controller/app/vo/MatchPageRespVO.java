package com.interest.tracker.module.match.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 比赛分页列表响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "比赛分页列表响应 VO")
@Data
public class MatchPageRespVO {

    @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long recordId;

    @Schema(description = "主队名称", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "巴塞罗那")
    private String homeTeamName;

    @Schema(description = "客队名称", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "皇家马德里")
    private String awayTeamName;

    @Schema(description = "比赛日期", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private LocalDate matchDate;

    @Schema(description = "主队得分", example = "2")
    private Integer homeScore;

    @Schema(description = "客队得分", example = "1")
    private Integer awayScore;

    /**
     * 比赛类型：1-联赛 2-杯赛 3-友谊赛
     * 枚举 {@link com.interest.tracker.module.match.enums.MatchTypeEnum}
     */
    @Schema(description = "比赛类型：1-联赛 2-杯赛 3-友谊赛", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Integer matchType;

    /**
     * 观赛方式：1-现场 2-直播 3-回放
     * 枚举 {@link com.interest.tracker.module.match.enums.WatchTypeEnum}
     */
    @Schema(description = "观赛方式：1-现场 2-直播 3-回放", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "2")
    private Integer watchType;

    @Schema(description = "比赛场地", example = "老特拉福德球场")
    private String venue;

    @Schema(description = "备注", example = "精彩比赛")
    private String notes;

    @Schema(description = "创建时间", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

