package com.interest.tracker.module.movie.client;

import com.interest.tracker.module.movie.client.dto.TMDBMovieDetail;
import com.interest.tracker.module.movie.client.dto.TMDBSearchResponse;
import com.interest.tracker.module.movie.client.dto.TMDBTVDetail;
import com.interest.tracker.module.movie.config.TMDBProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * TMDB 客户端
 *
 * @author interest-tracker
 */
@Component
@Slf4j
public class TMDBClient {

    @Resource
    private TMDBProperties tmdbProperties;

    @Resource
    private RestTemplate restTemplate;

    /**
     * 搜索影视
     *
     * @param keyword 搜索关键词
     * @param page 页码（从1开始）
     * @return 搜索结果
     */
    public TMDBSearchResponse search(String keyword, Integer page) {
        validateAuthConfig();

        try {
            String url = tmdbProperties.getBaseUrl() + "/search/multi" +
                    "?query=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) +
                    "&language=" + tmdbProperties.getLanguage() +
                    "&page=" + (page != null ? page : 1);

            log.debug("调用 TMDB 搜索接口: {} (认证方式: {})", url, tmdbProperties.getAuthMethod());
            TMDBSearchResponse response = executeRequest(url, TMDBSearchResponse.class);
            log.debug("TMDB 搜索返回结果数: {}", response != null && response.getResults() != null ? response.getResults().size() : 0);
            return response;
        } catch (RestClientException e) {
            log.error("TMDB 搜索失败: keyword={}, page={}", keyword, page, e);
            throw new RuntimeException("TMDB API 调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取电影详情
     *
     * @param tmdbId TMDB ID
     * @return 电影详情
     */
    public TMDBMovieDetail getMovieDetail(Long tmdbId) {
        validateAuthConfig();

        try {
            String url = tmdbProperties.getBaseUrl() + "/movie/" + tmdbId +
                    "?language=" + tmdbProperties.getLanguage() +
                    "&append_to_response=credits";

            log.debug("调用 TMDB 电影详情接口: {} (认证方式: {})", url, tmdbProperties.getAuthMethod());
            TMDBMovieDetail detail = executeRequest(url, TMDBMovieDetail.class);
            log.debug("TMDB 电影详情获取成功: tmdbId={}, title={}", tmdbId, detail != null ? detail.getTitle() : null);
            return detail;
        } catch (RestClientException e) {
            log.error("TMDB 获取电影详情失败: tmdbId={}", tmdbId, e);
            throw new RuntimeException("TMDB API 调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取电视剧详情
     *
     * @param tmdbId TMDB ID
     * @return 电视剧详情
     */
    public TMDBTVDetail getTVDetail(Long tmdbId) {
        validateAuthConfig();

        try {
            String url = tmdbProperties.getBaseUrl() + "/tv/" + tmdbId +
                    "?language=" + tmdbProperties.getLanguage() +
                    "&append_to_response=credits";

            log.debug("调用 TMDB 电视剧详情接口: {} (认证方式: {})", url, tmdbProperties.getAuthMethod());
            TMDBTVDetail detail = executeRequest(url, TMDBTVDetail.class);
            log.debug("TMDB 电视剧详情获取成功: tmdbId={}, name={}", tmdbId, detail != null ? detail.getName() : null);
            return detail;
        } catch (RestClientException e) {
            log.error("TMDB 获取电视剧详情失败: tmdbId={}", tmdbId, e);
            throw new RuntimeException("TMDB API 调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 校验认证配置
     */
    private void validateAuthConfig() {
        if (tmdbProperties.useTokenAuth()) {
            if (tmdbProperties.getReadAccessToken() == null || tmdbProperties.getReadAccessToken().isEmpty()) {
                log.warn("TMDB Read Access Token 未配置");
                throw new RuntimeException("TMDB Read Access Token 未配置，请参考文档配置：https://www.themoviedb.org/settings/api");
            }
        } else {
            if (tmdbProperties.getKey() == null || tmdbProperties.getKey().isEmpty()) {
                log.warn("TMDB API Key 未配置");
                throw new RuntimeException("TMDB API Key 未配置，请参考文档配置：https://www.themoviedb.org/settings/api");
            }
        }
    }

    /**
     * 执行 HTTP 请求（支持 API Key 和 Bearer Token 两种方式）
     */
    private <T> T executeRequest(String url, Class<T> responseType) {
        if (tmdbProperties.useTokenAuth()) {
            // 使用 Bearer Token 认证
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(tmdbProperties.getReadAccessToken());
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
            return response.getBody();
        } else {
            // 使用 API Key 认证（作为查询参数）
            String urlWithKey = url + (url.contains("?") ? "&" : "?") + "api_key=" + tmdbProperties.getKey();
            return restTemplate.getForObject(urlWithKey, responseType);
        }
    }

    /**
     * 构建图片完整URL
     *
     * @param imagePath 图片路径（如：/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg）
     * @return 完整URL
     */
    public String buildImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        // 如果已经是完整URL，直接返回
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath;
        }
        // 如果路径不以 / 开头，添加 /
        if (!imagePath.startsWith("/")) {
            imagePath = "/" + imagePath;
        }
        return tmdbProperties.getImageBaseUrl() + imagePath;
    }

}

