package com.interest.tracker.module.movie.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * TMDB 电影详情
 *
 * @author interest-tracker
 */
@Data
public class TMDBMovieDetail {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("original_title")
    private String originalTitle;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("vote_average")
    private Double voteAverage;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("runtime")
    private Integer runtime; // 时长（分钟）

    @JsonProperty("genres")
    private List<Genre> genres;

    @JsonProperty("credits")
    private Credits credits;

    /**
     * 类型
     */
    @Data
    public static class Genre {
        @JsonProperty("id")
        private Integer id;

        @JsonProperty("name")
        private String name;
    }

    /**
     * 演职员信息
     */
    @Data
    public static class Credits {
        @JsonProperty("crew")
        private List<CrewMember> crew;

        @JsonProperty("cast")
        private List<CastMember> cast;
    }

    /**
     * 工作人员
     */
    @Data
    public static class CrewMember {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("job")
        private String job; // "Director", "Producer" 等
    }

    /**
     * 演员
     */
    @Data
    public static class CastMember {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("character")
        private String character; // 角色名

        @JsonProperty("order")
        private Integer order; // 排序
    }

}

