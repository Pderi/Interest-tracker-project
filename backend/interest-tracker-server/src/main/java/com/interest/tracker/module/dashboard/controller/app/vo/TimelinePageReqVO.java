package com.interest.tracker.module.dashboard.controller.app.vo;

import com.interest.tracker.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 时间线分页请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "时间线分页请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TimelinePageReqVO extends PageParam {

    @Schema(description = "类型筛选：photo-照片 movie-影视 music-音乐 book-阅读 travel-旅游 concert-演唱会 match-球赛，多个用逗号分隔")
    private String types;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "标签筛选（关键词）")
    private String tag;

    @Schema(description = "关键词搜索（标题、描述）")
    private String keyword;

}

