package com.interest.tracker.module.concert.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 创建演唱会记录响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "创建演唱会记录响应 VO")
@Data
public class ConcertCreateRespVO {

    @Schema(description = "演唱会ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long concertId;

    @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long recordId;

}

