package com.interest.tracker.module.concert.controller.app.vo;

import com.interest.tracker.framework.common.pojo.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 演唱会分页列表响应 VO（包含统计信息）
 *
 * @author interest-tracker
 */
@Schema(description = "演唱会分页列表响应 VO（包含统计信息）")
@Data
public class ConcertPageWithStatsRespVO {

    @Schema(description = "分页数据", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private PageResult<ConcertPageRespVO> page;

    @Schema(description = "各状态数量统计：key为状态值（1-想看 2-已看），value为数量", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private Map<Integer, Long> statusCounts;

}

