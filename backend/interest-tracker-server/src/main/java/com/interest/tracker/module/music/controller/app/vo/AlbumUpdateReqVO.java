package com.interest.tracker.module.music.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新专辑信息请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "更新专辑信息请求 VO")
@Data
public class AlbumUpdateReqVO {

    @Schema(description = "专辑ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "专辑名称", example = "Abbey Road")
    private String title;

    @Schema(description = "艺术家/乐队", example = "The Beatles")
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

}

