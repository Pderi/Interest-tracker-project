package com.interest.tracker.module.movie.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 国内 API 配置属性
 *
 * @author interest-tracker
 */
@Data
@Component
@ConfigurationProperties(prefix = "domestic.api")
public class DomesticApiProperties {

    /**
     * 是否启用国内 API（作为备选方案）
     */
    private Boolean enabled = false;

    /**
     * API 类型（wmdb, douban, 等）
     */
    private String type = "wmdb";

    /**
     * API 基础URL
     */
    private String baseUrl = "https://api.wmdb.tv";

    /**
     * API Key（如果需要）
     */
    private String apiKey = "";

}

