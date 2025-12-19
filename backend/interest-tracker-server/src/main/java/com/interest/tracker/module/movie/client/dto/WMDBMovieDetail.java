package com.interest.tracker.module.movie.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * WMDB 影片详情
 */
@Data
public class WMDBMovieDetail {

    private Integer id;

    @JsonProperty("doubanId")
    private Integer doubanId;

    @JsonProperty("imdbId")
    private String imdbId;

    private String name;

    @JsonProperty("nameOriginal")
    private String nameOriginal;

    private String poster;

    private Integer year;

    @JsonProperty("imdbRating")
    private Double imdbRating;

    @JsonProperty("doubanRating")
    private Double doubanRating;

    /**
     * Movie / TV / Anime 等
     */
    private String type;

    /**
     * 简介
     */
    private String plot;

    /**
     * 演员
     */
    private List<String> actors;

    /**
     * 导演
     */
    private List<String> directors;

    /**
     * 类型/标签
     */
    private List<String> genres;

    /**
     * 集数（电视剧）
     */
    private Integer episodes;
}

