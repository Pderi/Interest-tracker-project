package com.interest.tracker.module.movie.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * TMDB 搜索响应
 *
 * @author interest-tracker
 */
@Data
public class TMDBSearchResponse {

    @JsonProperty("page")
    private Integer page;

    @JsonProperty("total_pages")
    private Integer totalPages;

    @JsonProperty("total_results")
    private Integer totalResults;

    @JsonProperty("results")
    private List<TMDBSearchResult> results;

    /**
     * TMDB 搜索结果项
     */
    @Data
    public static class TMDBSearchResult {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("media_type")
        private String mediaType; // "movie" 或 "tv"

        @JsonProperty("title")
        private String title; // 电影标题

        @JsonProperty("name")
        private String name; // 电视剧标题

        @JsonProperty("original_title")
        private String originalTitle; // 电影原始标题

        @JsonProperty("original_name")
        private String originalName; // 电视剧原始标题

        @JsonProperty("overview")
        private String overview; // 简介

        @JsonProperty("release_date")
        private String releaseDate; // 电影上映日期

        @JsonProperty("first_air_date")
        private String firstAirDate; // 电视剧首播日期

        @JsonProperty("vote_average")
        private Double voteAverage; // 评分

        @JsonProperty("poster_path")
        private String posterPath; // 海报路径

        @JsonProperty("backdrop_path")
        private String backdropPath; // 背景图路径
    }

}

