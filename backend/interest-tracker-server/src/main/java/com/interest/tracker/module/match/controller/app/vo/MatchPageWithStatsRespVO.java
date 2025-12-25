package com.interest.tracker.module.match.controller.app.vo;

import com.interest.tracker.framework.common.pojo.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 比赛分页列表响应 VO（包含统计信息）
 *
 * @author interest-tracker
 */
@Schema(description = "比赛分页列表响应 VO（包含统计信息）")
@Data
public class MatchPageWithStatsRespVO {

    @Schema(description = "分页数据", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private PageResult<MatchPageRespVO> page;

    @Schema(description = "各类型数量统计：key为类型值（1-联赛 2-杯赛 3-友谊赛），value为数量", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private Map<Integer, Long> typeCounts;

}

