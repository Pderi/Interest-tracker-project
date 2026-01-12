package com.interest.tracker.module.travel.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 创建旅游记录响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "创建旅游记录响应 VO")
@Data
public class TravelCreateRespVO {

    @Schema(description = "地点ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long placeId;

    @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long recordId;

}

