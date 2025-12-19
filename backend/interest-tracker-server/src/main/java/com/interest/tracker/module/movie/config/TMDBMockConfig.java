package com.interest.tracker.module.movie.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * TMDB Mock 配置（用于网络无法访问 TMDB 时的开发测试）
 *
 * @author interest-tracker
 */
@Data
@Component
@ConfigurationProperties(prefix = "tmdb.mock")
public class TMDBMockConfig {

    /**
     * 是否启用 Mock 模式
     * 当网络无法访问 TMDB API 时，可以启用此模式使用模拟数据
     */
    private Boolean enabled = false;

}

