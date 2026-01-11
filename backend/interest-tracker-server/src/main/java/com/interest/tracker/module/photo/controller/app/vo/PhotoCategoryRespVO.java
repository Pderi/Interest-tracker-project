package com.interest.tracker.module.photo.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 照片分类响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "照片分类响应 VO")
@Data
public class PhotoCategoryRespVO {

    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "风景")
    private String name;

    @Schema(description = "分类颜色（十六进制，如：#FF5733）", example = "#FF5733")
    private String color;

    @Schema(description = "分类图标（可选，如：camera、nature等）", example = "camera")
    private String icon;

    @Schema(description = "分类描述", example = "记录自然风景照片")
    private String description;

    @Schema(description = "排序顺序（数字越小越靠前）", example = "0")
    private Integer sortOrder;

    @Schema(description = "该分类下的照片数量", example = "10")
    private Integer photoCount;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

}

