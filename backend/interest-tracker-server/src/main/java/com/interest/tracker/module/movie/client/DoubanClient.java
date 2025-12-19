package com.interest.tracker.module.movie.client;

import com.interest.tracker.module.movie.client.dto.DoubanMovieDetail;
import com.interest.tracker.module.movie.client.dto.DoubanSearchResponse;
import com.interest.tracker.module.movie.client.dto.DoubanTVDetail;
import com.interest.tracker.module.movie.config.DomesticApiProperties;
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
 * 豆瓣 API 客户端
 * 
 * 注意：豆瓣官方 API 已停止申请，这里使用非官方 API 接口
 * 使用前请确保遵守相关法律法规和使用条款
 *
 * @author interest-tracker
 */
@Component
@Slf4j
public class DoubanClient {

    @Resource
    private DomesticApiProperties domesticApiProperties;

    @Resource
    private RestTemplate restTemplate;

    /**
     * 豆瓣 API 基础URL
     * 注意：这是非官方 API，可能随时失效
     */
    private static final String DOUBAN_BASE_URL = "https://frodo.douban.com/api/v2";

    /**
     * 搜索影视
     *
     * @param keyword 搜索关键词
     * @param page 页码（从0开始，豆瓣 API 使用 start 参数）
     * @return 搜索结果
     */
    public DoubanSearchResponse search(String keyword, Integer page) {
        try {
            // 豆瓣 API 使用 start 参数，每页 20 条
            int start = (page != null && page > 0 ? page - 1 : 0) * 20;
            
            // 搜索电影
            String movieUrl = DOUBAN_BASE_URL + "/search/movie" +
                    "?q=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) +
                    "&start=" + start +
                    "&count=20";

            log.debug("调用豆瓣搜索接口: {}", movieUrl);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<DoubanSearchResponse> response = restTemplate.exchange(
                    movieUrl, HttpMethod.GET, entity, DoubanSearchResponse.class);
            
            DoubanSearchResponse result = response.getBody();
            if (result == null) {
                result = new DoubanSearchResponse();
                result.setSubjects(new java.util.ArrayList<>());
                result.setTotal(0);
            }
            if (result.getSubjects() == null) {
                result.setSubjects(new java.util.ArrayList<>());
            }
            
            // 搜索电视剧
            try {
                String tvUrl = DOUBAN_BASE_URL + "/search/tv" +
                        "?q=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) +
                        "&start=" + start +
                        "&count=20";
                
                ResponseEntity<DoubanSearchResponse> tvResponse = restTemplate.exchange(
                        tvUrl, HttpMethod.GET, entity, DoubanSearchResponse.class);
                
                DoubanSearchResponse tvResult = tvResponse.getBody();
                
                // 合并电影和电视剧结果
                if (tvResult != null && tvResult.getSubjects() != null && !tvResult.getSubjects().isEmpty()) {
                    // 标记电视剧类型
                    tvResult.getSubjects().forEach(subject -> subject.setSubtype("tv"));
                    result.getSubjects().addAll(tvResult.getSubjects());
                    // 更新总数
                    int total = (result.getTotal() != null ? result.getTotal() : 0) + 
                               (tvResult.getTotal() != null ? tvResult.getTotal() : 0);
                    result.setTotal(total);
                }
            } catch (Exception e) {
                log.warn("豆瓣搜索电视剧失败，仅返回电影结果: {}", e.getMessage());
                // 继续使用电影结果
            }
            
            log.debug("豆瓣搜索返回结果数: {}", result.getSubjects().size());
            return result;
        } catch (RestClientException e) {
            log.error("豆瓣搜索失败: keyword={}, page={}", keyword, page, e);
            throw new RuntimeException("豆瓣 API 调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取电影详情
     *
     * @param doubanId 豆瓣 ID（字符串格式）
     * @return 电影详情
     */
    public DoubanMovieDetail getMovieDetail(String doubanId) {
        try {
            String url = DOUBAN_BASE_URL + "/movie/" + doubanId;
            
            log.debug("调用豆瓣电影详情接口: {}", url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<DoubanMovieDetail> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, DoubanMovieDetail.class);
            
            log.debug("豆瓣电影详情获取成功: doubanId={}, title={}", 
                    doubanId, response.getBody() != null ? response.getBody().getTitle() : null);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("豆瓣获取电影详情失败: doubanId={}", doubanId, e);
            throw new RuntimeException("豆瓣 API 调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取电视剧详情
     *
     * @param doubanId 豆瓣 ID（字符串格式）
     * @return 电视剧详情
     */
    public DoubanTVDetail getTVDetail(String doubanId) {
        try {
            String url = DOUBAN_BASE_URL + "/tv/" + doubanId;
            
            log.debug("调用豆瓣电视剧详情接口: {}", url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<DoubanTVDetail> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, DoubanTVDetail.class);
            
            log.debug("豆瓣电视剧详情获取成功: doubanId={}, name={}", 
                    doubanId, response.getBody() != null ? response.getBody().getTitle() : null);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("豆瓣获取电视剧详情失败: doubanId={}", doubanId, e);
            throw new RuntimeException("豆瓣 API 调用失败: " + e.getMessage(), e);
        }
    }

}

