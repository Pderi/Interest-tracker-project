package com.interest.tracker.module.book.controller.app.vo;

import com.interest.tracker.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 图书分页查询请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "图书分页查询请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class BookPageReqVO extends PageParam {

    @Schema(description = "用户ID（系统自动填充）", hidden = true)
    private Long userId;

    /**
     * 阅读状态：1-想读 2-在读 3-已读 4-弃读
     * 枚举 {@link com.interest.tracker.module.book.enums.ReadStatusEnum}
     */
    @Schema(description = "阅读状态：1-想读 2-在读 3-已读 4-弃读", example = "3")
    private Integer readStatus;

    @Schema(description = "标签筛选", example = "经典")
    private String tag;

    @Schema(description = "关键词搜索（书名）", example = "百年")
    private String keyword;

    @Schema(description = "作者筛选", example = "马尔克斯")
    private String author;

    @Schema(description = "开始阅读日期", example = "2025-01-01")
    private LocalDate startReadDate;

    @Schema(description = "结束阅读日期", example = "2025-12-31")
    private LocalDate endReadDate;

    @Schema(description = "排序字段：ctime-创建时间, readDate-阅读日期, rating-评分", example = "ctime")
    private String sort;

}

