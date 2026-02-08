package com.interest.tracker.module.book.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * 创建图书记录请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "创建图书记录请求 VO")
@Data
public class BookCreateReqVO {

    @Schema(description = "书名", requiredMode = REQUIRED, example = "百年孤独")
    @NotBlank(message = "书名不能为空")
    private String title;

    @Schema(description = "作者（逗号分隔）", example = "加西亚·马尔克斯")
    private String author;

    @Schema(description = "类型（小说、历史等，逗号分隔）", example = "小说,魔幻现实主义")
    private String genre;

    @Schema(description = "简介", example = "简介...")
    private String description;

    @Schema(description = "封面URL", example = "https://example.com/cover.jpg")
    private String coverUrl;

    @Schema(description = "阅读状态：1-想读 2-在读 3-已读 4-弃读", example = "1")
    /**
     * 阅读状态：1-想读 2-在读 3-已读 4-弃读
     * 枚举 {@link com.interest.tracker.module.book.enums.ReadStatusEnum}
     */
    private Integer readStatus;

    @Schema(description = "个人评分（0-10）", example = "9.0")
    private BigDecimal personalRating;

    @Schema(description = "评价", example = "很经典")
    private String comment;

    @Schema(description = "标签列表", example = "[\"经典\",\"文学\"]")
    private List<String> tags;

}

