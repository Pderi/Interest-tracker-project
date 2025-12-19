package com.interest.tracker.module.movie.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * TMDB API 配置属性
 *
 * @author interest-tracker
 */
@Data
@Component
@ConfigurationProperties(prefix = "tmdb.api")
public class TMDBProperties {

    /**
     * TMDB API Key（v3 auth）
     * 获取方式：https://www.themoviedb.org/settings/api
     * 使用方式：作为查询参数 ?api_key=YOUR_API_KEY
     * 
     * 注意：如果配置了 readAccessToken，则优先使用 Token 方式认证
     */
    private String key = "";

    /**
     * TMDB Read Access Token（可选，推荐）
     * 获取方式：https://www.themoviedb.org/settings/api
     * 使用方式：作为 Bearer Token 在 Authorization 头中发送
     * 
     * 优势：
     * - 更安全（不会出现在 URL 中）
     * - 可在 v3 和 v4 API 中通用
     * 
     * 如果配置了此字段，将优先使用 Token 认证方式
     */
    private String readAccessToken = "";

    /**
     * TMDB API 基础URL
     */
    private String baseUrl = "https://api.themoviedb.org/3";

    /**
     * TMDB 图片基础URL
     */
    private String imageBaseUrl = "https://image.tmdb.org/t/p/w500";

    /**
     * 默认语言
     */
    private String language = "zh-CN";

    /**
     * 请求超时时间（毫秒）
     */
    private Integer timeout = 5000;

    /**
     * 是否使用 Token 认证
     */
    public boolean useTokenAuth() {
        return readAccessToken != null && !readAccessToken.isEmpty();
    }

    /**
     * 获取认证方式（用于日志）
     */
    public String getAuthMethod() {
        return useTokenAuth() ? "Bearer Token" : "API Key";
    }

}

