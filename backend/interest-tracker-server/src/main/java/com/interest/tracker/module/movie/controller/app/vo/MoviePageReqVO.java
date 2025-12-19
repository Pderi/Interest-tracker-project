package com.interest.tracker.module.movie.controller.app.vo;

import com.interest.tracker.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 影视分页查询请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "影视分页查询请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MoviePageReqVO extends PageParam {

    @Schema(description = "用户ID（系统自动填充）", hidden = true)
    private Long userId;

    @Schema(description = "观看状态：1-想看 2-在看 3-已看 4-弃剧", example = "3")
    private Integer watchStatus;

    @Schema(description = "类型：1-电影 2-电视剧", example = "1")
    private Integer type;

    @Schema(description = "标签筛选", example = "动作")
    private String tag;

    @Schema(description = "关键词搜索（标题）", example = "搏击")
    private String keyword;

    @Schema(description = "开始观看日期", example = "2025-01-01")
    private LocalDate startWatchDate;

    @Schema(description = "结束观看日期", example = "2025-12-31")
    private LocalDate endWatchDate;

    @Schema(description = "排序字段：ctime-创建时间, watchDate-观看日期, rating-评分", example = "ctime")
    private String sort;

}

