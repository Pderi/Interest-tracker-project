package com.interest.tracker.module.movie.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 豆瓣搜索响应
 *
 * @author interest-tracker
 */
@Data
public class DoubanSearchResponse {

    @JsonProperty("count")
    private Integer count;

    @JsonProperty("start")
    private Integer start;

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("subjects")
    private List<DoubanSearchResult> subjects;

    /**
     * 豆瓣搜索结果项
     */
    @Data
    public static class DoubanSearchResult {
        @JsonProperty("id")
        private String id; // 豆瓣 ID（字符串格式）

        @JsonProperty("title")
        private String title; // 标题

        @JsonProperty("original_title")
        private String originalTitle; // 原始标题

        @JsonProperty("subtype")
        private String subtype; // "movie" 或 "tv"

        @JsonProperty("year")
        private String year; // 年份

        @JsonProperty("rating")
        private DoubanRating rating; // 评分

        @JsonProperty("images")
        private DoubanImages images; // 图片

        @JsonProperty("directors")
        private List<DoubanPerson> directors; // 导演

        @JsonProperty("casts")
        private List<DoubanPerson> casts; // 演员

        @JsonProperty("genres")
        private List<String> genres; // 类型

        /**
         * 评分信息
         */
        @Data
        public static class DoubanRating {
            @JsonProperty("average")
            private Double average; // 平均分（0-10）

            @JsonProperty("numRaters")
            private Integer numRaters; // 评分人数
        }

        /**
         * 图片信息
         */
        @Data
        public static class DoubanImages {
            @JsonProperty("small")
            private String small; // 小图

            @JsonProperty("medium")
            private String medium; // 中图

            @JsonProperty("large")
            private String large; // 大图
        }

        /**
         * 人员信息（导演/演员）
         */
        @Data
        public static class DoubanPerson {
            @JsonProperty("id")
            private String id;

            @JsonProperty("name")
            private String name;

            @JsonProperty("avatars")
            private DoubanImages avatars; // 头像
        }
    }

}

