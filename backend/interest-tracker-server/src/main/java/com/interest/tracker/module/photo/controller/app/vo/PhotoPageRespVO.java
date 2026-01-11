package com.interest.tracker.module.photo.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 照片分页列表响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "照片分页列表响应 VO")
@Data
public class PhotoPageRespVO {

    @Schema(description = "照片ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "照片标题", example = "美丽的风景")
    private String title;

    @Schema(description = "文件路径（COS URL）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String filePath;

    @Schema(description = "缩略图路径（COS URL）")
    private String thumbnailPath;

    @Schema(description = "分类名称", example = "风景")
    private String category;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "拍摄时间", example = "2025-01-15T10:30:00")
    private LocalDateTime shootTime;

    @Schema(description = "拍摄地点", example = "北京")
    private String location;

    @Schema(description = "标签（逗号分隔）", example = "风景,自然")
    private String tags;

    @Schema(description = "查看次数", example = "10")
    private Integer viewCount;

    @Schema(description = "点赞次数", example = "5")
    private Integer likeCount;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

