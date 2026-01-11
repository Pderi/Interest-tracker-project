package com.interest.tracker.module.photo.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 照片上传响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "照片上传响应 VO")
@Data
public class PhotoUploadRespVO {

    @Schema(description = "照片ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "文件路径（COS URL）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String filePath;

    @Schema(description = "缩略图路径（COS URL）")
    private String thumbnailPath;

    @Schema(description = "文件大小（字节）", example = "1024000")
    private Long fileSize;

    @Schema(description = "图片宽度（像素）", example = "1920")
    private Integer width;

    @Schema(description = "图片高度（像素）", example = "1080")
    private Integer height;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

