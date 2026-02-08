package com.interest.tracker.module.dashboard.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 最近活动响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "最近活动响应 VO")
@Data
public class RecentActivityRespVO {

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

    @Schema(description = "活动时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime activityTime;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "关联的详情ID（用于跳转）")
    private Long detailId;

    @Schema(description = "关联的记录ID（用于跳转）")
    private Long recordId;

    @Schema(description = "评分（0-10）")
    private java.math.BigDecimal rating;

    @Schema(description = "状态（影视：1-想看 2-在看 3-已看 4-弃剧；音乐：1-想听 2-在听 3-已听 4-弃听；阅读：1-想读 2-在读 3-已读 4-弃读；旅游：1-想去 2-计划中 3-已去；演唱会：1-想看 2-已看）")
    private Integer status;

    @Schema(description = "副标题（如艺术家、作者、地点等）")
    private String subtitle;

}

