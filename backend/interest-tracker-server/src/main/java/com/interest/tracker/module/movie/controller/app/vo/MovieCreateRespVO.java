package com.interest.tracker.module.movie.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 创建影视记录响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "创建影视记录响应 VO")
@Data
public class MovieCreateRespVO {

    @Schema(description = "影视ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long movieId;

    @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long recordId;

}

