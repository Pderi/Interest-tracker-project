package com.interest.tracker.module.photo.controller.app.vo;

import com.interest.tracker.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 照片分页查询请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "照片分页查询请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class PhotoPageReqVO extends PageParam {

    @Schema(description = "用户ID（系统自动填充）", hidden = true)
    private Long userId;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "旅游记录ID（用于筛选关联到特定旅游记录的照片）", example = "1")
    private Long travelRecordId;

    @Schema(description = "观演记录ID（用于筛选关联到特定观演记录的照片）", example = "1")
    private Long concertRecordId;

    @Schema(description = "是否只查询未关联的照片", example = "false")
    private Boolean unlinkedOnly;

    @Schema(description = "拍摄时间范围（开始）", example = "2025-01-01T00:00:00")
    private LocalDateTime shootTimeStart;

    @Schema(description = "拍摄时间范围（结束）", example = "2025-12-31T23:59:59")
    private LocalDateTime shootTimeEnd;

    @Schema(description = "关键词搜索（标题、描述、标签）", example = "风景")
    private String keyword;

}

