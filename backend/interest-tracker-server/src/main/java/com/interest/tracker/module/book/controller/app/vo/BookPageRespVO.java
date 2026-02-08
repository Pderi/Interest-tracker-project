package com.interest.tracker.module.book.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 图书分页列表响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "图书分页列表响应 VO")
@Data
public class BookPageRespVO {

    @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long recordId;

    @Schema(description = "书籍ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long bookId;

    @Schema(description = "书名", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "百年孤独")
    private String title;

    @Schema(description = "作者（逗号分隔）", example = "加西亚·马尔克斯")
    private String author;

    @Schema(description = "类型（小说、历史等，逗号分隔）", example = "小说,魔幻现实主义")
    private String genre;

    @Schema(description = "封面URL", example = "https://example.com/cover.jpg")
    private String coverUrl;

    /**
     * 阅读状态：1-想读 2-在读 3-已读 4-弃读
     * 枚举 {@link com.interest.tracker.module.book.enums.ReadStatusEnum}
     */
    @Schema(description = "阅读状态：1-想读 2-在读 3-已读 4-弃读", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "3")
    private Integer readStatus;

    @Schema(description = "个人评分（0-10）", example = "9.0")
    private BigDecimal personalRating;

    @Schema(description = "阅读日期", example = "2025-01-15")
    private LocalDate readDate;

    @Schema(description = "阅读进度（0-100）", example = "100.0")
    private BigDecimal readProgress;

    @Schema(description = "标签列表", example = "[\"经典\",\"文学\"]")
    private List<String> tags;

    @Schema(description = "评价", example = "很经典")
    private String comment;

    @Schema(description = "创建时间", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

