package com.interest.tracker.module.travel.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 旅游分页列表响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "旅游分页列表响应 VO")
@Data
public class TravelPageRespVO {

    @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long recordId;

    @Schema(description = "地点ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long placeId;

    @Schema(description = "地点名称", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "北京")
    private String name;

    @Schema(description = "国家/地区", example = "中国")
    private String country;

    @Schema(description = "城市", example = "北京")
    private String city;

    @Schema(description = "详细地址", example = "北京市东城区天安门广场")
    private String address;

    @Schema(description = "地点类型：1-城市 2-景点 3-国家 4-其他", example = "1")
    private Integer placeType;

    @Schema(description = "封面URL", example = "https://example.com/cover.jpg")
    private String coverUrl;

    @Schema(description = "旅游状态：1-想去 2-计划中 3-已去", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "3")
    private Integer travelStatus;

    @Schema(description = "个人评分（0-10）", example = "8.5")
    private BigDecimal personalRating;

    @Schema(description = "旅游日期", example = "2025-06-15")
    private LocalDate travelDate;

    @Schema(description = "旅游天数", example = "3")
    private Integer travelDuration;

    @Schema(description = "费用", example = "5000.00")
    private BigDecimal expense;

    @Schema(description = "评价", example = "很棒的旅行")
    private String comment;

    @Schema(description = "创建时间", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

