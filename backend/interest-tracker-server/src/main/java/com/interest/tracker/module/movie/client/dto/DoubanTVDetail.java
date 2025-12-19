package com.interest.tracker.module.movie.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 豆瓣电视剧详情
 *
 * @author interest-tracker
 */
@Data
public class DoubanTVDetail {

    @JsonProperty("id")
    private String id; // 豆瓣 ID

    @JsonProperty("title")
    private String title;

    @JsonProperty("original_title")
    private String originalTitle;

    @JsonProperty("summary")
    private String summary; // 简介

    @JsonProperty("year")
    private String year;

    @JsonProperty("rating")
    private DoubanSearchResponse.DoubanSearchResult.DoubanRating rating;

    @JsonProperty("images")
    private DoubanSearchResponse.DoubanSearchResult.DoubanImages images;

    @JsonProperty("directors")
    private List<DoubanSearchResponse.DoubanSearchResult.DoubanPerson> directors;

    @JsonProperty("casts")
    private List<DoubanSearchResponse.DoubanSearchResult.DoubanPerson> casts;

    @JsonProperty("genres")
    private List<String> genres;

    @JsonProperty("episodes_count")
    private Integer episodesCount; // 集数

    @JsonProperty("countries")
    private List<String> countries; // 制片国家

}

