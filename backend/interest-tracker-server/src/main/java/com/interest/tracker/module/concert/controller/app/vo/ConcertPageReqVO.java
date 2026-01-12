package com.interest.tracker.module.concert.controller.app.vo;

import com.interest.tracker.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 演唱会分页查询请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "演唱会分页查询请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ConcertPageReqVO extends PageParam {

    @Schema(description = "用户ID（系统自动填充）", hidden = true)
    private Long userId;

    @Schema(description = "观演状态：1-想看 2-已看", example = "2")
    private Integer status;

    @Schema(description = "艺术家筛选", example = "The Beatles")
    private String artist;

    @Schema(description = "城市筛选", example = "北京")
    private String city;

    @Schema(description = "演出类型筛选：1-演唱会 2-音乐节 3-演出 4-其他", example = "1")
    private Integer concertType;

    @Schema(description = "标签筛选", example = "摇滚")
    private String tag;

    @Schema(description = "关键词搜索（演出名称、艺术家）", example = "Abbey")
    private String keyword;

    @Schema(description = "开始观演日期", example = "2025-01-01")
    private LocalDate startWatchDate;

    @Schema(description = "结束观演日期", example = "2025-12-31")
    private LocalDate endWatchDate;

    @Schema(description = "排序字段：ctime-创建时间, watchDate-观演日期, rating-评分", example = "ctime")
    private String sort;

}

