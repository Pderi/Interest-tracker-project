package com.interest.tracker.module.dashboard.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 时间线项响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "时间线项响应 VO")
@Data
public class TimelineItemRespVO {

    @Schema(description = "活动ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "活动类型：photo-照片 movie-影视 music-音乐 book-阅读 travel-旅游 concert-演唱会 match-球赛", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @Schema(description = "活动标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "活动描述")
    private String description;

    @Schema(description = "封面图片URL")
    private String cover;

    @Schema(description = "活动时间（用于排序和展示）", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime activityTime;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "关联的详情ID（用于跳转）")
    private Long detailId;

    @Schema(description = "关联的记录ID（用于跳转）")
    private Long recordId;

    @Schema(description = "类型特定的元数据（JSON格式）")
    private String metadata;

}

