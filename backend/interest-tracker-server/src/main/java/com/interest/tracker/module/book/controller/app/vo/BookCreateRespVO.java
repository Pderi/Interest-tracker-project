package com.interest.tracker.module.book.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 创建图书记录响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "创建图书记录响应 VO")
@Data
public class BookCreateRespVO {

    @Schema(description = "书籍ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long bookId;

    @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long recordId;

}

