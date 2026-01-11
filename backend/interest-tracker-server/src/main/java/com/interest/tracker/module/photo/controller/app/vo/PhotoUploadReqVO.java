package com.interest.tracker.module.photo.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 照片上传请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "照片上传请求 VO")
@Data
public class PhotoUploadReqVO {

    @Schema(description = "照片标题", example = "美丽的风景")
    private String title;

    @Schema(description = "照片描述", example = "拍摄于2025年1月")
    private String description;

    @Schema(description = "标签（逗号分隔）", example = "风景,自然")
    private String tags;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "关联的旅游记录ID", example = "1")
    private Long travelRecordId;

    @Schema(description = "关联的观演记录ID", example = "1")
    private Long concertRecordId;

    @Schema(description = "拍摄地点", example = "北京")
    private String location;

}

