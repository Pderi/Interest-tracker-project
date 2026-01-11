package com.interest.tracker.module.photo.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 照片详情响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "照片详情响应 VO")
@Data
public class PhotoRespVO {

    @Schema(description = "照片ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "照片标题", example = "美丽的风景")
    private String title;

    @Schema(description = "照片描述", example = "拍摄于2025年1月")
    private String description;

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

    @Schema(description = "MIME类型", example = "image/jpeg")
    private String mimeType;

    @Schema(description = "EXIF数据（JSON格式）")
    private String exifData;

    @Schema(description = "拍摄地点", example = "北京")
    private String location;

    @Schema(description = "标签（逗号分隔）", example = "风景,自然")
    private String tags;

    @Schema(description = "分类名称", example = "风景")
    private String category;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "拍摄时间", example = "2025-01-15T10:30:00")
    private LocalDateTime shootTime;

    @Schema(description = "关联的旅游记录ID", example = "1")
    private Long travelRecordId;

    @Schema(description = "关联的观演记录ID", example = "1")
    private Long concertRecordId;

    @Schema(description = "查看次数", example = "10")
    private Integer viewCount;

    @Schema(description = "点赞次数", example = "5")
    private Integer likeCount;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

}

