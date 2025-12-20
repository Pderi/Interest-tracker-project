package com.interest.tracker.module.music.controller.app.vo;

import com.interest.tracker.framework.common.pojo.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 专辑分页列表响应 VO（包含统计信息）
 *
 * @author interest-tracker
 */
@Schema(description = "专辑分页列表响应 VO（包含统计信息）")
@Data
public class AlbumPageWithStatsRespVO {

    @Schema(description = "分页数据", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private PageResult<AlbumPageRespVO> page;

    @Schema(description = "各状态数量统计：key为状态值（1-想听 2-在听 3-已听 4-弃听），value为数量", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private Map<Integer, Long> statusCounts;

}

