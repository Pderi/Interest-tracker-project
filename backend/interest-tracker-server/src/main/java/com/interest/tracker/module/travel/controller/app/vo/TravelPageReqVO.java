package com.interest.tracker.module.travel.controller.app.vo;

import com.interest.tracker.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 旅游分页查询请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "旅游分页查询请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class TravelPageReqVO extends PageParam {

    @Schema(description = "用户ID（系统自动填充）", hidden = true)
    private Long userId;

    @Schema(description = "旅游状态：1-想去 2-计划中 3-已去", example = "3")
    private Integer status;

    @Schema(description = "国家筛选", example = "中国")
    private String country;

    @Schema(description = "城市筛选", example = "北京")
    private String city;

    @Schema(description = "地点类型筛选：1-城市 2-景点 3-国家 4-其他", example = "1")
    private Integer placeType;

    @Schema(description = "标签筛选", example = "历史")
    private String tag;

    @Schema(description = "关键词搜索（地点名称、城市、国家）", example = "北京")
    private String keyword;

    @Schema(description = "开始旅游日期", example = "2025-01-01")
    private LocalDate startTravelDate;

    @Schema(description = "结束旅游日期", example = "2025-12-31")
    private LocalDate endTravelDate;

    @Schema(description = "排序字段：ctime-创建时间, travelDate-旅游日期, rating-评分", example = "ctime")
    private String sort;

}

