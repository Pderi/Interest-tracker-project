package com.interest.tracker.module.music.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * 创建专辑记录请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "创建专辑记录请求 VO")
@Data
public class AlbumCreateReqVO {

    @Schema(description = "专辑名称", requiredMode = REQUIRED, example = "Abbey Road")
    @NotBlank(message = "专辑名称不能为空")
    private String title;

    @Schema(description = "艺术家", requiredMode = REQUIRED, example = "The Beatles")
    @NotBlank(message = "艺术家不能为空")
    private String artist;

    @Schema(description = "发行年份", example = "1969")
    private Integer releaseYear;

    @Schema(description = "音乐类型（摇滚、流行等，逗号分隔）", example = "摇滚,流行")
    private String genre;

    @Schema(description = "专辑简介", example = "经典专辑...")
    private String description;

    @Schema(description = "封面URL", example = "https://example.com/cover.jpg")
    private String coverUrl;

    @Schema(description = "总曲目数", example = "17")
    private Integer totalTracks;

    @Schema(description = "总时长（秒）", example = "2556")
    private Integer duration;

    @Schema(description = "听歌状态：1-想听 2-在听 3-已听 4-弃听", example = "1")
    private Integer listenStatus;

    @Schema(description = "个人评分（0-10）", example = "8.5")
    private BigDecimal personalRating;

    @Schema(description = "标签（逗号分隔）", example = "摇滚,经典")
    private String tags;

    @Schema(description = "评价", example = "很好听")
    private String comment;

}

