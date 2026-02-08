package com.interest.tracker.module.book.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 图书详情响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "图书详情响应 VO")
@Data
public class BookRespVO {

    @Schema(description = "书籍信息")
    private BookInfo book;

    @Schema(description = "阅读记录")
    private RecordInfo record;

    /**
     * 书籍信息
     */
    @Data
    @Schema(description = "书籍信息")
    public static class BookInfo {
        @Schema(description = "书籍ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "书名", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "百年孤独")
        private String title;

        @Schema(description = "作者（逗号分隔）", example = "加西亚·马尔克斯")
        private String author;

        @Schema(description = "出版社", example = "南海出版公司")
        private String publisher;

        @Schema(description = "出版年份", example = "2011")
        private Integer publishYear;

        @Schema(description = "ISBN", example = "9787544253994")
        private String isbn;

        @Schema(description = "类型（小说、历史等，逗号分隔）", example = "小说,魔幻现实主义")
        private String genre;

        @Schema(description = "简介", example = "简介...")
        private String description;

        @Schema(description = "封面URL", example = "https://example.com/cover.jpg")
        private String coverUrl;

        @Schema(description = "页数", example = "360")
        private Integer pageCount;

        @Schema(description = "语言", example = "中文")
        private String language;
    }

    /**
     * 阅读记录信息
     */
    @Data
    @Schema(description = "阅读记录信息")
    public static class RecordInfo {
        @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

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

        @Schema(description = "评价", example = "很经典")
        private String comment;

        @Schema(description = "标签列表", example = "[\"经典\",\"文学\"]")
        private List<String> tags;
    }

}

