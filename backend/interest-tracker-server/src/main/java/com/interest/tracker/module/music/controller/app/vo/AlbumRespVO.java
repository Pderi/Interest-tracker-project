package com.interest.tracker.module.music.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 专辑详情响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "专辑详情响应 VO")
@Data
public class AlbumRespVO {

    @Schema(description = "专辑信息")
    private AlbumInfo album;

    @Schema(description = "听歌记录")
    private RecordInfo record;

    /**
     * 专辑信息
     */
    @Data
    @Schema(description = "专辑信息")
    public static class AlbumInfo {
        @Schema(description = "专辑ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "专辑名称", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "Abbey Road")
        private String title;

        @Schema(description = "艺术家", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "The Beatles")
        private String artist;

        @Schema(description = "发行年份", example = "1969")
        private Integer releaseYear;

        @Schema(description = "音乐类型（摇滚、流行等，逗号分隔）", example = "摇滚,流行")
        private String genre;

        @Schema(description = "专辑简介", example = "简介...")
        private String description;

        @Schema(description = "封面URL", example = "https://example.com/cover.jpg")
        private String coverUrl;

        @Schema(description = "总曲目数", example = "17")
        private Integer totalTracks;

        @Schema(description = "总时长（秒）", example = "2556")
        private Integer duration;
    }

    /**
     * 听歌记录信息
     */
    @Data
    @Schema(description = "听歌记录信息")
    public static class RecordInfo {
        @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "听歌状态：1-想听 2-在听 3-已听 4-弃听", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "3")
        private Integer listenStatus;

        @Schema(description = "个人评分（0-10）", example = "8.5")
        private BigDecimal personalRating;

        @Schema(description = "听歌日期", example = "2025-01-15")
        private LocalDate listenDate;

        @Schema(description = "听歌次数", example = "10")
        private Integer listenCount;

        @Schema(description = "评价", example = "很好听")
        private String comment;

        @Schema(description = "标签列表", example = "[\"摇滚\",\"经典\"]")
        private List<String> tags;
    }

}

