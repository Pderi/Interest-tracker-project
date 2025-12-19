package com.interest.tracker.module.movie.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * TMDB搜索响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "TMDB搜索响应 VO")
@Data
public class MovieSearchRespVO {

    @Schema(description = "TMDB ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "550")
    private Long tmdbId;

    @Schema(description = "标题", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "搏击俱乐部")
    private String title;

    @Schema(description = "原始标题", example = "Fight Club")
    private String originalTitle;

    @Schema(description = "类型：1-电影 2-电视剧", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Integer type;

    @Schema(description = "上映年份", example = "1999")
    private Integer releaseYear;

    @Schema(description = "海报URL", example = "https://image.tmdb.org/t/p/w500/xxx.jpg")
    private String posterUrl;

    @Schema(description = "简介", example = "简介...")
    private String overview;

    @Schema(description = "评分", example = "8.4")
    private BigDecimal rating;

}

