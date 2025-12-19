package com.interest.tracker.module.movie.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * WMDB 搜索响应（搜索接口返回数组，这里封装为对象方便处理）
 */
@Data
public class WMDBSearchResponse {

    /**
     * 搜索结果列表
     */
    private List<WMDBItem> items = Collections.emptyList();

    /**
     * 总数（WMDB 未提供总数，默认 items.size）
     */
    private Integer total;

    @Data
    public static class WMDBItem {
        private Integer id;

        @JsonProperty("doubanId")
        private Integer doubanId;

        @JsonProperty("imdbId")
        private String imdbId;

        private String name;

        @JsonProperty("nameOriginal")
        private String nameOriginal;

        /**
         * AKA/别名
         */
        @JsonProperty("aka")
        private List<String> aliases;

        /**
         * 海报（完整 URL）
         */
        private String poster;

        private Integer year;

        @JsonProperty("imdbRating")
        private Double imdbRating;

        @JsonProperty("doubanRating")
        private Double doubanRating;

        /**
         * 类型：Movie / TV / Anime 等
         */
        private String type;
    }
}

