package com.interest.tracker.module.movie.client.provider;

import com.interest.tracker.module.movie.client.dto.TMDBMovieDetail;
import com.interest.tracker.module.movie.client.dto.TMDBSearchResponse;
import com.interest.tracker.module.movie.client.dto.TMDBTVDetail;

/**
 * 影视数据提供者接口
 * 统一不同数据源的接口，支持 TMDB 和国内 API 的切换
 *
 * @author interest-tracker
 */
public interface MovieDataProvider {

    /**
     * 搜索影视
     *
     * @param keyword 搜索关键词
     * @param page 页码（从1开始）
     * @return 搜索结果
     */
    TMDBSearchResponse search(String keyword, Integer page);

    /**
     * 获取电影详情
     *
     * @param tmdbId 影视ID（不同数据源可能使用不同的ID字段名，但统一使用tmdbId作为参数名）
     * @return 电影详情
     */
    TMDBMovieDetail getMovieDetail(Long tmdbId);

    /**
     * 获取电视剧详情
     *
     * @param tmdbId 影视ID
     * @return 电视剧详情
     */
    TMDBTVDetail getTVDetail(Long tmdbId);

    /**
     * 获取提供者名称（用于日志和错误提示）
     *
     * @return 提供者名称
     */
    String getProviderName();

    /**
     * 检查提供者是否可用
     *
     * @return true 如果可用
     */
    boolean isAvailable();

}

