package com.interest.tracker.module.music.controller.app.vo;

import com.interest.tracker.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 专辑分页查询请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "专辑分页查询请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AlbumPageReqVO extends PageParam {

    @Schema(description = "用户ID（系统自动填充）", hidden = true)
    private Long userId;

    @Schema(description = "听歌状态：1-想听 2-在听 3-已听 4-弃听", example = "3")
    private Integer status;

    @Schema(description = "艺术家筛选", example = "The Beatles")
    private String artist;

    @Schema(description = "音乐类型筛选", example = "摇滚")
    private String genre;

    @Schema(description = "标签筛选", example = "经典")
    private String tag;

    @Schema(description = "关键词搜索（专辑名、艺术家）", example = "Abbey")
    private String keyword;

    @Schema(description = "开始听歌日期", example = "2025-01-01")
    private LocalDate startListenDate;

    @Schema(description = "结束听歌日期", example = "2025-12-31")
    private LocalDate endListenDate;

    @Schema(description = "排序字段：ctime-创建时间, listenDate-听歌日期, rating-评分", example = "ctime")
    private String sort;

}

