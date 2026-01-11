package com.interest.tracker.module.photo.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * 创建照片分类请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "创建照片分类请求 VO")
@Data
public class PhotoCategoryCreateReqVO {

    @Schema(description = "分类名称", requiredMode = REQUIRED, example = "风景")
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 64, message = "分类名称长度不能超过64")
    private String name;

    @Schema(description = "分类颜色（十六进制，如：#FF5733）", example = "#FF5733")
    @Size(max = 16, message = "分类颜色长度不能超过16")
    private String color;

    @Schema(description = "分类图标（可选，如：camera、nature等）", example = "camera")
    @Size(max = 64, message = "分类图标长度不能超过64")
    private String icon;

    @Schema(description = "分类描述", example = "记录自然风景照片")
    @Size(max = 255, message = "分类描述长度不能超过255")
    private String description;

    @Schema(description = "排序顺序（数字越小越靠前）", example = "0")
    private Integer sortOrder;

}

