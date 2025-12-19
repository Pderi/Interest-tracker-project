package com.interest.tracker.module.movie.client;

import com.interest.tracker.module.movie.client.dto.TMDBMovieDetail;
import com.interest.tracker.module.movie.client.dto.TMDBSearchResponse;
import com.interest.tracker.module.movie.client.dto.TMDBTVDetail;
import com.interest.tracker.module.movie.client.provider.DomesticProvider;
import com.interest.tracker.module.movie.client.provider.MovieDataProvider;
import com.interest.tracker.module.movie.client.provider.TMDBProvider;
import com.interest.tracker.module.movie.client.util.MovieIdConverter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 影视数据服务
 * 实现自动切换和降级逻辑：优先使用 TMDB，失败时自动切换到国内 API
 *
 * @author interest-tracker
 */
@Service
@Slf4j
public class MovieDataService {

    @Resource
    private TMDBProvider tmdbProvider;

    @Resource
    private DomesticProvider domesticProvider;

    @Resource
    private TMDBClient tmdbClient;

    /**
     * 搜索影视（自动切换）
     *
     * @param keyword 搜索关键词
     * @param page 页码
     * @return 搜索结果
     */
    public TMDBSearchResponse search(String keyword, Integer page) {
        List<MovieDataProvider> providers = getProviders();
        
        for (MovieDataProvider provider : providers) {
            if (!provider.isAvailable()) {
                log.debug("跳过不可用的提供者: {}", provider.getProviderName());
                continue;
            }

            try {
                log.debug("尝试使用 {} 搜索: keyword={}, page={}", provider.getProviderName(), keyword, page);
                TMDBSearchResponse response = provider.search(keyword, page);
                log.info("使用 {} 搜索成功: keyword={}, page={}, results={}", 
                        provider.getProviderName(), keyword, page, 
                        response != null && response.getResults() != null ? response.getResults().size() : 0);
                return response;
            } catch (Exception e) {
                log.warn("{} 搜索失败，尝试下一个提供者: keyword={}, page={}, error={}", 
                        provider.getProviderName(), keyword, page, e.getMessage());
                // 继续尝试下一个提供者
            }
        }

        // 所有提供者都失败
        throw new RuntimeException("所有数据源都不可用，无法搜索影视");
    }

    /**
     * 获取电影详情（自动切换）
     *
     * @param tmdbId 影视ID（可能是 TMDB ID 或豆瓣 ID）
     * @return 电影详情
     */
    public TMDBMovieDetail getMovieDetail(Long tmdbId) {
        // 根据 ID 类型确定优先使用的数据源
        List<MovieDataProvider> providers = getProvidersForDetail(tmdbId);
        
        for (MovieDataProvider provider : providers) {
            if (!provider.isAvailable()) {
                log.debug("跳过不可用的提供者: {}", provider.getProviderName());
                continue;
            }

            try {
                log.debug("尝试使用 {} 获取电影详情: tmdbId={}", provider.getProviderName(), tmdbId);
                TMDBMovieDetail detail = provider.getMovieDetail(tmdbId);
                log.info("使用 {} 获取电影详情成功: tmdbId={}, title={}", 
                        provider.getProviderName(), tmdbId, detail != null ? detail.getTitle() : null);
                return detail;
            } catch (Exception e) {
                log.warn("{} 获取电影详情失败，尝试下一个提供者: tmdbId={}, error={}", 
                        provider.getProviderName(), tmdbId, e.getMessage());
                // 继续尝试下一个提供者
            }
        }

        throw new RuntimeException("所有数据源都不可用，无法获取电影详情");
    }

    /**
     * 获取电视剧详情（自动切换）
     *
     * @param tmdbId 影视ID（可能是 TMDB ID 或豆瓣 ID）
     * @return 电视剧详情
     */
    public TMDBTVDetail getTVDetail(Long tmdbId) {
        // 根据 ID 类型确定优先使用的数据源
        List<MovieDataProvider> providers = getProvidersForDetail(tmdbId);
        
        for (MovieDataProvider provider : providers) {
            if (!provider.isAvailable()) {
                log.debug("跳过不可用的提供者: {}", provider.getProviderName());
                continue;
            }

            try {
                log.debug("尝试使用 {} 获取电视剧详情: tmdbId={}", provider.getProviderName(), tmdbId);
                TMDBTVDetail detail = provider.getTVDetail(tmdbId);
                log.info("使用 {} 获取电视剧详情成功: tmdbId={}, name={}", 
                        provider.getProviderName(), tmdbId, detail != null ? detail.getName() : null);
                return detail;
            } catch (Exception e) {
                log.warn("{} 获取电视剧详情失败，尝试下一个提供者: tmdbId={}, error={}", 
                        provider.getProviderName(), tmdbId, e.getMessage());
                // 继续尝试下一个提供者
            }
        }

        throw new RuntimeException("所有数据源都不可用，无法获取电视剧详情");
    }

    /**
     * 获取提供者列表（按优先级排序）
     * 优先级：豆瓣 API > TMDB（国内服务器友好）
     *
     * @return 提供者列表
     */
    private List<MovieDataProvider> getProviders() {
        List<MovieDataProvider> providers = new ArrayList<>();
        
        // 优先使用豆瓣 API（国内服务器友好，访问速度快）
        if (domesticProvider.isAvailable()) {
            providers.add(domesticProvider);
        }
        
        // TMDB 作为备选（数据质量更高，但可能需要 VPN）
        providers.add(tmdbProvider);
        
        return providers;
    }

    /**
     * 根据 ID 类型获取提供者列表（用于获取详情）
     * 如果 ID 是豆瓣 ID，优先使用豆瓣 API；否则也优先使用豆瓣（国内服务器友好）
     *
     * @param id 影视ID
     * @return 提供者列表
     */
    private List<MovieDataProvider> getProvidersForDetail(Long id) {
        List<MovieDataProvider> providers = new ArrayList<>();
        
        // 如果 ID 是豆瓣 ID，优先使用豆瓣 API
        if (MovieIdConverter.isDoubanId(id)) {
            if (domesticProvider.isAvailable()) {
                providers.add(domesticProvider);
            }
            providers.add(tmdbProvider);
        } else {
            // 即使不是豆瓣 ID，也优先尝试豆瓣（国内服务器友好）
            // 如果豆瓣无法处理，再尝试 TMDB
            if (domesticProvider.isAvailable()) {
                providers.add(domesticProvider);
            }
            providers.add(tmdbProvider);
        }
        
        return providers;
    }

    /**
     * 构建图片完整URL（使用 TMDB 的图片服务）
     * 注意：如果使用国内 API，可能需要调整图片 URL 的构建逻辑
     *
     * @param imagePath 图片路径
     * @return 完整URL
     */
    public String buildImageUrl(String imagePath) {
        // 当前使用 TMDB 的图片服务
        // 如果后续使用国内 API，可能需要根据当前使用的提供者来构建 URL
        return tmdbClient.buildImageUrl(imagePath);
    }

}

