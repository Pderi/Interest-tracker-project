package com.interest.tracker.module.movie.client.provider;

import com.interest.tracker.module.movie.client.TMDBClient;
import com.interest.tracker.module.movie.client.dto.TMDBMovieDetail;
import com.interest.tracker.module.movie.client.dto.TMDBSearchResponse;
import com.interest.tracker.module.movie.client.dto.TMDBTVDetail;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * TMDB 数据提供者实现
 *
 * @author interest-tracker
 */
@Component
@Slf4j
public class TMDBProvider implements MovieDataProvider {

    @Resource
    private TMDBClient tmdbClient;

    @Override
    public TMDBSearchResponse search(String keyword, Integer page) {
        try {
            return tmdbClient.search(keyword, page);
        } catch (Exception e) {
            log.warn("TMDB 搜索失败: keyword={}, page={}, error={}", keyword, page, e.getMessage());
            throw new RuntimeException("TMDB API 调用失败: " + e.getMessage(), e);
        }
    }

    @Override
    public TMDBMovieDetail getMovieDetail(Long tmdbId) {
        try {
            return tmdbClient.getMovieDetail(tmdbId);
        } catch (Exception e) {
            log.warn("TMDB 获取电影详情失败: tmdbId={}, error={}", tmdbId, e.getMessage());
            throw new RuntimeException("TMDB API 调用失败: " + e.getMessage(), e);
        }
    }

    @Override
    public TMDBTVDetail getTVDetail(Long tmdbId) {
        try {
            return tmdbClient.getTVDetail(tmdbId);
        } catch (Exception e) {
            log.warn("TMDB 获取电视剧详情失败: tmdbId={}, error={}", tmdbId, e.getMessage());
            throw new RuntimeException("TMDB API 调用失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProviderName() {
        return "TMDB";
    }

    @Override
    public boolean isAvailable() {
        // 简单检查：尝试访问 TMDB API（可以优化为健康检查）
        return true; // 默认认为可用，实际可用性在调用时判断
    }

}

