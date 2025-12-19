package com.interest.tracker.module.music.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 创建专辑记录响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "创建专辑记录响应 VO")
@Data
public class AlbumCreateRespVO {

    @Schema(description = "专辑ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long albumId;

    @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long recordId;

}

